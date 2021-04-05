package com.bolero.game.interactions;

import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

public class InspectRectangle extends AbstractRectangle {

  @Getter private final String stringID;

  public InspectRectangle(Rectangle rectangle, String stringID, boolean hidden) {
    super(rectangle, hidden);
    this.stringID = stringID;
  }
}
