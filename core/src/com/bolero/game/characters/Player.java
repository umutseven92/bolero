package com.bolero.game.characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.dtos.MovementDTO;
import com.bolero.game.dtos.SizeDTO;
import java.io.FileNotFoundException;

public class Player extends AbstractCharacter {
  public Player(Vector2 position, World box2DWorld) throws FileNotFoundException {
    super(
        position,
        box2DWorld,
        new SizeDTO(2.7f, 2.5f),
        new MovementDTO(0.7f, 5.5f),
        "images/player.png",
        new SpriteSheetValues(10, 10, 5, 7),
        BodyDef.BodyType.DynamicBody);
  }
}
