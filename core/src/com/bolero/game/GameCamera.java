package com.bolero.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GameCamera {
    private final float ZOOM_SPEED = 0.8f;
    private final OrthographicCamera camera;

    private float zoomTarget;

    public GameCamera() {
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 30, 20);

        camera.update();
        zoomTarget = camera.zoom;
    }

    public void updatePosition(Vector2 position, float unit, MapValues mapValues) {
        camera.position.x = position.x;
        camera.position.y = position.y;

        float centerX = camera.viewportWidth / 2;
        float centerY = camera.viewportHeight / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, centerX, mapValues.mapWidthPixels / unit - centerX);
        camera.position.y = MathUtils.clamp(camera.position.y, centerY, mapValues.mapHeightPixels / unit - centerY);

        camera.update();
    }

    public void update(Vector2 position, float unit, MapValues mapValues, float deltaTime) {

        updatePosition(position, unit, mapValues);

        if (camera.zoom != zoomTarget) {
            if (camera.zoom > zoomTarget) {
                camera.zoom -= deltaTime * ZOOM_SPEED;
                if (camera.zoom < zoomTarget) {
                    camera.zoom = zoomTarget;
                }
            } else {
                camera.zoom += deltaTime * ZOOM_SPEED;
                if (camera.zoom > zoomTarget) {
                    camera.zoom = zoomTarget;
                }

            }

        }
    }

    public void zoomInInstant(float amount) {
        camera.zoom -= amount;
        zoomTarget = camera.zoom;
    }

    public void zoomOutInstant(float amount) {
        camera.zoom += amount;
        zoomTarget = camera.zoom;

    }

    public void zoomIn(float amount) {
        zoomTarget = camera.zoom - amount;
    }

    public void zoomOut(float amount) {
        zoomTarget = camera.zoom + amount;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
