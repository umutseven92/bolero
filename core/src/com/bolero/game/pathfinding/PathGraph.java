package com.bolero.game.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bolero.game.data.PathNode;
import lombok.Getter;
import lombok.val;

public class PathGraph implements IndexedGraph<PathNode> {
  private static final float TOLERANCE = 5f;

  @Getter private final Array<PathNode> nodes;

  public PathGraph(Array<PathNode> nodes) {
    this.nodes = nodes;
  }

  @Override
  public int getIndex(PathNode node) {
    return node.getIndex();
  }

  @Override
  public int getNodeCount() {
    return nodes.size;
  }

  @Override
  public Array<Connection<PathNode>> getConnections(PathNode fromNode) {
    return fromNode.getConnections();
  }

  public PathNode getClosestNode(Vector2 position) throws Exception {
    for (val node : nodes) {
      if (position.dst(node.getX(), node.getY()) <= TOLERANCE) {
        return node;
      }
    }

    throw new Exception(
        String.format(
            "There are no nodes close enough (<= %f) to %s!", TOLERANCE, position.toString()));
  }
}
