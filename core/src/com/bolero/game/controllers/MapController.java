package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.Tuple;
import com.bolero.game.mixins.FileLoader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.val;

public class MapController implements Disposable, FileLoader {
  private final float TIME_BETWEEN_WALK_SOUND = 0.3f;
  private final float MINIMUM_STEP_FOR_SOUND = 1f;

  private final OrthogonalTiledMapRenderer mapRenderer;
  private final TiledMap map;
  private int[] backgroundLayers;
  private int[] foregroundLayers;
  private List<Tuple<Integer, FileHandle>> backgroundLayersWithWalkSound;
  private Music music;
  private Sound walkSound;
  private Vector2 lastPosition;
  private float elapsedTimeSinceLastWalk;

  public MapController(TiledMap map) {
    this.map = map;
    this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / BoleroGame.UNIT);
    this.elapsedTimeSinceLastWalk = 0f;
    this.lastPosition = Vector2.Zero;
  }

  public void load() throws FileNotFoundException {
    loadLayers();
    loadMusic();
  }

  private void loadLayers() throws FileNotFoundException {
    val bg = new ArrayList<Integer>();
    val fg = new ArrayList<Integer>();
    backgroundLayersWithWalkSound = new ArrayList<>();

    val layers = map.getLayers();
    for (int i = 0; i < layers.getCount(); i++) {
      val layer = layers.get(i);
      Boolean foreground = layer.getProperties().get("foreground", Boolean.class);

      // Skip layers that don't have the foreground property.
      if (foreground != null) {
        if (foreground) {
          fg.add(i);
        } else {
          bg.add(i);
          val sound = layer.getProperties().get("walk_sound", String.class);
          if (sound != null) {
            val file = getFile(String.format("sound_effects/%s", sound));
            val entry = new Tuple<>(i, file);
            backgroundLayersWithWalkSound.add(entry);
          }
        }
      }
    }

    backgroundLayers = bg.stream().mapToInt(i -> i).toArray();
    foregroundLayers = fg.stream().mapToInt(i -> i).toArray();

    // Reverse the layers so that the layer closest to the player is at the top.
    Collections.reverse(backgroundLayersWithWalkSound);
  }

  private void loadMusic() throws FileNotFoundException {
    val songName = map.getProperties().get("music", String.class);

    if (songName == null) {
      // No song is defined in map.
      return;
    }

    val file = Gdx.files.internal(String.format("music/%s", songName));

    if (!file.exists()) {
      throw new FileNotFoundException(String.format("music/%s does not exist.", songName));
    }

    music = Gdx.audio.newMusic(file);
    music.setLooping(true);
  }

  public void setView(OrthographicCamera camera) {
    mapRenderer.setView(camera);
  }

  public void drawBackground() {
    mapRenderer.render(backgroundLayers);
  }

  public void drawForeground() {
    mapRenderer.render(foregroundLayers);
  }

  public void playMusic() {
    if (music != null) {
      music.play();
    }
  }

  public void stopMusic() {
    if (music != null) {
      music.stop();
    }
  }

  public void pauseMusic() {
    if (music != null) {
      music.pause();
    }
  }

  public void toggleMusic() {
    if (music != null) {
      if (music.isPlaying()) {
        music.pause();
      } else {
        music.play();
      }
    }
  }

  // Gets the footstep sound for the layer under `position`, starting with the topmost one.
  private Optional<FileHandle> getLayerSound(Vector2 position) {
    for (val tuple : backgroundLayersWithWalkSound) {
      val layer = (TiledMapTileLayer) map.getLayers().get(tuple.x);

      // This is needed to convert coordinates into tile coordinates.
      // Tile coordinate is Game Coordinate / (Tile Size / Game Unit).
      val tileDivider = BoleroGame.TILE_SIZE / BoleroGame.UNIT;
      val cell = layer.getCell((int) (position.x / tileDivider), (int) (position.y / tileDivider));

      if (cell != null) {
        return Optional.of(tuple.y);
      }
    }

    return Optional.empty();
  }

  public void playWalkSound(Vector2 position, float delta) {
    elapsedTimeSinceLastWalk += delta;

    if (elapsedTimeSinceLastWalk <= TIME_BETWEEN_WALK_SOUND) {
      // Do not play footsteps if enough time has not passed.
      return;
    }
    elapsedTimeSinceLastWalk = 0f;

    if (lastPosition.epsilonEquals(position)) {
      // Do not play footsteps if the character has not moved.
      return;
    }

    if (Math.abs(position.x - lastPosition.x) < MINIMUM_STEP_FOR_SOUND
        && Math.abs(position.y - lastPosition.y) < MINIMUM_STEP_FOR_SOUND) {
      // Do not play footsteps for very small movements.
      return;
    }

    lastPosition = position.cpy();

    val sound = getLayerSound(position);

    if (!sound.isPresent()) {
      return;
    }

    walkSound = Gdx.audio.newSound(sound.get());
    walkSound.play();
  }

  @Override
  public void dispose() {
    if (music != null) {
      music.dispose();
    }
    if (walkSound != null) {
      walkSound.dispose();
    }
    mapRenderer.dispose();
  }
}
