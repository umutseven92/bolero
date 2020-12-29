package com.bolero.game.controllers;

import box2dLight.ConeLight;
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
import com.bolero.game.BoleroGame;
import com.bolero.game.Clock;
import com.bolero.game.LightContainer;
import com.bolero.game.data.ConeLightValues;
import com.bolero.game.data.PointLightValues;
import com.bolero.game.enums.LightTime;
import com.bolero.game.enums.LightType;
import com.bolero.game.exceptions.MissingLightTypeException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.WrongLightTimeException;
import com.bolero.game.exceptions.WrongLightTypeException;

import java.util.ArrayList;

public class LightController implements Disposable {
    private final TiledMap map;
    private final RayHandler rayHandler;

    private final Clock clock;
    private final ArrayList<LightContainer> lights;

    private boolean previousNight;

    public LightController(TiledMap map, RayHandler rayHandler, Clock clock) {
        this.map = map;
        this.rayHandler = rayHandler;
        this.clock = clock;
        lights = new ArrayList<>();
    }

    public void map(float unit) throws MissingLightTypeException, WrongLightTypeException, MissingPropertyException, WrongLightTimeException {
        MapLayer layer = map.getLayers().get(BoleroGame.LIGHT_LAYER);

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

    public void update() {
        long timestamp = this.clock.getTimestamp();
        int ratio = Clock.RATIO;

        boolean night;
        if (timestamp >= BoleroGame.DUSK_START * ratio || timestamp < BoleroGame.DAWN_END * ratio) {
            // NIGHT
            night = true;
        } else {
            // DAY
            night = false;
        }

        // To prevent unnecessary updates
        if (night != previousNight) {
            toggleLights(night);
            previousNight = night;
        }
    }

    private void toggleLights(boolean night) {
        for (LightContainer light : lights) {
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

    private void generatePointLight(MapProperties props, float unit) throws MissingPropertyException, WrongLightTimeException {
        PointLightValues lightValues = getPointLightValues(props);
        Vector2 pos = getPosition(props, unit);

        PointLight light = new PointLight(rayHandler, lightValues.getRays(), lightValues.getColor(), lightValues.getDistance(), pos.x, pos.y);

        lights.add(new LightContainer(lightValues.getTime(), light));
    }

    private void generateConeLight(MapProperties props, float unit) throws MissingPropertyException, WrongLightTimeException {
        ConeLightValues lightValues = getConeLightValues(props);
        Vector2 pos = getPosition(props, unit);

        ConeLight light = new ConeLight(rayHandler, lightValues.getRays(), lightValues.getColor(), lightValues.getDistance(), pos.x, pos.y, lightValues.getDirectionDegree(), lightValues.getConeDegree());

        lights.add(new LightContainer(lightValues.getTime(), light));
    }

    private ConeLightValues getConeLightValues(MapProperties props) throws MissingPropertyException, WrongLightTimeException {
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
        LightTime time = getLightTime(props);

        return new ConeLightValues(distance, color, rays, directionDegree, coneDegree, time);

    }

    private PointLightValues getPointLightValues(MapProperties props) throws MissingPropertyException, WrongLightTimeException {
        checkProps(props);
        float distance = props.get("distance", float.class);
        Color color = props.get("color", Color.class);
        int rays = props.get("rays", int.class);
        LightTime time = getLightTime(props);
        return new PointLightValues(distance, color, rays, time);
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

    private LightTime getLightTime(MapProperties props) throws WrongLightTimeException {
        String time = props.get("time", String.class);

        if (time == null) {
            return LightTime.both;
        }

        LightTime lightTime;
        try {
            lightTime = LightTime.valueOf(time);
        } catch (IllegalArgumentException e) {
            throw new WrongLightTimeException(time);
        }

        return lightTime;
    }

    private Vector2 getPosition(MapProperties props, float unit) {
        float x = props.get("x", float.class) / unit;
        float y = props.get("y", float.class) / unit;

        return new Vector2(x, y);
    }

    @Override
    public void dispose() {
        for (LightContainer light : lights) {
            light.dispose();
        }
    }
}
