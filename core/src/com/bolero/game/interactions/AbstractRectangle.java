package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

public abstract class AbstractRectangle {
  @Getter private final Rectangle rectangle;

  public AbstractRectangle(Rectangle rectangle) {

    this.rectangle = rectangle;
  }
}
