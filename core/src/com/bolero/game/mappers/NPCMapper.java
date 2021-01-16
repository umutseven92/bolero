package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.characters.NPC;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.enums.SpawnType;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NPCMapper extends AbstractMapper implements Mapper<List<NPC>> {
  private final World world;
  private final BundleController bundleController;

  public NPCMapper(TiledMap map, World world, BundleController bundleController) {
    super(map);
    this.world = world;
    this.bundleController = bundleController;
  }

  @Override
  public List<NPC> map()
      throws MissingPropertyException, NPCDoesNotExistException, FileNotFoundException {
    MapObjects spawnObjects = super.getLayer(BoleroGame.SPAWN_LAYER);

    List<NPC> npcs = new ArrayList<>();

    for (MapObject spawn : spawnObjects) {
      MapProperties props = spawn.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));
      String type = props.get("type", String.class);

      if (SpawnType.valueOf(type) == SpawnType.npc) {
        super.checkMissingProperties(props, Arrays.asList("name", "script", "sprite_sheet"));

        String name = props.get("name", String.class);

        String script = props.get("script", String.class);

        String spriteSheet = props.get("sprite_sheet", String.class);

        Vector2 spawnPosition =
            new Vector2(
                (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);

        NPC npc =
            new NPC(
                name,
                script,
                spawnPosition,
                world,
                new CharacterValues(2.7f, 2.5f, 5f, 0.5f),
                spriteSheet,
                bundleController);
        npcs.add(npc);
      }
    }

    return npcs;
  }
}
