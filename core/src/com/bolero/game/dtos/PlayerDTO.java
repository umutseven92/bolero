package com.bolero.game.dtos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.characters.Player;
import java.io.FileNotFoundException;
import lombok.Data;

@Data
public class PlayerDTO {
  private String spriteSheet;
  private SizeDTO size;
  private MovementDTO movement;

  public Player toPlayer(Vector2 spawnPos, World world) throws FileNotFoundException {

    return new Player(spawnPos, world, size, movement, spriteSheet);
  }
}
