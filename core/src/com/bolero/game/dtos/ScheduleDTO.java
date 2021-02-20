package com.bolero.game.dtos;

import com.badlogic.gdx.math.Vector2;
import com.bolero.game.schedule.Schedule;
import java.util.List;
import lombok.Data;

@Data
public class ScheduleDTO {
  private int hour;
  private int minute;
  private List<NodeDTO> nodes;

  public Schedule toSchedule(List<Vector2> positions) {
    return new Schedule(positions, getHour(), getMinute());
  }
}
