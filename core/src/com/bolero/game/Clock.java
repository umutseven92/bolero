package com.bolero.game;

import com.bolero.game.managers.BundleManager;
import lombok.Getter;

public class Clock {

  // Determines how much real time (in ms) needs to pass for one hour to pass in game
  public static final int RATIO = 200;
  private static final int STEP = 1;

  private final String[] days;

  @Getter private String currentDay;
  @Getter private int currentHour;
  @Getter private int currentMinute;
  @Getter private long timestamp;

  private int dayIndex;

  public Clock(BundleManager bundle) {
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
    this.timestamp += STEP;
    if (this.timestamp >= RATIO * 24) {
      this.timestamp = 0;
      dayIndex++;
      if (dayIndex >= days.length) {
        dayIndex = 0;
      }
      currentDay = days[dayIndex];
    }

    currentHour = (int) timestamp / RATIO;
    currentMinute = (int) ((timestamp / (RATIO / 60f)) % 60f);
  }
}
