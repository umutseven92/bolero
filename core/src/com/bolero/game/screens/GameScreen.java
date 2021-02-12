package com.bolero.game.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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
import com.bolero.game.enums.CharacterState;
import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.exceptions.MapperException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;
import com.bolero.game.icons.ButtonIcon;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import java.io.FileNotFoundException;

public class GameScreen implements Screen {
  private final BoleroGame game;
  private final String name;
  private World world;

  private GameCamera gameCamera;
  private TiledMap map;

  private Player player;
  private ButtonIcon eButtonIcon;

  private MapValues mapValues;

  private CollisionController collisionController;
  private InteractionController interactionController;
  private LightController lightController;

  private DebugDrawer debugDrawer;
  private DialogDrawer dialogDrawer;
  private InspectDrawer inspectDrawer;
  private Vector2 playerSpawnPosition;
  private float accumulator = 0;

  private NPCController npcController;
  private MapController mapController;
  private RayHandler rayHandler;

  private Sun sun;
  private float darkenAmount;

  private final String mapPath;
  private final String spawnPos;

  public GameScreen(BoleroGame game, String name, String mapPath, String spawnPos)
      throws MapperException, FileNotFoundException, FileFormatException {
    this.game = game;
    this.name = name;

    this.mapPath = mapPath;
    this.spawnPos = spawnPos;
    initializeAll();
  }

  private void initializeMap() {
    Gdx.app.log(GameScreen.class.getName(), "Initializing map..");

    map = new TmxMapLoader().load(mapPath);
    mapValues = new MapValues(map);

    mapController = new MapController(map);
    mapController.load();
  }

  private void initializeCollision() throws MissingPropertyException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing collisions..");

    world = new World(Vector2.Zero, true);

