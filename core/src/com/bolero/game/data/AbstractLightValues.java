package com.bolero.game.data;

import com.badlogic.gdx.graphics.Color;
import com.bolero.game.enums.LightTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public abstract class AbstractLightValues {
  private final float distance;
  private final Color color;
  private final int rays;
  private final LightTime time;

  protected AbstractLightValues(float distance, Color color, int rays, LightTime time) {
    this.distance = distance;
    this.color = color;
    this.rays = rays;
    this.time = time;
  }
}
