package com.bolero.game;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.bolero.game.data.PathNode;

public class ManhattanDistance implements Heuristic<PathNode> {
  @Override
  public float estimate(PathNode node, PathNode endNode) {
    return Math.abs(endNode.getX() - node.getX()) + Math.abs(endNode.getY() - node.getY());
  }
}
