package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;

public class TransitionRectangle extends InteractionRectangle {
  private final String mapName;
  private final String spawnName;

  public TransitionRectangle(String mapName, String spawnName, Rectangle rectangle) {
    super(rectangle);
    this.mapName = mapName;
    this.spawnName = spawnName;
  }

  public String getName() {
    return mapName;
  }

  public String getSpawnName() {
    return spawnName;
  }
}
