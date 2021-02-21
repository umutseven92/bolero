package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.Clock;
import com.bolero.game.ManhattanDistance;
import com.bolero.game.PathGraph;
import com.bolero.game.characters.NPC;
import com.bolero.game.data.PathNode;
import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.mappers.NPCMapper;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.val;

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
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException,
          FileFormatException {
    loadNPCs(world);
  }

  private void loadNPCs(World world)
      throws FileNotFoundException, MissingPropertyException, NPCDoesNotExistException,
          FileFormatException {
    val mapper = new NPCMapper(map, world, bundleManager);
    npcs = mapper.map();
  }

  public void checkSchedules(Clock clock) {
    for (val npc : npcs) {
      for (val schedule : npc.getScheduleList().getSchedules()) {
        if (clock.getCurrentHour() == schedule.getHour()
            && clock.getCurrentMinute() == schedule.getMinute()) {
          Gdx.app.log(
              NPCController.class.getName(),
              String.format(
                  "Schedule activated for NPC %s at %d:%d",
                  npc.getName(), schedule.getHour(), schedule.getMinute()));

          val outPath = new DefaultGraphPath<PathNode>();

          // TODO: Error handling (null check)
          val npcNode = graph.getClosestNode(npc.getPosition());
          val endGoalNode = graph.getClosestNode(schedule.getPositions().get(0));

          val searchResult =
              pathfinder.searchNodePath(npcNode, endGoalNode, new ManhattanDistance(), outPath);

          if (searchResult) {
            npc.setGoals(outPath.nodes);
          } else {
            Gdx.app.error(
                NPCController.class.getName(),
                String.format(
                    "No path found for NPC %s's schedule at %d:%d!",
                    npc.getName(), schedule.getHour(), schedule.getMinute()));
          }
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
