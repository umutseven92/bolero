package com.bolero.game.dtos;

import com.badlogic.gdx.math.Vector2;
import com.bolero.game.schedule.Schedule;
import java.util.List;

public class ScheduleDTO {
  private int hour;
  private int minute;
  private List<NodeDTO> nodes;

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

  public Schedule toSchedule(List<Vector2> positions) {
    return new Schedule(positions, getHour(), getMinute());
  }

  public List<NodeDTO> getNodes() {
    return nodes;
  }

  public void setNodes(List<NodeDTO> nodes) {
    this.nodes = nodes;
  }
}
