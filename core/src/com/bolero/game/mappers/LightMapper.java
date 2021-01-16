package com.bolero.game.mappers;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.BoleroGame;
import com.bolero.game.LightContainer;
import com.bolero.game.data.ConeLightValues;
import com.bolero.game.data.PointLightValues;
import com.bolero.game.enums.LightTime;
import com.bolero.game.enums.LightType;
import com.bolero.game.exceptions.MissingPropertyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LightMapper extends AbstractMapper implements Mapper<List<LightContainer>> {
  private final RayHandler rayHandler;

  public LightMapper(TiledMap map, RayHandler rayHandler) {
    super(map);
    this.rayHandler = rayHandler;
  }

  @Override
  public List<LightContainer> map() throws MissingPropertyException {
    MapObjects objects = super.getLayer(BoleroGame.LIGHT_LAYER);

    List<LightContainer> lights = new ArrayList<>();

    for (MapObject object : objects) {

      final MapProperties props = object.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));

      String type = props.get("type", String.class);

      LightType lightType = LightType.valueOf(type);

      LightContainer light;
      switch (lightType) {
        case point:
          lights.add(generatePointLight(props));
          break;
        case cone:
          lights.add(generateConeLight(props));
          break;
      }
    }

    return lights;
  }

  private LightContainer generatePointLight(MapProperties props) throws MissingPropertyException {
    PointLightValues lightValues = getPointLightValues(props);
    Vector2 pos = getPosition(props);

    PointLight light =
        new PointLight(
            rayHandler,
            lightValues.getRays(),
            lightValues.getColor(),
            lightValues.getDistance(),
            pos.x,
            pos.y);

    return new LightContainer(lightValues.getTime(), light);
  }

  private LightContainer generateConeLight(MapProperties props) throws MissingPropertyException {
    ConeLightValues lightValues = getConeLightValues(props);
    Vector2 pos = getPosition(props);

    ConeLight light =
        new ConeLight(
            rayHandler,
            lightValues.getRays(),
            lightValues.getColor(),
            lightValues.getDistance(),
            pos.x,
            pos.y,
            lightValues.getDirectionDegree(),
            lightValues.getConeDegree());

    return new LightContainer(lightValues.getTime(), light);
  }

  private ConeLightValues getConeLightValues(MapProperties props) throws MissingPropertyException {
    checkProps(props);
    super.checkMissingProperties(props, Arrays.asList("direction_degree", "cone_degree"));

    float distance = props.get("distance", float.class);
    Color color = props.get("color", Color.class);
    int rays = props.get("rays", int.class);
    float directionDegree = props.get("direction_degree", float.class);
    float coneDegree = props.get("cone_degree", float.class);
    LightTime time = getLightTime(props);

    return new ConeLightValues(distance, color, rays, directionDegree, coneDegree, time);
  }

  private PointLightValues getPointLightValues(MapProperties props)
      throws MissingPropertyException {
    checkProps(props);
    float distance = props.get("distance", float.class);
    Color color = props.get("color", Color.class);
    int rays = props.get("rays", int.class);
    LightTime time = getLightTime(props);
    return new PointLightValues(distance, color, rays, time);
  }

  private void checkProps(MapProperties props) throws MissingPropertyException {
    super.checkMissingProperties(props, Arrays.asList("distance", "color", "rays"));
  }

  private LightTime getLightTime(MapProperties props) {
    String time = props.get("time", String.class);

    if (time == null) {
      return LightTime.both;
    }

    return LightTime.valueOf(time);
  }

  private Vector2 getPosition(MapProperties props) {
    float x = props.get("x", float.class) / BoleroGame.UNIT;
    float y = props.get("y", float.class) / BoleroGame.UNIT;

    return new Vector2(x, y);
  }
}
