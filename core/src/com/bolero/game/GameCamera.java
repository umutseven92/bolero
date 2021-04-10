package com.bolero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.data.MapValues;
import com.bolero.game.mixins.SmoothMovement;
import lombok.val;

public class GameCamera implements SmoothMovement {
  // How fast the camera catches up to the Player.
  private static final float CAMERA_SPEED = 0.1f;

  // How fast to zoom in. Ignored when zooming in instantly.
  private static final float ZOOM_SPEED = 0.8f;
  private final OrthographicCamera camera;

  private float zoomTarget;

  public GameCamera(Vector2 initialPosition) {
    camera = new OrthographicCamera();

    float width = Gdx.graphics.getWidth();
    float height = Gdx.graphics.getHeight();

    // TODO: Calculate these magic numbers
    camera.setToOrtho(false, width / 28.6f, height / 32f);

    camera.position.x = initialPosition.x;
    camera.position.y = initialPosition.y;

    camera.update();
    zoomTarget = camera.zoom;
  }

  public void setViewPort(int width, int height) {
    camera.viewportWidth = width / 28.6f;
    camera.viewportHeight = height / 32f;
  }

  public void updateCameraPosition(Vector2 targetPos, MapValues mapValues) {
    val newPos =
        getSmoothMovement(
            CAMERA_SPEED, targetPos, new Vector2(camera.position.x, camera.position.y));
    camera.position.set(newPos.x);

    // Prevent camera from going outside the screen.
    // If the map is smaller than the camera view, this will cause flickering & other weird
    // behaviour, so we clamp on if the map is big enough for both axes.
    if (mapValues.getMapWidthUnit() * 2 > camera.viewportWidth) {
      float centerX = camera.viewportWidth / 2;

      camera.position.x =
          MathUtils.clamp(
              camera.position.x,
              centerX,
              mapValues.getMapWidthPixels() / BoleroGame.UNIT - centerX);
    }

    if (mapValues.getMapHeightUnit() * 2 > camera.viewportHeight) {
      float centerY = camera.viewportHeight / 2;

      camera.position.y =
          MathUtils.clamp(
              camera.position.y,
              centerY,
              mapValues.getMapHeightPixels() / BoleroGame.UNIT - centerY);
    }

    camera.update();
  }

  private void checkCameraZoom(float deltaTime) {
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

  public void update(Vector2 position, MapValues mapValues, float deltaTime) {
    this.updateCameraPosition(position, mapValues);
    this.checkCameraZoom(deltaTime);
  }

  // Zoom in an amount instantly.
  public void zoomInInstant(float amount) {
    camera.zoom -= amount;
    zoomTarget = camera.zoom;
  }

  public void zoomOutInstant(float amount) {
    camera.zoom += amount;
    zoomTarget = camera.zoom;
  }

  // Zoom in an amount smoothly.
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
