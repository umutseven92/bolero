package com.bolero.game.interactions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;
import lombok.Setter;

public class InspectRectangle extends AbstractRectangle {

  @Getter @Setter private FileHandle soundFile;
  @Getter private final String stringID;

  public InspectRectangle(Rectangle rectangle, String stringID, boolean hidden) {
    super(rectangle, hidden);
    this.stringID = stringID;
  }
}
