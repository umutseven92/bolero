package com.bolero.game.data;

import com.badlogic.gdx.utils.Array;
import lombok.Getter;

public class Goal {
  private int goalIndex;
  @Getter private boolean finished;
  @Getter private final Array<PathNode> steps;
  @Getter private PathNode currentGoal;

  public void incrementGoal() {
    this.goalIndex++;
    if (this.goalIndex > this.steps.size - 1) {
      this.finished = true;
    } else {
      currentGoal = steps.get(goalIndex);
    }
  }

  public boolean onLastGoal() {
    return this.goalIndex == this.steps.size - 1;
  }

  public PathNode getNextGoal() {
    if (!onLastGoal()) {
      return this.steps.get(this.goalIndex + 1);
    }

    return null;
  }

  public Goal(Array<PathNode> steps) {
    this.finished = false;
    this.goalIndex = 0;
    this.currentGoal = steps.get(goalIndex);
    this.steps = steps;
  }
}
