package com.bolero.game.controllers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.bolero.game.exceptions.MissingPropertyException;

import java.util.List;

public abstract class BaseMapper {
  private final TiledMap map;

  BaseMapper(TiledMap map) {
    this.map = map;
  }

  protected MapObjects getLayer(String layerName) {
    MapLayer layer = this.map.getLayers().get(layerName);

    if (layer == null) {
      return new MapObjects();
    }

    return layer.getObjects();
  }

  protected void checkMissingProperties(MapProperties allProperties, List<String> toCheck)
      throws MissingPropertyException {
    for (String prop : toCheck) {
      if (!allProperties.containsKey(prop)) {
        throw new MissingPropertyException(prop);
      }
    }
  }
}
