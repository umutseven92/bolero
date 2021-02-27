package com.bolero.game;

import com.bolero.game.dtos.ClockDTO;
import com.bolero.game.managers.BundleManager;
import lombok.Getter;
import lombok.val;
import lombok.var;

public class Clock {

  private final String[] days;

  @Getter private final ClockDTO clockConfig;
  @Getter private String currentDay;
  @Getter private int currentHour;
  @Getter private int currentMinute;
  @Getter private long timestamp;

  private int dayIndex;

  public Clock(BundleManager bundle, ClockDTO clockConfig) {
    this.clockConfig = clockConfig;

    timestamp = calculateInitialTimestamp();
    days =
        new String[] {
          bundle.getString("monday"),
          bundle.getString("tuesday"),
          bundle.getString("wednesday"),
          bundle.getString("thursday"),
          bundle.getString("friday"),
          bundle.getString("saturday"),
          bundle.getString("sunday")
        };

    dayIndex = clockConfig.getStart().getDay();
    currentDay = days[dayIndex];
    currentHour = clockConfig.getStart().getTime().getHour();
    currentMinute = clockConfig.getStart().getTime().getHour();
  }

  private long calculateInitialTimestamp() {
    var ts = clockConfig.getSpeed() * clockConfig.getStart().getTime().getHour();
    ts += clockConfig.getSpeed() * (clockConfig.getStart().getTime().getHour() / 60f);

    return ts;
  }

  public void increment() {
    val speed = clockConfig.getSpeed();

    this.timestamp += 1;
    if (this.timestamp >= speed * 24L) {
      // A day has passed; reset timestamp.
      this.timestamp = 0;
      dayIndex++;
      if (dayIndex >= days.length) {
        // A week has passed; reset back to monday.
        dayIndex = 0;
      }
      currentDay = days[dayIndex];
    }

    currentHour = (int) timestamp / speed;
    currentMinute = (int) ((timestamp / (speed / 60f)) % 60f);
  }
}
