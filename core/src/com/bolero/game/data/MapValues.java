package com.bolero.game.data;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import lombok.Data;

@Data
public class MapValues {
  private final int mapWidthUnit;
  private final int mapHeightUnit;
  private final int mapWidthPixels;
  private final int mapHeightPixels;

  private final int tileWidthPixels;
  private final int tileHeightPixels;

  public MapValues(TiledMap map) {
    MapProperties prop = map.getProperties();

    mapWidthUnit = prop.get("width", Integer.class);
    mapHeightUnit = prop.get("height", Integer.class);
    tileWidthPixels = prop.get("tilewidth", Integer.class);
    tileHeightPixels = prop.get("tileheight", Integer.class);

    mapWidthPixels = mapWidthUnit * tileWidthPixels;
    mapHeightPixels = mapHeightUnit * tileHeightPixels;
  }
}
