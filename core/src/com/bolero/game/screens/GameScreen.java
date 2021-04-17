package com.bolero.game.screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.bolero.game.sprite_elements.AbstractSpriteElement;
import com.bolero.game.sprite_elements.Sparkle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public class GameScreen implements Screen {
  private final BoleroGame game;
  private final String mapName;
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
  private float physicsAccumulator = 0;

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
  private final boolean muted;

  private boolean disposing;
  private List<AbstractSpriteElement> spriteElements;

  public GameScreen(BoleroGame game, String mapName, String mapPath, String spawnPos)
      throws MapperException, FileNotFoundException, ConfigurationNotLoadedException {
    this.game = game;
    this.mapName = mapName;

    this.mapPath = mapPath;
    this.spawnPos = spawnPos;
    this.keys = BoleroGame.getConfig().getKeys();
    this.paused = false;
    this.darken = false;
    this.muted = true;
    this.disposing = false;
    darkenRenderer = new ShapeRenderer();
    initializeAll();
    mapController.playMusic();
  }

  private void initializeMap() throws FileNotFoundException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing map..");

    map = new TmxMapLoader().load(mapPath);
    mapValues = new MapValues(map);

    mapController = new MapController(map, mapName);
    mapController.load();
    mapController.setMusicVolume(muted ? 0f : 1f);
  }

  // Depends on initializeMap
  private void initializeCollision()
      throws MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing collisions..");

    world = new World(Vector2.Zero, true);

    collisionController = new CollisionController(world, map);
    collisionController.load(mapValues);
  }

  // Depends on initializeMap
  private void initializePaths() throws FileNotFoundException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing paths..");

    val mapper = new PathMapper(map);
    this.pathNodes = mapper.map();
  }

  // Depends on initializeMap
  private void initializeInteractions()
      throws MissingPropertyException, ConfigurationNotLoadedException, FileNotFoundException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing interactions..");

    interactionController = new InteractionController(map);
    interactionController.load();
  }

  // Depends on initializeCollision, initializeMap
  private void initializeLights(boolean force)
      throws MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing sun & lights..");

    rayHandler = new RayHandler(world);

    rayHandler.setBlurNum(3);

    sun = new Sun(rayHandler, game.getClock(), BoleroGame.getConfig().getSun());
    sun.update();

    lightController = new LightController(map, rayHandler, sun);
    lightController.load();

    lightController.update(force);
  }

  // Depends on initializeCollision, initializeMap
  private void initializeNPCs()
      throws FileNotFoundException, MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing NPCs..");

    npcController = new NPCController(map, game.getBundleManager(), pathNodes, game.getClock());
    npcController.load(world);
  }

  // Depends on initializePlayer
  private void initializeCamera() {
    Gdx.app.log(GameScreen.class.getName(), "Initializing camera..");

    gameCamera = new GameCamera(player.getPosition());
    gameCamera.updateCameraPosition(player.getPosition(), mapValues);
  }

  // Depends on initializeCollision, initializeMap
  private void initializePlayer() throws FileNotFoundException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing player..");

    val mapper = new PlayerMapper(map, world, spawnPos);
    player = mapper.map();

    interactIcon = new InteractIcon(player);
  }

  // Depends on initializeCamera, initializePlayer
  private void initializeDrawers() throws ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing drawers..");

    debugDrawer = new DebugDrawer(gameCamera.getCamera());
    inspectDrawer = new InspectDrawer(game.getBundleManager());
    dialogDrawer = new DialogDrawer(player, game.getBundleManager());
    pauseDrawer = new PauseDrawer(game.getBundleManager());
  }

  // Depends on initializeInteractions
  private void initializeSpriteElements() throws FileNotFoundException {
    Gdx.app.log(GameScreen.class.getName(), "Initializing sprite elements..");
    spriteElements = new ArrayList<>();

    // Initialize sparkles for interactions
    for (val interact : interactionController.getAllRectangles()) {
      if (interact.getHidden()) {
        // Do not sparkle hidden interaction rectangles.
        continue;
      }
      val sparkle = new Sparkle(interact.getOrigin());
      spriteElements.add(sparkle);
    }
  }

  // TODO: Parallelize these- watch out for order
  private void initializeAll()
      throws FileNotFoundException, MissingPropertyException, ConfigurationNotLoadedException {
    Gdx.app.log(GameScreen.class.getName(), String.format("Loading map %s..", this.mapName));

    initializeMap();
    initializeCollision();
    initializeInteractions();
    initializeLights(true);
    initializePaths();
    initializeNPCs();
    initializePlayer();
    initializeCamera();
    initializeDrawers();
    initializeSpriteElements();

    Gdx.app.log(GameScreen.class.getName(), String.format("Map %s loaded.", this.mapName));
  }

  private void reInitialize()
      throws MissingPropertyException, ConfigurationNotLoadedException, FileNotFoundException {
    initializeMap();
    initializeInteractions();
    initializeLights(true);
  }

  private void draw(boolean canInteract, boolean inspecting, boolean talking) {
    Gdx.gl.glClearColor(0, 0, 0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // 1. Draw background
    mapController.setView(gameCamera.getCamera());
    mapController.drawBackground();

    if (game.getDebugMode()) {
      // 2. Draw debug shapes (if in debug mode)
      debugDrawer.drawDebugShapes(
          interactionController.getAllRectangles(), npcController.getNpcs(), pathNodes);
    }

    game.getBatch().setProjectionMatrix(gameCamera.getCamera().combined);
    game.getBatch().begin();

    // 3. Draw sprite elements
    for (val element : spriteElements) {
      element.draw(game.getBatch());
    }

    // 4. Draw players
    player.draw(game.getBatch());

    // 5. Draw NPCs
    npcController.drawNPCs(game.getBatch());

    if (canInteract) {
      // 6. Draw interact button icon
      interactIcon.draw(game.getBatch());
    }

    game.getBatch().end();

    // 7. Draw foreground
    mapController.drawForeground();

    // 8. Draw collision shapes (if in debug mode)
    if (game.getDebugMode()) {
      debugDrawer.drawBox2DBodies(world);
    }

    // 9. Draw lights
    rayHandler.setCombinedMatrix(gameCamera.getCamera());
    rayHandler.updateAndRender();

    // 10. Dim the screen (if in menus)
    if (darken) {
      darkenScreen();
    }

    if (game.getDebugMode()) {
      // 11. Draw debug information (if in debug mode)
      debugDrawer.drawDebugInfo(player, mapName, gameCamera.getCamera().zoom, game.getClock());
    }

    // 12. Draw inspect or dialog menus (if inspecting or talking)
    if (inspecting) {
      inspectDrawer.draw();
    } else if (talking) {
      dialogDrawer.draw();
    }

    // 13. Draw pause menu
    if (paused) {
      pauseDrawer.draw();
    }
  }

  //  Method called by the game loop from the application every time rendering should be performed.
  // Game logic updates are usually also performed in this method.
  @Override
  public void render(float delta) {
    if (disposing) {
      return;
    }
    val playerPosPixels = player.getPosition().cpy().scl(BoleroGame.UNIT);
    val transitionRectangle = interactionController.checkIfInInteractionRectangle(playerPosPixels);
    val inspectRectangle = interactionController.checkIfInInspectRectangle(playerPosPixels);

    if (!paused) {
      gameStep(delta);
    } else {
      pauseDrawer.checkForInput();
    }

    NPC npc = npcController.checkIfNearNPC(player.getPosition());

    val canInteract =
        (transitionRectangle != null
                || inspectRectangle != null
                || (npc != null && npc.hasDialog()))
            && player.getState() != CharacterState.inspecting
            && player.getState() != CharacterState.talking;
    val inspecting = player.getState() == CharacterState.inspecting && inspectRectangle != null;
    val talking = player.getState() == CharacterState.talking;

    this.draw(canInteract, inspecting, talking);

    handlePauseInput();

    if (!paused) {
      handleInteractionInput(transitionRectangle, inspectRectangle, npc);
      physicsStep(delta);
    }
  }

  private void gameStep(float delta) {
    try {
      npcController.checkSchedules();
    } catch (Exception e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
    }

    if (player.getState() != CharacterState.inspecting
        && player.getState() != CharacterState.talking) {
      handleMovementInput();
    }

    if (player.getState() == CharacterState.talking) {
      dialogDrawer.checkForInput();
    }

    handleMiscInput();

    player.updatePosition();
    mapController.playWalkSound(player.getPosition(), delta);
    npcController.setPositions();
    gameCamera.update(player.getPosition(), mapValues, delta);
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

  private void physicsStep(float deltaTime) {
    // Fixed time step from https://gafferongames.com/post/fix_your_timestep/
    // Max frame time to avoid spiral of death on slow devices
    float frameTime = Math.min(deltaTime, 0.25f);
    physicsAccumulator += frameTime;
    float TIME_STEP = 1 / 16f;
    while (physicsAccumulator >= TIME_STEP) {
      world.step(TIME_STEP, 6, 2);
      game.getClock().increment();
      sun.update();
      lightController.update();
      physicsAccumulator -= TIME_STEP;
    }
  }

  private void handleMiscInput() {
    if (Gdx.input.isKeyJustPressed(keys.getDebugInput())) {
      game.setDebugMode(!game.getDebugMode());
    }

    if (game.getDebugMode()) {
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
    boolean pausePressed = Gdx.input.isKeyJustPressed(keys.getPauseInput());

    if (pausePressed || (interactionPressed && paused)) {
      pauseGame();
    }
  }

  private void handleTransition(TransitionRectangle rectangle) {
    game.playTransitionSound();
    loadRoute(rectangle.getMapName(), rectangle.getSpawnName());
  }

  private void loadRoute(String mapName, String spawnName) {
    game.loadRouteLoading(mapName, spawnName);
  }

  private void pauseGame() {
    if (pauseDrawer.isActivated()) {
      // Still paused.
      return;
    }

    mapController.toggleMusic();
    darken = !darken;
    paused = !paused;
  }

  private void inspect(InspectRectangle inspectRectangle) {
    // Inspect menus only have one input, and only one inspect menu can be active at one time,
    // so no need to check whether we are still inspecting.
    // Interact button can either open or close the inspect menu; no other interactions are
    // possible.
    darken = !darken;
    if (player.getState() != CharacterState.inspecting) {
      interactionController.playInspectSound(inspectRectangle);

      inspectDrawer.activate(inspectRectangle);
      gameCamera.zoomIn(0.2f);
      player.startInspecting();
    } else {
      gameCamera.zoomOut(0.2f);
      player.stopInspecting();
    }
  }

  private void talkToNPC(NPC npc) {
    if (dialogDrawer.isActivated()) {
      // Still in dialog.
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

  private void clearWorld() {}

  //  This method is called every time the game screen is re-sized and the game is not in the paused
  // state. It is also called once just after the create() method.
  //  The parameters are the new width and height the screen has been resized to in pixels.
  @Override
  public void resize(int width, int height) {
    gameCamera.setViewPort(width, height);
    inspectDrawer.init(width);
    debugDrawer.init(gameCamera.getCamera());
    dialogDrawer.init(width);
    pauseDrawer.init();
  }

  // Called when this screen becomes the current screen for a Game.
  @Override
  public void show() {}

  //  On Android this method is called when the Home button is pressed or an incoming call is
  // received.
  // On desktop this is called just before dispose() when exiting the application.
  //  A good place to save the game state.
  @Override
  public void pause() {
    disposing = true;
  }

  // This method is only called on Android, when the application resumes from a paused state.
  @Override
  public void resume() {}

  // Called when this screen is no longer the current screen for a Game.
  @Override
  public void hide() {}

  // Called when the application is destroyed. It is preceded by a call to pause().
  @Override
  public void dispose() {
    Gdx.app.debug(GameScreen.class.getName(), String.format("Disposing map %s..", mapName));

    interactIcon.dispose();
    dialogDrawer.dispose();
    pauseDrawer.dispose();
    debugDrawer.dispose();
    inspectDrawer.dispose();
    interactionController.dispose();
    player.dispose();
    mapController.dispose();
    npcController.dispose();
    lightController.dispose();
    collisionController.dispose();
    world.dispose();
    map.dispose();

    Gdx.app.debug(GameScreen.class.getName(), String.format("Disposed map %s.", mapName));
  }
}
