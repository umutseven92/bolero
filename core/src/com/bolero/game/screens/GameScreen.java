package com.bolero.game.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.GameCamera;
import com.bolero.game.Sun;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.controllers.CollisionController;
import com.bolero.game.controllers.InteractionController;
import com.bolero.game.controllers.LightController;
import com.bolero.game.controllers.MapController;
import com.bolero.game.controllers.NPCController;
import com.bolero.game.data.MapValues;
import com.bolero.game.drawers.DebugDrawer;
import com.bolero.game.drawers.DialogDrawer;
import com.bolero.game.drawers.InspectDrawer;
import com.bolero.game.drawers.PauseDrawer;
import com.bolero.game.dtos.KeysDTO;
import com.bolero.game.enums.CharacterState;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MapperException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.icons.InteractIcon;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import com.bolero.game.mappers.PathMapper;
import com.bolero.game.mappers.PlayerMapper;
import com.bolero.game.pathfinding.PathGraph;
import java.io.FileNotFoundException;
import lombok.val;

public class GameScreen implements Screen {
  private final BoleroGame game;
  private final String name;
  private World world;

  private GameCamera gameCamera;
  private TiledMap map;

  private Player player;
  private InteractIcon interactIcon;

  private MapValues mapValues;

  private CollisionController collisionController;
  private InteractionController interactionController;
  private LightController lightController;

  private final ShapeRenderer darkenRenderer;
  private DebugDrawer debugDrawer;
  private DialogDrawer dialogDrawer;
  private InspectDrawer inspectDrawer;
  private PauseDrawer pauseDrawer;
  private Vector2 playerSpawnPosition;
  private float accumulator = 0;

  private NPCController npcController;
  private MapController mapController;
  private RayHandler rayHandler;

  private Sun sun;

  private final String mapPath;
  private final String spawnPos;

  private final KeysDTO keys;

  private PathGraph pathNodes;
  private boolean paused;
  private boolean darken;

  public GameScreen(BoleroGame game, String name, String mapPath, String spawnPos)
      throws MapperException, FileNotFoundException, ConfigurationNotLoadedException {
    this.game = game;
    this.name = name;

    this.mapPath = mapPath;
    this.spawnPos = spawnPos;
    this.keys = BoleroGame.config.getConfig().getKeys();
    this.paused = false;
    this.darken = false;
    darkenRenderer = new ShapeRenderer();
    initializeAll();
  }

  private void initializeMap() {
    Gdx.app.log(GameScreen.class.getName(), "Initializing map..");

    map = new TmxMapLoader().load(mapPath);
    mapValues = new MapValues(map);

    mapController = new MapController(map);
    mapController.load();
  }

  private void initializeCollision()
      throws MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing collisions..");

    world = new World(Vector2.Zero, true);

