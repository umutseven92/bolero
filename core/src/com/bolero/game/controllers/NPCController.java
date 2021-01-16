package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.Clock;
import com.bolero.game.Schedule;
import com.bolero.game.characters.NPC;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;
import com.bolero.game.mappers.NPCMapper;
import com.bolero.game.mappers.ScheduleMapper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class NPCController implements Disposable {

  private final TiledMap map;
  private final BundleController bundleController;

  private List<NPC> npcs;
  private List<Schedule> schedules;

  public NPCController(TiledMap map, BundleController bundleController) {
    this.map = map;
    this.bundleController = bundleController;
    npcs = new ArrayList<>();
    schedules = new ArrayList<>();
  }

  public void load(World world)
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException {
    loadNPCs(world);
    loadSchedules();
  }

  private void loadNPCs(World world)
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException {
    NPCMapper mapper = new NPCMapper(map, world, bundleController);
    npcs = mapper.map();
  }

  private void loadSchedules() throws MissingPropertyException, NPCDoesNotExistException {
    if (npcs.size() <= 0) {
      return;
    }

    ScheduleMapper mapper = new ScheduleMapper(map, npcs);
    schedules = mapper.map();
  }

  public void checkSchedules(Clock clock) {
    for (Schedule schedule : schedules) {
      if (clock.getCurrentHour() == schedule.getHour()
          && clock.getCurrentMinute() == schedule.getMinute()) {
        Gdx.app.log(
            NPCController.class.getName(),
            String.format(
                "Schedule activated for NPC %s at %d:%d",
                schedule.getNpc().getName(), schedule.getHour(), schedule.getMinute()));
        schedule.getNpc().setGoal(schedule.getPosition());
      }
    }
  }

  public void setPositions() {
    for (NPC npc : npcs) {
      npc.setPosition();
    }
  }

  public List<NPC> getNpcs() {
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
