package com.bolero.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.BoleroGame;
import com.bolero.game.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    final BoleroGame game;

    final private OrthographicCamera camera;

    final private Texture timTexture;
    final private TiledMap map;
    final private int[] backgroundLayers = {0, 1};
    final private int[] foregroundLayers = {2};

    final private Player player;

    final float UNIT = 16f;
    final float MAP_HEIGHT = 3200;
    final float MAP_WIDTH = 4000;

    final Vector2 SPAWN_POSITION = new Vector2(240, 35);
    final Color collisionColor = new Color(1, 0, 0, 0.3f);

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final ShapeRenderer shapeRenderer;

    private Boolean debugMode = false;
    private Boolean colliding = false;

    private Vector2 lastPlayerPosition = SPAWN_POSITION;

    private final List<Rectangle> collisionRectangles = new ArrayList<Rectangle>() {
        {
            add(new Rectangle(224, 44, 6, 2));
            add(new Rectangle(213, 43, 5, 3));
        }
    };

    public GameScreen(BoleroGame game) {
        this.game = game;

        map = new TmxMapLoader().load("map/torello.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / UNIT);

        camera = new OrthographicCamera();

        camera.setToOrtho(false, 30, 20);

        camera.update();

        timTexture = new Texture(Gdx.files.internal("tim.png"));

        player = new Player(2, 2, SPAWN_POSITION, 5, timTexture);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        handleCamera();

        mapRenderer.setView(camera);
        mapRenderer.render(backgroundLayers);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
        mapRenderer.render(foregroundLayers);

        drawHUD();
        handleOutOfBounds();
        handleCollision();

        if (colliding) {
            player.setPosition(lastPlayerPosition);
        } else {
            // https://gamedev.stackexchange.com/a/103974/50150
            lastPlayerPosition = player.getPosition().cpy();
        }

        if (debugMode) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(collisionColor);
            shapeRenderer.rect(player.collisionRectangle.x, player.collisionRectangle.y, player.collisionRectangle.width, player.collisionRectangle.height);

            for (Rectangle rect : collisionRectangles) {
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }

            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
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

        camera.position.x = player.getPosition().x + player.getWidth() / 2;
        camera.position.y = player.getPosition().y + player.getHeight() / 2;

        float centerX = camera.viewportWidth / 2;
        float centerY = camera.viewportHeight / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, centerX, MAP_WIDTH / UNIT - centerX);
        camera.position.y = MathUtils.clamp(camera.position.y, centerY, MAP_HEIGHT / UNIT - centerY);

        camera.update();
    }

    private void handleOutOfBounds() {
        final Vector2 playerPos = player.getPosition();
        if (playerPos.x < 0) {
            player.setPositionX(0);
        }

        if (playerPos.x > MAP_WIDTH / UNIT - player.getWidth()) {
            player.setPositionX(MAP_WIDTH / UNIT - player.getWidth());
        }

        if (playerPos.y > MAP_HEIGHT / UNIT - player.getHeight()) {
            player.setPositionY(MAP_HEIGHT / UNIT - player.getHeight());
        }

        if (playerPos.y < 0) {
            player.setPositionY(0);
        }
    }

    private void handleCollision() {
        for (Rectangle rect : collisionRectangles) {
            if (rectangleCollision(rect)) {
                colliding = true;
                return;
            }
        }
        colliding = false;
    }

    private Boolean rectangleCollision(Rectangle rectangle) {
        return rectangle.overlaps(player.collisionRectangle);
    }

    private void handleInput(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            debugMode = !debugMode;
        }

        if (!colliding) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.moveLeft(deltaTime);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.moveRight(deltaTime);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.moveDown(deltaTime);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.moveUp(deltaTime);
            }
        }

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
    }
}
