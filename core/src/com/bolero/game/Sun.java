package com.bolero.game;

import box2dLight.RayHandler;

public class Sun {
  private static final float NIGHT = 0.3f;
  private static final float DAY = 1f;

  private final RayHandler rayHandler;
  private final Clock clock;

  private float previousAlpha;

  public Sun(RayHandler rayHandler, Clock clock) {
    this.rayHandler = rayHandler;
    this.clock = clock;
  }

  public void update(float darkenAmount) {
    long timestamp = this.clock.getTimestamp();
    int ratio = Clock.RATIO;

    float alpha;
    if (timestamp >= BoleroGame.DUSK_END * ratio || timestamp < BoleroGame.DAWN_START * ratio) {
      // NIGHT
      alpha = NIGHT;
    } else if (timestamp >= BoleroGame.DAWN_END * ratio
        && timestamp < BoleroGame.DUSK_START * ratio) {
      // DAY
      alpha = DAY;
    } else if (timestamp >= BoleroGame.DAWN_START * ratio
        && timestamp < BoleroGame.DAWN_END * ratio) {
      // DAWN
      // Represented as a linear function (y = mx + c), with x being alpha and y being the
      // timestamp.
      float m = ((BoleroGame.DAWN_END * ratio) - (BoleroGame.DAWN_START * ratio)) / (DAY - NIGHT);
      float c = (BoleroGame.DAWN_START * ratio) - (m * NIGHT);
      alpha = (timestamp - c) / m;
    } else {
      // DUSK
      // Similar to dawn, but goes from DAY to NIGHT.
      float m = ((BoleroGame.DUSK_END * ratio) - (BoleroGame.DUSK_START * ratio)) / (NIGHT - DAY);
      float c = (BoleroGame.DUSK_START * ratio) - (m * DAY);
      alpha = (timestamp - c) / m;
    }

    alpha -= darkenAmount;

    // To prevent unnecessary updates
    if (alpha != previousAlpha) {
      rayHandler.setAmbientLight(0f, 0f, 0f, alpha);
      previousAlpha = alpha;
    }
  }
}
