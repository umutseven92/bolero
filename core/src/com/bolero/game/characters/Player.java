package com.bolero.game.characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.dtos.MovementDTO;
import com.bolero.game.dtos.SizeDTO;
import com.bolero.game.dtos.SpriteSheetDTO;
import java.io.FileNotFoundException;

public class Player extends AbstractCharacter {
  public Player(
      Vector2 position,
      World box2DWorld,
      SizeDTO size,
      MovementDTO movement,
      SpriteSheetDTO spriteSheetDTO)
      throws FileNotFoundException {
    super(position, box2DWorld, size, movement, spriteSheetDTO, BodyDef.BodyType.DynamicBody);
  }
}
