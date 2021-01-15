package com.bolero.game.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.Clock;
import com.bolero.game.Schedule;
import com.bolero.game.characters.NPC;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.enums.SpawnType;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class NPCController extends BaseMapper implements Disposable {

  private final ArrayList<NPC> npcs;
  private final ArrayList<Schedule> schedules;
  private final BundleController bundleController;

  public NPCController(TiledMap map, BundleController bundleController) {
    super(map);
    this.bundleController = bundleController;
    npcs = new ArrayList<>();
    schedules = new ArrayList<>();
  }

  public void map(World world)
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException {
    mapNPCs(world);
    mapSchedules();
  }

  private void mapNPCs(World world) throws FileNotFoundException, MissingPropertyException {

    MapObjects spawnObjects = super.getLayer(BoleroGame.SPAWN_LAYER);

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
  }

  private void mapSchedules() throws MissingPropertyException, NPCDoesNotExistException {
    MapObjects scheduleObjects = super.getLayer(BoleroGame.SCHEDULE_LAYER);

    for (MapObject scheduleObj : scheduleObjects) {
      final MapProperties props = scheduleObj.getProperties();

      super.checkMissingProperties(props, Arrays.asList("hour", "npc", "minute"));

      String npcName = props.get("npc", String.class);
      int hour = props.get("hour", int.class);
      int minute = props.get("minute", int.class);

      Optional<NPC> npc = npcs.stream().filter(n -> n.getName().equals(npcName)).findFirst();

      if (!npc.isPresent()) {
        throw new NPCDoesNotExistException(npcName);
      }

      Vector2 position =
          new Vector2(
              (float) props.get("x") / BoleroGame.UNIT, (float) props.get("y") / BoleroGame.UNIT);

      Schedule schedule = new Schedule(position, hour, minute, npc.get());

      schedules.add(schedule);
    }
  }

  public void checkSchedules(Clock clock) {
    for (Schedule schedule : schedules) {
      if (clock.getCurrentHour() == schedule.getHour()
          && clock.getCurrentMinute() == schedule.getMinute()) {
        schedule.getNpc().setGoal(schedule.getPosition());
      }
    }
  }

  public void setPositions() {
    for (NPC npc : npcs) {
      npc.setPosition();
    }
  }

  public ArrayList<NPC> getNpcs() {
    return npcs;
  }

  public void drawNPCs(SpriteBatch batch) {
    for (NPC npc : npcs) {
      npc.draw(batch);
    }
  }

  public NPC checkIfNearNPC(Vector2 playerPos) {
    for (NPC npc : this.npcs) {

      if (npc.getTalkCircle().contains(playerPos)) {
        return npc;
      }
    }

    return null;
  }

  @Override
  public void dispose() {
    for (NPC npc : npcs) {
      npc.dispose();
    }
  }
}
