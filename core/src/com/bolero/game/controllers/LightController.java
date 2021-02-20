package com.bolero.game.controllers;

import box2dLight.RayHandler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.Clock;
import com.bolero.game.LightContainer;
import com.bolero.game.enums.LightTime;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.mappers.LightMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public class LightController implements Disposable {

  private final Clock clock;
  private List<LightContainer> lights;
  private boolean previousNight;
  private final LightMapper mapper;

  public LightController(TiledMap map, RayHandler rayHandler, Clock clock) {
    this.clock = clock;
    lights = new ArrayList<>();
    mapper = new LightMapper(map, rayHandler);
  }

  public void load() throws MissingPropertyException {
    lights = mapper.map();
  }

  public void update(Boolean force) {
    long timestamp = this.clock.getTimestamp();
    int ratio = Clock.RATIO;

    boolean night;

    //noinspection RedundantIfStatement
    if (timestamp >= BoleroGame.DUSK_START * ratio || timestamp < BoleroGame.DAWN_END * ratio) {
      // NIGHT
      night = true;
    } else {
      // DAY
      night = false;
    }

    // To prevent unnecessary updates
    if (force || night != previousNight) {
      toggleLights(night);
      previousNight = night;
    }
  }

  public void update() {
    update(false);
  }

  private void toggleLights(boolean night) {
    for (val light : lights) {
      if (night) {
        if (light.getTime() == LightTime.night) {
          light.setActive(true);
        } else if (light.getTime() == LightTime.day) {
          light.setActive(false);
        }
      } else {
        if (light.getTime() == LightTime.day) {
          light.setActive(true);
        } else if (light.getTime() == LightTime.night) {
          light.setActive(false);
        }
      }
      if (light.getTime() == LightTime.both) {
        light.setActive(true);
      }
    }
  }

  @Override
  public void dispose() {
    for (val light : lights) {
      light.dispose();
    }
  }
}
