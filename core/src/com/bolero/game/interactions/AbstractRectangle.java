package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;

public abstract class AbstractRectangle {
  private final Rectangle rectangle;

  public AbstractRectangle(Rectangle rectangle) {

    this.rectangle = rectangle;
  }

  public Rectangle getRectangle() {
    return rectangle;
  }
}
