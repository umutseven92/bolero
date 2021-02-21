package com.bolero.game.data;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;

public class PathNode {
  @Getter private final int index;
  @Getter private final float x;
  @Getter private final float y;
  @Getter private final Array<Connection<PathNode>> connections;

  public PathNode(int index, float x, float y) {
    this.index = index;
    this.x = x;
    this.y = y;
    this.connections = new Array<>();
  }
}
