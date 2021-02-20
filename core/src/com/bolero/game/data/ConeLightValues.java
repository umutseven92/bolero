package com.bolero.game.data;

import com.badlogic.gdx.graphics.Color;
import com.bolero.game.enums.LightTime;
import lombok.Getter;

public class ConeLightValues extends AbstractLightValues {
  @Getter private final float directionDegree;
  @Getter private final float coneDegree;

  public ConeLightValues(
      float distance,
      Color color,
      int rays,
      float directionDegree,
      float coneDegree,
      LightTime time) {
    super(distance, color, rays, time);
    this.directionDegree = directionDegree;
    this.coneDegree = coneDegree;
  }
}
