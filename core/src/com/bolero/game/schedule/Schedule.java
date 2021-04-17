package com.bolero.game.schedule;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import lombok.Data;

@Data
public class Schedule {
  private final List<Vector2> positions;
  private final int hour;
  private final int minute;


  public Schedule(List<Vector2> positions, int hour, int minute) {
    this.positions = positions;
    this.hour = hour;
    this.minute = minute;
  }
}
