package com.bolero.game.data;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapValues {
  public final int mapWidthUnit;
  public final int mapHeightUnit;
  public final int mapWidthPixels;
  public final int mapHeightPixels;
  public final int tileWidthPixels;
  public final int tileHeightPixels;

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
