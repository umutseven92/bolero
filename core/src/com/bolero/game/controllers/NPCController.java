package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.Clock;
import com.bolero.game.characters.NPC;
import com.bolero.game.data.Goal;
import com.bolero.game.data.PathNode;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.mappers.NPCMapper;
import com.bolero.game.pathfinding.ManhattanDistance;
import com.bolero.game.pathfinding.PathGraph;
import com.bolero.game.schedule.Schedule;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.val;
import lombok.var;

public class NPCController implements Disposable {

  private final TiledMap map;
  private final BundleManager bundleManager;
  private final IndexedAStarPathFinder<PathNode> pathfinder;
  private final PathGraph graph;

  @Getter private List<NPC> npcs;

  public NPCController(TiledMap map, BundleManager bundleManager, PathGraph graph) {
    this.map = map;
    this.bundleManager = bundleManager;
    npcs = new ArrayList<>();
    this.graph = graph;
    pathfinder = new IndexedAStarPathFinder<>(graph);
  }

  public void load(World world)
      throws FileNotFoundException, MissingPropertyException, ConfigurationNotLoadedException {
    val mapper = new NPCMapper(map, world, bundleManager);
    npcs = mapper.map();
  }

  private boolean scheduleAlreadyTriggered(NPC npc, Schedule schedule) {
    val goal = npc.getGoal();

    if (goal == null) {
      return false;
    }

    return schedule.getHour() == goal.getHour() && schedule.getMinute() == goal.getMinute();
  }

  public void checkSchedules(Clock clock) throws Exception {
    for (val npc : npcs) {
      for (val schedule : npc.getScheduleList().getSchedules()) {
        if (clock.getCurrentHour() == schedule.getHour()
            && clock.getCurrentMinute() == schedule.getMinute()) {

          if (scheduleAlreadyTriggered(npc, schedule)) {
            continue;
          }

          Gdx.app.log(
              NPCController.class.getName(),
              String.format(
                  "Schedule activated for NPC %s at %d:%d",
                  npc.getName(), schedule.getHour(), schedule.getMinute()));

          val npcNode = graph.getClosestNode(npc.getPosition());

          val nodes = new Array<PathNode>();

          var startPos = npcNode;

          for (val pos : schedule.getPositions()) {
            val endGoalNode = graph.getClosestNode(pos);

            val outPath = new DefaultGraphPath<PathNode>();
            val searchResult =
                pathfinder.searchNodePath(startPos, endGoalNode, new ManhattanDistance(), outPath);

            if (searchResult) {
              nodes.addAll(outPath.nodes);
              startPos = endGoalNode;
            } else {
              Gdx.app.error(
                  NPCController.class.getName(),
                  String.format(
                      "No path found for NPC %s's schedule at %d:%d!",
                      npc.getName(), schedule.getHour(), schedule.getMinute()));
            }
          }

          npc.setGoal(new Goal(nodes, schedule.getHour(), schedule.getMinute()));
        }
      }
    }
  }

  public void setPositions() {
    for (val npc : npcs) {
      npc.setPosition();
    }
  }

  public void drawNPCs(SpriteBatch batch) {
    for (val npc : npcs) {
      npc.draw(batch);
    }
  }

  public NPC checkIfNearNPC(Vector2 playerPos) {
    for (val npc : this.npcs) {

      if (npc.getTalkCircle().contains(playerPos)) {
        return npc;
      }
    }

    return null;
  }

  @Override
  public void dispose() {
    for (val npc : npcs) {
      npc.dispose();
    }
  }
}
