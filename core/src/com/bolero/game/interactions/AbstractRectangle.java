package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;

@RequiredArgsConstructor
public abstract class AbstractRectangle {
  @Getter private final Rectangle rectangle;

  // If hidden, this rectangle will not emit a sparkle. It will still be interactive.
  @Getter private final Boolean hidden;

  public Vector2 getOrigin() {
    var origin = new Vector2();
    origin = this.rectangle.getCenter(origin);
    return origin;
  }
}
