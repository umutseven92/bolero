package com.bolero.game.schedule;

import java.util.List;

public class ScheduleList {
  private final List<Schedule> schedules;

  public ScheduleList(List<Schedule> schedules) {
    this.schedules = schedules;
  }

  public List<Schedule> getSchedules() {
    return schedules;
  }
}
