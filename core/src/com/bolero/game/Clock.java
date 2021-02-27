package com.bolero.game;

import com.bolero.game.managers.BundleManager;
import lombok.Getter;

public class Clock {

  private final String[] days;

  @Getter private final int speed;
  @Getter private String currentDay;
  @Getter private int currentHour;
  @Getter private int currentMinute;
  @Getter private long timestamp;

  private int dayIndex;

  public Clock(BundleManager bundle, int speed) {
    this.speed = speed;

    timestamp = 0;
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

    dayIndex = 0;
    currentDay = days[dayIndex];
    currentHour = 0;
    currentMinute = 0;
  }

  public void increment() {
    this.timestamp += 1;
    if (this.timestamp >= speed * 24L) {
      this.timestamp = 0;
      dayIndex++;
      if (dayIndex >= days.length) {
        dayIndex = 0;
      }
      currentDay = days[dayIndex];
    }

    currentHour = (int) timestamp / speed;
    currentMinute = (int) ((timestamp / (speed / 60f)) % 60f);
  }
}
