package com.bolero.game.schedule;

import com.badlogic.gdx.math.Vector2;
import com.bolero.game.characters.NPC;

public class Schedule {
  private final Vector2 position;
  private final int hour;
  private final int minute;

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public Vector2 getPosition() {
    return position;
  }

  public Schedule(Vector2 position, int hour, int minute) {
    this.position = position;
    this.hour = hour;
    this.minute = minute;
  }
}
