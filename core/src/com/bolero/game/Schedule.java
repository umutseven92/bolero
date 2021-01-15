package com.bolero.game;

import com.badlogic.gdx.math.Vector2;
import com.bolero.game.characters.NPC;

public class Schedule {
  private final Vector2 position;
  private final int hour;
  private final int minute;
  private final NPC npc;

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public NPC getNpc() {
    return npc;
  }

  public Vector2 getPosition() {
    return position;
  }

  public Schedule(Vector2 position, int hour, int minute, NPC npc) {
    this.position = position;
    this.hour = hour;
    this.minute = minute;
    this.npc = npc;
  }
}