    collisionController = new CollisionController(world, map);
    collisionController.load(mapValues);
  }

  private void initializeInteractions() throws MissingPropertyException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing interactions..");

    interactionController = new InteractionController(map);
    interactionController.load();
  }

  private void initializeLights(boolean force) throws MissingPropertyException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing lights..");

    rayHandler = new RayHandler(world);

    rayHandler.setBlurNum(3);

    darkenAmount = 0f;
    sun = new Sun(rayHandler, game.clock);
    sun.update(darkenAmount);

    lightController = new LightController(map, rayHandler, game.clock);
    lightController.load();

    lightController.update(force);
  }

  private void initializeNPCs()
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException,
          FileFormatException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing NPCs..");

    npcController = new NPCController(map, game.getBundleController());
    npcController.load(world);
  }

  private void initializeCamera() {
    Gdx.app.log(GameScreen.class.getName(), "Initializing camera..");

    gameCamera = new GameCamera();
    gameCamera.updatePosition(player.getPosition(), mapValues);
  }

  private void initializePlayer() throws FileNotFoundException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing player..");

    setPlayerSpawnPoint(spawnPos);

    player = new Player(playerSpawnPosition, world);
    eButtonIcon = new ButtonIcon(player);
  }

  private void initializeDrawers() {
    Gdx.app.log(GameScreen.class.getName(), "Initializing drawers..");

    debugDrawer = new DebugDrawer(gameCamera.getCamera());
    inspectDrawer = new InspectDrawer();
    dialogDrawer = new DialogDrawer(player, gameCamera.getCamera());
  }

  private void initializeAll()
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException,
          FileFormatException {
    initializeMap();
    initializeCollision();
    initializeInteractions();
    initializeLights(false);
    initializeNPCs();
    initializePlayer();
    initializeCamera();
    initializeDrawers();
  }

  private void reInitialize() throws MissingPropertyException {
    initializeMap();
    initializeInteractions();
    initializeLights(true);
  }

  private void setPlayerSpawnPoint(String name) {
    MapObject playerSpawnObject =
        map.getLayers().get(BoleroGame.SPAWN_LAYER).getObjects().get(name);

    final MapProperties props = playerSpawnObject.getProperties();

    playerSpawnPosition =
        new Vector2(
            (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    Vector2 playerPos = new Vector2(player.getPosition().x, player.getPosition().y);
    Vector2 playerPosPixels =
        new Vector2(
            player.getPosition().x * BoleroGame.UNIT, player.getPosition().y * BoleroGame.UNIT);

    TransitionRectangle transitionRectangle =
        interactionController.checkIfInInteractionRectangle(playerPosPixels);
    InspectRectangle inspectRectangle =
        interactionController.checkIfInInspectRectangle(playerPosPixels);

    npcController.checkSchedules(game.clock);
    NPC npc = npcController.checkIfNearNPC(playerPos);

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

    mapController.setView(gameCamera.getCamera());
    mapController.drawBackground();

    if (game.debugMode) {
      debugDrawer.drawInteractionZones(
          interactionController.getAllRectangles(), npcController.getNpcs());
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
      eButtonIcon.draw(game.batch);
    }

    game.batch.end();

    mapController.drawForeground();

    rayHandler.setCombinedMatrix(gameCamera.getCamera());
    rayHandler.updateAndRender();

    drawHUD();

    if (player.getState() == CharacterState.inspecting && inspectRectangle != null) {
      drawInspection(inspectRectangle);
    } else if (player.getState() == CharacterState.talking) {
      drawDialog();
    }

    handleInteractionInput(transitionRectangle, inspectRectangle, npc);
    if (game.debugMode) {
      debugDrawer.drawBox2DBodies(world);
    }

    doPhysicsStep(delta);
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
      sun.update(darkenAmount);
      lightController.update();
      accumulator -= TIME_STEP;
    }
  }

  private void drawHUD() {
    game.hudBatch.begin();
    if (game.debugMode) {
      debugDrawer.drawDebugInfo(
          game.font,
          game.hudBatch,
          player,
          game.currentScreen.name,
          gameCamera.getCamera().zoom,
          game.clock);
    }
    game.hudBatch.end();
  }

  private void handleMovementInput() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
      game.debugMode = !game.debugMode;
    }

    if (game.debugMode) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
        reloadMap();
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
        gameCamera.zoomOutInstant(0.3f);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
        gameCamera.zoomInInstant(0.3f);
      }
    }

    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      this.player.applyLeftMovement();
    }

    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      this.player.applyRightMovement();
    }

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      this.player.applyUpMovement();
    }

    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      this.player.applyDownMovement();
    }

    if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
      this.player.stopXMovement();
    }

    if (!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
      this.player.stopYMovement();
    }
  }

  private void handleInteractionInput(
      TransitionRectangle transitionRectangle, InspectRectangle inspectRectangle, NPC npc) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.E) && transitionRectangle != null) {
      handleTransition(transitionRectangle);
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.E) && inspectRectangle != null) {
      inspect();
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.E) && npc != null && npc.hasDialog()) {
      talkToNPC(npc);
    }
  }

  private void handleTransition(TransitionRectangle rectangle) {
    game.loadRoute(rectangle.getName(), rectangle.getSpawnName());
  }

  private void inspect() {
    if (player.getState() != CharacterState.inspecting) {
      gameCamera.zoomIn(0.2f);
      player.startInspecting();
    } else {
      gameCamera.zoomOut(0.2f);
      player.stopInspecting();
    }
  }

  private void drawInspection(InspectRectangle rectangle) {
    String text = game.getBundleController().getString(rectangle.getStringID());
    game.hudBatch.begin();
    inspectDrawer.draw(game.hudBatch, text);
    game.hudBatch.end();
  }

  private void talkToNPC(NPC npc) {
    if (dialogDrawer.isActivated()) {
      return;
    }

    if (player.getState() != CharacterState.talking) {
      darkenAmount = 0.2f;
      dialogDrawer.activate(npc);
      gameCamera.zoomIn(0.2f);
      player.startTalking();
      npc.startTalking();
    } else {
      darkenAmount = 0f;
      gameCamera.zoomOut(0.2f);
      player.stopTalking();
      npc.stopTalking();
    }
  }

  private void drawDialog() {
    dialogDrawer.drawCharacters();

    game.hudBatch.begin();
    dialogDrawer.drawUI(game.hudBatch);
    game.hudBatch.end();
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
  public void show() {}

  @Override
  public void resize(int width, int height) {}

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
    eButtonIcon.dispose();
    npcController.dispose();
    inspectDrawer.dispose();
    lightController.dispose();
    rayHandler.dispose();
  }
}
