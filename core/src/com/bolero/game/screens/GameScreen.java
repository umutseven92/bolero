package com.bolero.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.*;
import com.bolero.game.drawers.DebugDrawer;
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
    private final OrthographicCamera camera;

    private final TiledMap map;
    private final int[] backgroundLayers;
    private final int[] foregroundLayers;

    private final Player player;
    private final ButtonIcon eButtonIcon;

    private final float UNIT = 16f;
    private final MapValues mapValues;

    private final OrthogonalTiledMapRenderer mapRenderer;

    private final CollisionMapper collisionMapper;
    private final InteractionMapper interactionMapper;

    private final DebugDrawer debugDrawer;
    private Vector2 playerSpawnPosition;
    private float accumulator = 0;

    private final NPCController npcController;


    public GameScreen(BoleroGame game, String mapPath, int[] backgroundLayers, int[] foregroundLayers) {
        this.game = game;
        this.foregroundLayers = foregroundLayers;
        this.backgroundLayers = backgroundLayers;
        map = new TmxMapLoader().load(mapPath);
        mapValues = new MapValues(map);

        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / UNIT);

        setPlayerSpawnPoint(game.SPAWN_INITIAL_OBJ);

        camera = new OrthographicCamera();

        camera.setToOrtho(false, 30, 20);

        camera.update();

        world = new World(Vector2.Zero, true);

        player = new Player(playerSpawnPosition, world);
        eButtonIcon = new ButtonIcon(player);

        handleCamera();

        collisionMapper = new CollisionMapper(world, map);
        collisionMapper.createCollisions(UNIT, mapValues, game.COL_LAYER);

        interactionMapper = new InteractionMapper(map);
        interactionMapper.createInteractionMap(game.INT_LAYER, game.SPAWN_INITIAL_OBJ);

        debugDrawer = new DebugDrawer(UNIT, camera);

        npcController = new NPCController(map);
        npcController.spawnNPCs(game.SPAWN_LAYER, UNIT, world);
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

        handleInput(spawnRectangle, inspectRectangle, npc);
        player.setPosition();
        npcController.setPositions();
        handleCamera();

        mapRenderer.setView(camera);
        mapRenderer.render(backgroundLayers);

        if (game.debugMode) {
            debugDrawer.drawInteractionZones(interactionMapper.getAllRectangles(), npcController.getNpcs());
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        npcController.drawNPCs(game.batch);

        if (spawnRectangle != null || inspectRectangle != null || npc != null) {
            eButtonIcon.draw(game.batch);
        }

        game.batch.end();

        mapRenderer.render(foregroundLayers);

        drawHUD();

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
            debugDrawer.drawDebugInfo(game.font, game.hudBatch, player, game.currentScreen);
        }
        game.hudBatch.end();
    }


    private void handleCamera() {

        camera.position.x = player.getPosition().x;
        camera.position.y = player.getPosition().y;

        float centerX = camera.viewportWidth / 2;
        float centerY = camera.viewportHeight / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, centerX, mapValues.mapWidthPixels / UNIT - centerX);
        camera.position.y = MathUtils.clamp(camera.position.y, centerY, mapValues.mapHeightPixels / UNIT - centerY);

        camera.update();
    }

    private void handleInput(SpawnRectangle spawnRectangle, InspectRectangle inspectRectangle, NPC npc) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            game.debugMode = !game.debugMode;
        }

        if (game.debugMode) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                respawnPlayer();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                camera.zoom += 0.3f;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                camera.zoom -= 0.3f;
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


        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && spawnRectangle != null) {
            handleInteraction(spawnRectangle);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E) && npc != null) {
            talkToNPC(npc);
        }

    }

    private void handleInteraction(SpawnRectangle rectangle) {
        game.loadRoute(rectangle.getName(), rectangle.getSpawnName());
    }

    private void talkToNPC(NPC npc) {

    }

    private SpawnRectangle checkIfInInteractionTriangle() {
        return checkIfInTriangle(interactionMapper.getSpawnRectangles());
//        Vector2 playerPosPixels = new Vector2(player.getPosition().x * UNIT, player.getPosition().y * UNIT);
//        for (SpawnRectangle intRectangle : interactionMapper.getSpawnRectangles()) {
//
//            if (intRectangle.getRectangle().contains(playerPosPixels)) {
//                return intRectangle;
//            }
//        }
//
//        return null;
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
        debugDrawer.dispose();
        mapRenderer.dispose();
        eButtonIcon.dispose();
        npcController.dispose();
    }
}
