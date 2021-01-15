package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;

public abstract class InteractionRectangle {
  private final Rectangle rectangle;

  public InteractionRectangle(Rectangle rectangle) {

    this.rectangle = rectangle;
  }

  public Rectangle getRectangle() {
    return rectangle;
  }
}
