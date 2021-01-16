package com.bolero.game.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;

import java.util.ArrayList;

public class MapController implements Disposable {
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final TiledMap map;
  private int[] backgroundLayers;
  private int[] foregroundLayers;

  public MapController(TiledMap map) {
    this.map = map;
    mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / BoleroGame.UNIT);
  }

  public void load() {
    ArrayList<Integer> bg = new ArrayList<>();
    ArrayList<Integer> fg = new ArrayList<>();

    MapLayers layers = map.getLayers();
    for (int i = 0; i < layers.getCount(); i++) {
      MapLayer layer = layers.get(i);
      Boolean foreground = layer.getProperties().get("foreground", Boolean.class);
      if (foreground != null) {
        if (foreground) {
          fg.add(i);
        } else {
          bg.add(i);
        }
      }
    }

    backgroundLayers = bg.stream().mapToInt(i -> i).toArray();
    foregroundLayers = fg.stream().mapToInt(i -> i).toArray();
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

  @Override
  public void dispose() {
    mapRenderer.dispose();
  }
}
