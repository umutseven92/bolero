package com.bolero.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.CollisionMap;
import com.bolero.game.MapValues;
import com.bolero.game.Player;

import java.util.ArrayList;

public abstract class GameScreen implements Screen {
    final private BoleroGame game;

    final private World world;
    final private OrthographicCamera camera;

    final private Texture timTexture;
    final private TiledMap map;
    final private int[] backgroundLayers;
    final private int[] foregroundLayers;

    final private Player player;

    final private float UNIT = 16f;
    final float TIME_STEP = 1 / 16f;
    private final MapValues mapValues;
    private final Vector2 playerSpawnPosition;

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final CollisionMap collisionMap;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final ShapeRenderer debugRenderer;

    private Boolean debugMode = false;

    private float accumulator = 0;

    private final ArrayList<Rectangle> interactions;

    public GameScreen(BoleroGame game, String mapPath, int[] backgroundLayers, int[] foregroundLayers) {
        this.game = game;
        this.foregroundLayers = foregroundLayers;
        this.backgroundLayers = backgroundLayers;
        map = new TmxMapLoader().load(mapPath);
        mapValues = new MapValues(map);

        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / UNIT);


        playerSpawnPosition = getPlayerSpawnPoint();
        box2DDebugRenderer = new Box2DDebugRenderer();
        debugRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();

        camera.setToOrtho(false, 30, 20);

        camera.update();

        timTexture = new Texture(Gdx.files.internal("tim.png"));
        world = new World(Vector2.Zero, true);

        player = new Player(2, 2, playerSpawnPosition, timTexture, world);
        handleCamera();

        collisionMap = new CollisionMap(world, map);
        collisionMap.createCollisions(UNIT, mapValues);

        interactions = createInteractionMap();
    }

    private ArrayList<Rectangle> createInteractionMap() {
        ArrayList<Rectangle> rectangles = new ArrayList<>();

        MapLayer layer = map.getLayers().get("Interaction");

        if (layer == null) {
            return rectangles;
        }

        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            rectangles.add(rectangle);
        }

        return rectangles;
    }

    private Vector2 getPlayerSpawnPoint() {
        MapObject playerSpawnObject = map.getLayers().get("Spawn").getObjects().get("player");

        final MapProperties props = playerSpawnObject.getProperties();

        return new Vector2((float) props.get("x") / UNIT, (float) props.get("y") / UNIT);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        player.setPosition();
        handleCamera();

        mapRenderer.setView(camera);
        mapRenderer.render(backgroundLayers);

        if (debugMode) {
            drawInteractionZones();
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        mapRenderer.render(foregroundLayers);

        drawHUD();

        if (debugMode) {
            box2DDebugRenderer.render(world, camera.combined);
        }

        doPhysicsStep(delta);
    }


    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }

    private void drawHUD() {
        game.hudBatch.begin();
        if (debugMode) {
            drawDebugInfo();
        }
        game.hudBatch.end();
    }

    private void drawInteractionZones() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        debugRenderer.setProjectionMatrix(camera.combined);

        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(1, 0, 0, 0.5f);

        for (Rectangle rectangle : interactions) {
            debugRenderer.rect(rectangle.x / UNIT, rectangle.y / UNIT, rectangle.width / UNIT, rectangle.height / UNIT);

        }
        debugRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawDebugInfo() {
        float cameraX = Gdx.graphics.getWidth() - UNIT * 15;
        float cameraY = Gdx.graphics.getHeight() - UNIT * 2;
        float camera3Y = Gdx.graphics.getHeight() - UNIT * 3;

        game.font.draw(game.hudBatch, "Player Pos: " + player.getPosition().x + ", " + player.getPosition().y, cameraX, cameraY);
        game.font.draw(game.hudBatch, "Camera Pos: " + camera.position.x + ", " + camera.position.y, cameraX, camera3Y);
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

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            debugMode = !debugMode;
        }

        if (debugMode) {
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


        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            handleInteraction();
        }

    }

    private void handleInteraction() {
        Vector2 playerPosPixels = new Vector2(player.getPosition().x * UNIT, player.getPosition().y * UNIT);
        for (Rectangle rectangle : interactions) {

            if (rectangle.contains(playerPosPixels)) {
                game.setScreen(game.houseScreen);
            }
        }
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
        timTexture.dispose();
        map.dispose();
        player.dispose();
        collisionMap.dispose();
        debugRenderer.dispose();
        box2DDebugRenderer.dispose();
        mapRenderer.dispose();
    }
}
