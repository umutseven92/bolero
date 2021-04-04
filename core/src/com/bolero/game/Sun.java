package com.bolero.game;

import box2dLight.RayHandler;
import com.bolero.game.dtos.SunDTO;
import lombok.val;

public class Sun {
  private final RayHandler rayHandler;
  private final Clock clock;

  private final float nightLight;
  private final float dayLight;
  private final float dawnStart;
  private final float dawnEnd;
  private final float duskStart;
  private final float duskEnd;

  private float previousAlpha;

  public Sun(RayHandler rayHandler, Clock clock, SunDTO sunDTO) {
    this.rayHandler = rayHandler;
    this.clock = clock;

    val speed = clock.getClockConfig().getSpeed();

    nightLight = sunDTO.getNightLight();
    dayLight = sunDTO.getDayLight();
    dawnStart = sunDTO.getDawnStart() * speed;
    dawnEnd = sunDTO.getDawnEnd() * speed;
    duskStart = sunDTO.getDuskStart() * speed;
    duskEnd = sunDTO.getDuskEnd() * speed;
  }

  public boolean isNight() {
    long timestamp = this.clock.getTimestamp();
    return timestamp >= duskEnd || timestamp < dawnStart;
  }

  public boolean isDay() {
    long timestamp = this.clock.getTimestamp();
    return timestamp >= dawnEnd && timestamp < duskStart;
  }

  public boolean isDawn() {
    long timestamp = this.clock.getTimestamp();
    return timestamp >= dawnStart && timestamp < dawnEnd;
  }

  public void update() {
    long timestamp = this.clock.getTimestamp();

    float alpha;
    if (isNight()) {
      // NIGHT
      alpha = nightLight;
    } else if (isDay()) {
      // DAY
      alpha = dayLight;
    } else if (isDawn()) {
      // DAWN
      // Represented as a linear function (y = mx + c), with x being alpha and y being the
      // timestamp.
      float m = ((dawnEnd) - (dawnStart)) / (dayLight - nightLight);
      float c = (dawnStart) - (m * nightLight);
      alpha = (timestamp - c) / m;
    } else {
      // DUSK
      // Similar to dawn, but goes from DAY to NIGHT.
      float m = ((duskEnd) - (duskStart)) / (nightLight - dayLight);
      float c = (duskStart) - (m * dayLight);
      alpha = (timestamp - c) / m;
    }

    // To prevent unnecessary updates
    if (alpha != previousAlpha) {
      rayHandler.setAmbientLight(0f, 0f, 0f, alpha);
      previousAlpha = alpha;
    }
  }
}
