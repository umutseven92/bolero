package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.bolero.game.exceptions.MissingPropertyException;
import java.util.List;
import lombok.val;

public abstract class AbstractMapper {
  private final TiledMap map;

  public AbstractMapper(TiledMap map) {
    this.map = map;
  }

  protected MapObjects getLayer(String layerName) {
    val layer = this.map.getLayers().get(layerName);

    if (layer == null) {
      return new MapObjects();
    }

    return layer.getObjects();
  }

  protected void checkMissingProperties(MapProperties allProperties, List<String> toCheck)
      throws MissingPropertyException {
    for (val prop : toCheck) {
      if (!allProperties.containsKey(prop)) {
        throw new MissingPropertyException(prop);
      }
    }
  }
}
