package com.bolero.game.dtos;

import com.badlogic.gdx.math.Vector2;
import com.bolero.game.schedule.Schedule;

public class ScheduleDTO {
  private int hour;
  private int minute;
  private String node;

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public int getMinute() {
    return minute;
  }

  public void setMinute(int minute) {
    this.minute = minute;
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  public Schedule toSchedule(Vector2 position) {
    return new Schedule(position, getHour(), getMinute());
  }
}
