package com.bolero.game.mappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.characters.Player;
import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.loaders.PlayerLoader;
import java.io.FileNotFoundException;
import lombok.val;

public class PlayerMapper extends AbstractMapper implements Mapper<Player> {
  private final World world;
  private final Vector2 spawnPos;

  public PlayerMapper(TiledMap map, World world, Vector2 spawnPos) {
    super(map);
    this.world = world;
    this.spawnPos = spawnPos;
  }

  @Override
  public Player map() throws FileNotFoundException, FileFormatException {
    val file = Gdx.files.internal("config/player.yaml");

    val playerLoader = new PlayerLoader();

    val playerDTO = playerLoader.load(file);

    return playerDTO.toPlayer(spawnPos, world);
  }
}
