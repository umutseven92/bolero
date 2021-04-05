package com.bolero.game.sprite_elements;

import com.badlogic.gdx.math.Vector2;
import java.io.FileNotFoundException;

public class Sparkle extends AbstractSpriteElement {

  public Sparkle(Vector2 position) throws FileNotFoundException {
    super(
        "images/sparkle.png",
        position,
        new Vector2(1, 1),
        0.1f,
        new SpriteElementValues(4, 8, 0, 4, 0, 4));
  }
}
