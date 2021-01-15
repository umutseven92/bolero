package com.bolero.game.characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.data.SpriteSheetValues;

import java.io.FileNotFoundException;

public class Player extends Character {
  public Player(Vector2 position, World box2DWorld) throws FileNotFoundException {
    super(
        position,
        box2DWorld,
        new CharacterValues(2.7f, 2.5f, 5.5f, 0.7f),
        "player.png",
        new SpriteSheetValues(10, 10, 5, 7),
        BodyDef.BodyType.DynamicBody);
  }
}
