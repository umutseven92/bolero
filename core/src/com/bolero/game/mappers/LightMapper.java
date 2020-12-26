package com.bolero.game.mappers;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.data.ConeLightValues;
import com.bolero.game.data.PointLightValues;
import com.bolero.game.enums.LightType;
import com.bolero.game.exceptions.MissingLightTypeException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.WrongLightTypeException;

import java.util.ArrayList;

public class LightMapper implements Disposable {
    private final TiledMap map;
    private final RayHandler rayHandler;

    private final ArrayList<Light> lights;

    public LightMapper(TiledMap map, RayHandler rayHandler) {
        this.map = map;
        this.rayHandler = rayHandler;
        lights = new ArrayList<>();
    }

    public void map(String lightLayer, float unit) throws MissingLightTypeException, WrongLightTypeException, MissingPropertyException {
        MapLayer layer = map.getLayers().get(lightLayer);

        if (layer == null) {
            return;
        }

        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {

            final MapProperties props = object.getProperties();

            String type = props.get("type", String.class);

            if (type == null) {
                throw new MissingLightTypeException();
            }

            LightType lightType;
            try {
                lightType = LightType.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new WrongLightTypeException(type);
            }

            switch (lightType) {
                case point:
                    generatePointLight(props, unit);
                    break;
                case cone:
                    generateConeLight(props, unit);
                    break;
                default:
                    throw new WrongLightTypeException(type);
            }
        }
    }

    private void generatePointLight(MapProperties props, float unit) throws MissingPropertyException {
        PointLightValues lightValues = getPointLightValues(props);
        Vector2 pos = getPosition(props, unit);

        PointLight light = new PointLight(rayHandler, lightValues.getRays(), lightValues.getColor(), lightValues.getDistance(), pos.x, pos.y);

        lights.add(light);
    }

    private void generateConeLight(MapProperties props, float unit) throws MissingPropertyException {
        ConeLightValues lightValues = getConeLightValues(props);
        Vector2 pos = getPosition(props, unit);

        ConeLight light = new ConeLight(rayHandler, lightValues.getRays(), lightValues.getColor(), lightValues.getDistance(), pos.x, pos.y, lightValues.getDirectionDegree(), lightValues.getConeDegree());

        lights.add(light);
    }

    private ConeLightValues getConeLightValues(MapProperties props) throws MissingPropertyException {
        checkProps(props);
        if (!props.containsKey("direction_degree")) {
            throw new MissingPropertyException("direction_degree");
        }

        if (!props.containsKey("cone_degree")) {
            throw new MissingPropertyException("cone_degree");
        }

        float distance = props.get("distance", float.class);
        Color color = props.get("color", Color.class);
        int rays = props.get("rays", int.class);
        float directionDegree = props.get("direction_degree", float.class);
        float coneDegree = props.get("cone_degree", float.class);

        return new ConeLightValues(distance, color, rays, directionDegree, coneDegree);

    }

    private PointLightValues getPointLightValues(MapProperties props) throws MissingPropertyException {
        checkProps(props);
        float distance = props.get("distance", float.class);
        Color color = props.get("color", Color.class);
        int rays = props.get("rays", int.class);

        return new PointLightValues(distance, color, rays);
    }

    private void checkProps(MapProperties props) throws MissingPropertyException {
        if (!props.containsKey("distance")) {
            throw new MissingPropertyException("distance");
        }

        if (!props.containsKey("color")) {
            throw new MissingPropertyException("color");
        }

        if (!props.containsKey("rays")) {
            throw new MissingPropertyException("rays");
        }
    }

    private Vector2 getPosition(MapProperties props, float unit) {
        float x = props.get("x", float.class) / unit;
        float y = props.get("y", float.class) / unit;

        return new Vector2(x, y);
    }

    @Override
    public void dispose() {
        for (Light light : lights) {
            light.dispose();
        }
    }
}
