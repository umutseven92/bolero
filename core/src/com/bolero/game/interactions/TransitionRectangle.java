package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

public class TransitionRectangle extends AbstractRectangle {
  @Getter private final String mapName;
  @Getter private final String spawnName;

  public TransitionRectangle(String mapName, String spawnName, Rectangle rectangle) {
    super(rectangle);
    this.mapName = mapName;
    this.spawnName = spawnName;
  }
}
