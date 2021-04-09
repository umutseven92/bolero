package com.bolero.game.mappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.characters.NPC;
import com.bolero.game.enums.SpawnType;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.loaders.NPCLoader;
import com.bolero.game.managers.BundleManager;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.val;

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
      throws MissingPropertyException, FileNotFoundException, ConfigurationNotLoadedException {
    val file = Gdx.files.internal("config/npcs.yaml");

    val npcLoader = new NPCLoader();
    val npcsDTO = npcLoader.load(file);

    val spawnObjects =
        super.getLayer(BoleroGame.getConfig().getMaps().getLayers().getSpawn());

    val npcs = new ArrayList<NPC>();

    for (val spawn : spawnObjects) {
      val props = spawn.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));
      val type = props.get("type", String.class);

      if (SpawnType.valueOf(type) == SpawnType.npc) {
        super.checkMissingProperties(props, Collections.singletonList("npc"));

        val name = props.get("npc", String.class);

        val spawnPosition =
            new Vector2(
                (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);

        val npcDTO = npcsDTO.getNpcDTOFromName(name);

        if (npcDTO.isPresent()) {
          val scheduleObjects =
              super.getLayer(BoleroGame.getConfig().getMaps().getLayers().getSchedule());

          val scheduleMap = new HashMap<String, Vector2>();

          for (MapObject scheduleObj : scheduleObjects) {
            val scheduleProps = scheduleObj.getProperties();

            val schedulePosition =
                new Vector2(
                    (float) scheduleProps.get("x") / BoleroGame.UNIT,
                    (float) scheduleProps.get("y") / BoleroGame.UNIT);
            scheduleMap.put(scheduleObj.getName(), schedulePosition);
          }

          val npc = npcDTO.get().toNPC(spawnPosition, scheduleMap, world, bundleManager);
          npcs.add(npc);
        }
      }
    }

    return npcs;
  }
}
