package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.BoleroGame;
import com.bolero.game.Schedule;
import com.bolero.game.characters.NPC;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ScheduleMapper extends AbstractMapper implements Mapper<List<Schedule>> {
  private final List<NPC> npcs;

  public ScheduleMapper(TiledMap map, List<NPC> npcs) {
    super(map);
    this.npcs = npcs;
  }

  public List<Schedule> map() throws MissingPropertyException, NPCDoesNotExistException {
    MapObjects scheduleObjects = super.getLayer(BoleroGame.SCHEDULE_LAYER);

    List<Schedule> schedules = new ArrayList<>();
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

    return schedules;
  }
}
