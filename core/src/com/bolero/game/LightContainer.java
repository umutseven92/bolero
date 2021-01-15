package com.bolero.game;

import box2dLight.Light;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.enums.LightTime;

public class LightContainer implements Disposable {
  private final LightTime time;
  private final Light light;

  public LightContainer(LightTime time, Light light) {
    this.time = time;
    this.light = light;
  }

  public void setActive(boolean active) {
    this.light.setActive(active);
  }

  @Override
  public void dispose() {
    light.dispose();
  }

  public LightTime getTime() {
    return time;
  }
}
