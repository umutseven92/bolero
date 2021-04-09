package com.bolero.game.mappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.characters.Player;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.loaders.PlayerLoader;
import java.io.FileNotFoundException;
import lombok.val;

public class PlayerMapper extends AbstractMapper implements Mapper<Player> {
  private final World world;
  private final String spawnPosName;
  private final TiledMap map;

  public PlayerMapper(TiledMap map, World world, String spawnPosName) {
    super(map);
    this.map = map;
    this.world = world;
    this.spawnPosName = spawnPosName;
  }

  // Get the spawn position of the player from the map.
  // Spawn position is a point object within the SPAWN_LAYER in the map.
  private Vector2 getPlayerSpawnPoint(String name) throws ConfigurationNotLoadedException {
    MapObject playerSpawnObject =
        map.getLayers()
            .get(BoleroGame.getConfig().getMaps().getLayers().getSpawn())
            .getObjects()
            .get(name);

    final MapProperties props = playerSpawnObject.getProperties();

    return new Vector2(
        (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);
  }

  @Override
  public Player map() throws FileNotFoundException, ConfigurationNotLoadedException {
    val file = Gdx.files.internal("config/player.yaml");

    val playerLoader = new PlayerLoader();

    val playerDTO = playerLoader.load(file);

    val spawnPos = getPlayerSpawnPoint(this.spawnPosName);
    return playerDTO.toPlayer(spawnPos, world);
  }
}
