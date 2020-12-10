package com.bolero.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.bolero.game.BoleroGame;
import com.bolero.game.Player;

import java.util.ArrayList;

public class GameScreen implements Screen {
    final BoleroGame game;

    final private World world;
    final private OrthographicCamera camera;

    final private Texture timTexture;
    final private TiledMap map;
    final private int[] backgroundLayers = {0, 1};
    final private int[] foregroundLayers = {2, 3};
    final private ArrayList<Shape> staticShapes = new ArrayList<Shape>();

    final private Player player;

    final float UNIT = 16f;
    final float TIME_STEP = 1 / 16f;
    final float MAP_HEIGHT = 3200;
    final float MAP_WIDTH = 4000;

    final Vector2 SPAWN_POSITION = new Vector2(240, 35);

    private final OrthogonalTiledMapRenderer mapRenderer;

    private final Box2DDebugRenderer debugRenderer;

    private Boolean debugMode = false;

    private float accumulator = 0;

    public GameScreen(BoleroGame game) {
        this.game = game;

        map = new TmxMapLoader().load("map/torello.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / UNIT);
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();

        camera.setToOrtho(false, 30, 20);

        camera.update();

        timTexture = new Texture(Gdx.files.internal("tim.png"));
        world = new World(Vector2.Zero, true);

        player = new Player(2, 2, SPAWN_POSITION, timTexture, world);
        handleCamera();
        createWalls();
        createCollisions();
    }

    public void createWalls() {
        PolygonShape verticalMapWall = new PolygonShape();
        verticalMapWall.setAsBox(1, (MAP_HEIGHT * 2 / (UNIT)));

        staticShapes.add(verticalMapWall);

        PolygonShape horizontalMapWall = new PolygonShape();
        horizontalMapWall.setAsBox((MAP_HEIGHT * 2 / (UNIT)), 1);

        staticShapes.add(verticalMapWall);

        BodyDef eastWallDef = new BodyDef();
        eastWallDef.position.set(new Vector2((MAP_WIDTH / UNIT) + 1, MAP_HEIGHT / UNIT));

        BodyDef westWallDef = new BodyDef();
        westWallDef.position.set(-1, 0);

        BodyDef northWallDef = new BodyDef();
        northWallDef.position.set(new Vector2(0, (MAP_HEIGHT / UNIT) + 1));

        BodyDef southWallDef = new BodyDef();
        southWallDef.position.set(0, -1);

        world.createBody(eastWallDef).createFixture(verticalMapWall, 0.0f);
        world.createBody(westWallDef).createFixture(verticalMapWall, 0.0f);
        world.createBody(northWallDef).createFixture(horizontalMapWall, 0.0f);
        world.createBody(southWallDef).createFixture(horizontalMapWall, 0.0f);
    }


    public static Shape getShapeFromRectangle(Rectangle rectangle) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.width * 0.5F / 16, rectangle.height * 0.5F / 16);
        return polygonShape;
    }

    public static Vector2 getTransformedCenterForRectangle(Rectangle rectangle) {
        Vector2 center = new Vector2();
        rectangle.getCenter(center);
        return center.scl(1 / 16f);
    }

    public void createCollisions() {
        MapObjects objects = map.getLayers().get("Collision").getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            //create a dynamic within the world body (also can be KinematicBody or StaticBody
            BodyDef bodyDef = new BodyDef();
            Body body = world.createBody(bodyDef);

            //create a fixture for each body from the shape
            body.createFixture(getShapeFromRectangle(rectangle), 0.0f);

            //setting the position of the body's origin. In this case with zero rotation
            body.setTransform(getTransformedCenterForRectangle(rectangle), 0);
        }
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

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        mapRenderer.render(foregroundLayers);

        drawHUD();

        if (debugMode) {
            debugRenderer.render(world, camera.combined);
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

        camera.position.x = MathUtils.clamp(camera.position.x, centerX, MAP_WIDTH / UNIT - centerX);
        camera.position.y = MathUtils.clamp(camera.position.y, centerY, MAP_HEIGHT / UNIT - centerY);

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
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.player.applyLeftMovement();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.player.applyRightMovement();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            this.player.applyUpMovement();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            this.player.applyDownMovement();
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.player.stopXMovement();
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            this.player.stopYMovement();
        }

    }


    private void respawnPlayer() {
        player.respawn(SPAWN_POSITION);
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

        for (Shape shape : staticShapes) {
            shape.dispose();
        }
    }
}