    collisionController = new CollisionController(world, map);
    collisionController.load(mapValues);
  }

  private void initializePaths() throws FileNotFoundException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing paths..");

    val mapper = new PathMapper(map);
    this.pathNodes = mapper.map();
  }

  private void initializeInteractions()
      throws MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing interactions..");

    interactionController = new InteractionController(map);
    interactionController.load();
  }

  private void initializeLights(boolean force)
      throws MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing sun & lights..");

    rayHandler = new RayHandler(world);

    rayHandler.setBlurNum(3);

    sun = new Sun(rayHandler, game.clock, BoleroGame.config.getConfig().getSun());
    sun.update();

    lightController = new LightController(map, rayHandler, sun);
    lightController.load();

    lightController.update(force);
  }

  private void initializeNPCs()
      throws FileNotFoundException, MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing NPCs..");

    npcController = new NPCController(map, game.getBundleController(), pathNodes);
    npcController.load(world);
  }

  private void initializeCamera() {
    Gdx.app.log(GameScreen.class.getName(), "Initializing camera..");

    gameCamera = new GameCamera();
    gameCamera.updatePosition(player.getPosition(), mapValues);
  }

  private void initializePlayer() throws FileNotFoundException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing player..");

    setPlayerSpawnPoint(spawnPos);

    val mapper = new PlayerMapper(map, world, playerSpawnPosition);
    player = mapper.map();
    interactIcon = new InteractIcon(player);
  }

  private void initializeDrawers() throws ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing drawers..");

    debugDrawer = new DebugDrawer(gameCamera.getCamera());
    inspectDrawer = new InspectDrawer(game.getBundleController());
    dialogDrawer = new DialogDrawer(player, game.getBundleController());
    pauseDrawer = new PauseDrawer(game.getBundleController());
  }

  // TODO: Parallelize these- watch out for order
  private void initializeAll()
      throws FileNotFoundException, MissingPropertyException, ConfigurationNotLoadedException {
    initializeMap();
    initializeCollision();
    initializeInteractions();
    initializeLights(true);
    initializePaths();
    initializeNPCs();
    initializePlayer();
    initializeCamera();
    initializeDrawers();
  }

  private void reInitialize() throws MissingPropertyException, ConfigurationNotLoadedException {
    initializeMap();
    initializeInteractions();
    initializeLights(true);
  }

  private void setPlayerSpawnPoint(String name) throws ConfigurationNotLoadedException {
    MapObject playerSpawnObject =
        map.getLayers()
            .get(BoleroGame.config.getConfig().getMaps().getLayers().getSpawn())
            .getObjects()
            .get(name);

    final MapProperties props = playerSpawnObject.getProperties();

    playerSpawnPosition =
        new Vector2(
            (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    val playerPos = new Vector2(player.getPosition().x, player.getPosition().y);
    val playerPosPixels =
        new Vector2(
            player.getPosition().x * BoleroGame.UNIT, player.getPosition().y * BoleroGame.UNIT);

    val transitionRectangle = interactionController.checkIfInInteractionRectangle(playerPosPixels);
    val inspectRectangle = interactionController.checkIfInInspectRectangle(playerPosPixels);

    NPC npc = null;

    if (!paused) {

      try {
        npcController.checkSchedules(game.clock);
      } catch (Exception e) {
        Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
        e.printStackTrace();
        System.exit(1);
      }

      npc = npcController.checkIfNearNPC(playerPos);

      if (player.getState() != CharacterState.inspecting
          && player.getState() != CharacterState.talking) {
        handleMovementInput();
      }

      if (player.getState() == CharacterState.talking) {
        dialogDrawer.checkForInput();
      }

      player.setPosition();
      npcController.setPositions();
      gameCamera.update(player.getPosition(), mapValues, delta);
    } else {
      pauseDrawer.checkForInput();
    }

    handleMiscInput();

    mapController.setView(gameCamera.getCamera());
    mapController.drawBackground();

    if (game.debugMode) {
      debugDrawer.drawDebugShapes(
          interactionController.getAllRectangles(), npcController.getNpcs(), pathNodes);
    }

    game.batch.setProjectionMatrix(gameCamera.getCamera().combined);
    game.batch.begin();
    player.draw(game.batch);
    npcController.drawNPCs(game.batch);

    if ((transitionRectangle != null
            || inspectRectangle != null
            || (npc != null && npc.hasDialog()))
        && player.getState() != CharacterState.inspecting
        && player.getState() != CharacterState.talking) {
      interactIcon.draw(game.batch);
    }

    game.batch.end();

    mapController.drawForeground();

    rayHandler.setCombinedMatrix(gameCamera.getCamera());
    rayHandler.updateAndRender();

    drawHUD();

    if (darken) {
      // Darken screen before menus.
      darkenScreen();
    }

    if (player.getState() == CharacterState.inspecting && inspectRectangle != null) {
      drawInspection();
    } else if (player.getState() == CharacterState.talking) {
      drawDialog();
    }
    if (game.debugMode) {
      debugDrawer.drawBox2DBodies(world);
    }

    if (!paused) {
      handleInteractionInput(transitionRectangle, inspectRectangle, npc);
      doPhysicsStep(delta);
    } else {
      handlePauseInput();
    }
  }

  private void darkenScreen() {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    darkenRenderer.begin(ShapeRenderer.ShapeType.Filled);
    darkenRenderer.setColor(new Color(0, 0, 0, 0.5f));
    darkenRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    darkenRenderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  private void doPhysicsStep(float deltaTime) {
    // Fixed time step from https://gafferongames.com/post/fix_your_timestep/
    // Max frame time to avoid spiral of death on slow devices
    float frameTime = Math.min(deltaTime, 0.25f);
    accumulator += frameTime;
    float TIME_STEP = 1 / 16f;
    while (accumulator >= TIME_STEP) {
      world.step(TIME_STEP, 6, 2);
      game.clock.increment();
      sun.update();
      lightController.update();
      accumulator -= TIME_STEP;
    }
  }

  private void drawHUD() {
    if (game.debugMode) {
      debugDrawer.drawDebugInfo(
          player, game.currentScreen.name, gameCamera.getCamera().zoom, game.clock);
    }

    if (paused) {
      pauseDrawer.draw();
    }
  }

  private void handleMiscInput() {
    if (Gdx.input.isKeyJustPressed(keys.getDebugInput())) {
      game.debugMode = !game.debugMode;
    }

    if (game.debugMode) {
      if (Gdx.input.isKeyJustPressed(keys.getReloadInput())) {
        reloadMap();
      }

      if (Gdx.input.isKeyJustPressed(keys.getZoomInInput())) {
        gameCamera.zoomOutInstant(0.3f);
      }
      if (Gdx.input.isKeyJustPressed(keys.getZoomOutInput())) {
        gameCamera.zoomInInstant(0.3f);
      }
    }

    if (Gdx.input.isKeyJustPressed(keys.getPauseInput())) {
      paused = !paused;
      darken = !darken;
    }
  }

  private void handleMovementInput() {

    if (Gdx.input.isKeyPressed(keys.getLeftInput())) {
      this.player.applyLeftMovement();
    }

    if (Gdx.input.isKeyPressed(keys.getRightInput())) {
      this.player.applyRightMovement();
    }

    if (Gdx.input.isKeyPressed(keys.getUpInput())) {
      this.player.applyUpMovement();
    }

    if (Gdx.input.isKeyPressed(keys.getDownInput())) {
      this.player.applyDownMovement();
    }

    if (!Gdx.input.isKeyPressed(keys.getLeftInput())
        && !Gdx.input.isKeyPressed(keys.getRightInput())) {
      this.player.stopXMovement();
    }

    if (!Gdx.input.isKeyPressed(keys.getUpInput())
        && !Gdx.input.isKeyPressed(keys.getDownInput())) {
      this.player.stopYMovement();
    }
  }

  private void handleInteractionInput(
      TransitionRectangle transitionRectangle, InspectRectangle inspectRectangle, NPC npc) {
    boolean interactionPressed = Gdx.input.isKeyJustPressed(keys.getInteractInput());

    if (interactionPressed && transitionRectangle != null) {
      handleTransition(transitionRectangle);
    } else if (interactionPressed && inspectRectangle != null) {
      inspect(inspectRectangle);
    } else if (interactionPressed && npc != null && npc.hasDialog()) {
      talkToNPC(npc);
    }
  }

  private void handlePauseInput() {
    boolean interactionPressed = Gdx.input.isKeyJustPressed(keys.getInteractInput());
    if (interactionPressed && paused) {
      paused = false;
      darken = !darken;
    }
  }

  private void handleTransition(TransitionRectangle rectangle) {
    game.loadRoute(rectangle.getMapName(), rectangle.getSpawnName());
  }

  private void inspect(InspectRectangle inspectRectangle) {
    darken = !darken;
    if (player.getState() != CharacterState.inspecting) {
      inspectDrawer.activate(inspectRectangle);
      gameCamera.zoomIn(0.2f);
      player.startInspecting();
    } else {
      gameCamera.zoomOut(0.2f);
      player.stopInspecting();
    }
  }

  private void drawInspection() {
    inspectDrawer.draw();
  }

  private void talkToNPC(NPC npc) {
    if (dialogDrawer.isActivated()) {
      return;
    }

    darken = !darken;
    if (player.getState() != CharacterState.talking) {
      dialogDrawer.activate(npc);
      gameCamera.zoomIn(0.2f);
      player.startTalking();
      npc.startTalking();
    } else {
      gameCamera.zoomOut(0.2f);
      player.stopTalking();
      npc.stopTalking();
    }
  }

  private void drawDialog() {
    dialogDrawer.draw();
  }

  private void reloadMap() {
    Gdx.app.log(GameScreen.class.getName(), "Reloading map.");
    try {
      reInitialize();
    } catch (Exception e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void resize(int width, int height) {
    gameCamera.setViewPort(width, height);
    inspectDrawer.init(width);
    debugDrawer.init(gameCamera.getCamera());
    dialogDrawer.init(width);
    pauseDrawer.init();
  }

  @Override
  public void show() {}

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    map.dispose();
    world.dispose();
    player.dispose();
    collisionController.dispose();
    mapController.dispose();
    debugDrawer.dispose();
    interactIcon.dispose();
    npcController.dispose();
    inspectDrawer.dispose();
    lightController.dispose();
    rayHandler.dispose();
  }
}
