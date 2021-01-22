package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;

public class InspectRectangle extends AbstractRectangle {

  private final String stringID;

  public InspectRectangle(Rectangle rectangle, String stringID) {
    super(rectangle);
    this.stringID = stringID;
  }

  public String getStringID() {
    return stringID;
  }
}
