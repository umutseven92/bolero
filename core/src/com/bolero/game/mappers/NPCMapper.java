package com.bolero.game.mappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.NPCLoader;
import com.bolero.game.characters.NPC;
import com.bolero.game.dtos.NpcDTO;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.enums.SpawnType;
import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;
import com.bolero.game.managers.BundleManager;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NPCMapper extends AbstractMapper implements Mapper<List<NPC>> {
  private final World world;
  private final BundleManager bundleManager;

  public NPCMapper(TiledMap map, World world, BundleManager bundleManager) {
    super(map);
    this.world = world;
    this.bundleManager = bundleManager;
  }

  @Override
  public List<NPC> map()
      throws MissingPropertyException, NPCDoesNotExistException, FileNotFoundException,
          FileFormatException {
    FileHandle file = Gdx.files.internal("npcs.yaml");

    NPCLoader npcLoader = new NPCLoader();
    NpcsDTO npcsDTO = npcLoader.load(file);

    MapObjects spawnObjects = super.getLayer(BoleroGame.SPAWN_LAYER);

    List<NPC> npcs = new ArrayList<>();

    for (MapObject spawn : spawnObjects) {
      MapProperties props = spawn.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));
      String type = props.get("type", String.class);

      if (SpawnType.valueOf(type) == SpawnType.npc) {
        String name = props.get("name", String.class);

        Vector2 spawnPosition =
            new Vector2(
                (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);

        Optional<NpcDTO> npcDTO = npcsDTO.getNpcDTOFromSpawn(name);

        if (npcDTO.isPresent()) {
          MapObjects scheduleObjects = super.getLayer(BoleroGame.SCHEDULE_LAYER);

          Map<String, Vector2> scheduleMap = new HashMap<String, Vector2>();

          for (MapObject scheduleObj : scheduleObjects) {
            final MapProperties scheduleProps = scheduleObj.getProperties();
            String scheduleName = scheduleProps.get("name", String.class);

            Vector2 schedulePosition =
                new Vector2(
                    (float) scheduleProps.get("x") / BoleroGame.UNIT,
                    (float) scheduleProps.get("y") / BoleroGame.UNIT);
            scheduleMap.put(scheduleName, schedulePosition);
          }
          NPC npc = npcDTO.get().toNPC(spawnPosition, scheduleMap, world, bundleManager);
          npcs.add(npc);
        }
      }
    }

    return npcs;
  }
}
