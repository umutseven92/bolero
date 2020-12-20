package com.bolero.game.screens;

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
import com.bolero.game.MapValues;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.controllers.MapController;
import com.bolero.game.controllers.NPCController;
import com.bolero.game.drawers.DebugDrawer;
import com.bolero.game.drawers.DialogDrawer;
import com.bolero.game.enums.CharacterState;
import com.bolero.game.exceptions.MissingInteractionTypeException;
import com.bolero.game.exceptions.MissingSpawnTypeException;
import com.bolero.game.exceptions.WrongInteractionTypeException;
import com.bolero.game.icons.ButtonIcon;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.InteractionRectangle;
import com.bolero.game.interactions.SpawnRectangle;
import com.bolero.game.mappers.CollisionMapper;
import com.bolero.game.mappers.InteractionMapper;

import java.util.ArrayList;

public abstract class GameScreen implements Screen {
    private final BoleroGame game;

    private final World world;

    private final GameCamera gameCamera;
    private final TiledMap map;

    private final Player player;
    private final ButtonIcon eButtonIcon;

    private final float UNIT = 16f;
    private final MapValues mapValues;

    private final CollisionMapper collisionMapper;
    private final InteractionMapper interactionMapper;

    private final DebugDrawer debugDrawer;
    private final DialogDrawer dialogDrawer;
    private Vector2 playerSpawnPosition;
    private float accumulator = 0;

    private final NPCController npcController;
    private final BundleController bundleController;
    private final MapController mapController;

    public GameScreen(BoleroGame game, String mapPath) throws WrongInteractionTypeException, MissingInteractionTypeException, MissingSpawnTypeException {
        this.game = game;

        map = new TmxMapLoader().load(mapPath);
        mapValues = new MapValues(map);

        mapController = new MapController(map, UNIT);
        setPlayerSpawnPoint(game.SPAWN_INITIAL_OBJ);

        gameCamera = new GameCamera();
        world = new World(Vector2.Zero, true);

        player = new Player(playerSpawnPosition, world);
        eButtonIcon = new ButtonIcon(player);

        gameCamera.updatePosition(player.getPosition(), UNIT, mapValues);

        collisionMapper = new CollisionMapper(world, map);
        collisionMapper.createCollisions(UNIT, mapValues, game.COL_LAYER);

        interactionMapper = new InteractionMapper(map);
        interactionMapper.createInteractionMap(game.INT_LAYER, game.SPAWN_INITIAL_OBJ);

        debugDrawer = new DebugDrawer(UNIT, gameCamera.getCamera());
        dialogDrawer = new DialogDrawer();
        npcController = new NPCController(map);
        npcController.spawnNPCs(game.SPAWN_LAYER, UNIT, world);
        bundleController = new BundleController();
    }


    public void setPlayerSpawnPoint(String name) {
        MapObject playerSpawnObject = map.getLayers().get(game.SPAWN_LAYER).getObjects().get(name);

        final MapProperties props = playerSpawnObject.getProperties();

        playerSpawnPosition = new Vector2((float) props.get("x") / UNIT, (float) props.get("y") / UNIT);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpawnRectangle spawnRectangle = checkIfInInteractionTriangle();
        InspectRectangle inspectRectangle = checkIfInInspectTriangle();
        NPC npc = checkIfNearNPC();

        if (player.getState() != CharacterState.inspecting) {
            handleMovementInput();
        }

        player.setPosition();
        npcController.setPositions();
        gameCamera.update(player.getPosition(), UNIT, mapValues, delta);

        mapController.setView(gameCamera.getCamera());
        mapController.drawBackground();

        if (game.debugMode) {
            debugDrawer.drawInteractionZones(interactionMapper.getAllRectangles(), npcController.getNpcs());
        }

        game.batch.setProjectionMatrix(gameCamera.getCamera().combined);
        game.batch.begin();
        player.draw(game.batch);
        npcController.drawNPCs(game.batch);

        if ((spawnRectangle != null || inspectRectangle != null || npc != null) && player.getState() != CharacterState.inspecting) {
            eButtonIcon.draw(game.batch);
        }

        game.batch.end();

        mapController.drawForeground();
        drawHUD();

        if (player.getState() == CharacterState.inspecting && inspectRectangle != null) {
            drawInspection(inspectRectangle);
        }

        handleInteractionInput(spawnRectangle, inspectRectangle, npc);
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
            accumulator -= TIME_STEP;
        }
    }

    private void drawHUD() {
        game.hudBatch.begin();
        if (game.debugMode) {
            debugDrawer.drawDebugInfo(game.font, game.hudBatch, player, game.currentScreen, gameCamera.getCamera().zoom);
        }
        game.hudBatch.end();
    }


    private void handleMovementInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            game.debugMode = !game.debugMode;
        }

        if (game.debugMode) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                respawnPlayer();
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

    private void handleInteractionInput(SpawnRectangle spawnRectangle, InspectRectangle inspectRectangle, NPC npc) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && spawnRectangle != null) {
            handleInteraction(spawnRectangle);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E) && inspectRectangle != null) {
            inspect();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E) && npc != null) {
            talkToNPC(npc);
        }
    }

    private void handleInteraction(SpawnRectangle rectangle) {
        game.loadRoute(rectangle.getName(), rectangle.getSpawnName());
    }

    private void inspect() {
        if (player.getState() != CharacterState.inspecting) {
            gameCamera.zoomIn(0.2f);
            player.setState(CharacterState.inspecting);
        } else {
            gameCamera.zoomOut(0.2f);
            player.setState(CharacterState.idle);
        }
    }

    private void drawInspection(InspectRectangle rectangle) {
        String text = bundleController.getBundle().get(rectangle.getStringID());
        game.hudBatch.begin();
        dialogDrawer.draw(game.hudBatch, text);
        game.hudBatch.end();
    }

    private void talkToNPC(NPC npc) {
        player.setState(CharacterState.talking);
    }

    private SpawnRectangle checkIfInInteractionTriangle() {
        return checkIfInTriangle(interactionMapper.getSpawnRectangles());
    }

    private InspectRectangle checkIfInInspectTriangle() {
        return checkIfInTriangle(interactionMapper.getInspectRectangles());
    }

    private <E extends InteractionRectangle> E checkIfInTriangle(ArrayList<E> rectangles) {
        Vector2 playerPosPixels = new Vector2(player.getPosition().x * UNIT, player.getPosition().y * UNIT);
        for (E intRectangle : rectangles) {

            if (intRectangle.getRectangle().contains(playerPosPixels)) {
                return intRectangle;
            }
        }

        return null;
    }

    private NPC checkIfNearNPC() {
        Vector2 playerPosPixels = new Vector2(player.getPosition().x, player.getPosition().y);
        for (NPC npc : npcController.getNpcs()) {

            if (npc.getTalkCircle().contains(playerPosPixels)) {
                return npc;
            }
        }

        return null;

    }

    private void respawnPlayer() {
        player.respawn(playerSpawnPosition);
    }

    @Override
    public void show() {

    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        world.dispose();
        player.dispose();
        collisionMapper.dispose();
        mapController.dispose();
        debugDrawer.dispose();
        eButtonIcon.dispose();
        npcController.dispose();
        dialogDrawer.dispose();
    }
}
