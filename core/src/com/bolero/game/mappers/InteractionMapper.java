package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.bolero.game.InteractionRectangle;
import com.bolero.game.InteractionType;

import java.util.ArrayList;

public class InteractionMapper {
    private final TiledMap map;
    private ArrayList<InteractionRectangle> interactions;

    public InteractionMapper(TiledMap map) {
        this.map = map;
    }

    public void createInteractionMap(String intLayer, String fallbackSpawn) {
        ArrayList<InteractionRectangle> rectangles = new ArrayList<>();

        MapLayer layer = map.getLayers().get(intLayer);

        if (layer != null) {
            MapObjects objects = layer.getObjects();
            for (MapObject object : objects) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                String type = object.getProperties().get("type", String.class);

                String spawnName;
                String mapName;
                String fullName = object.getName();
                if (!fullName.contains(":")) {
                    mapName = fullName;
                    spawnName = fallbackSpawn;
                } else {
                    String[] split = fullName.split(":");
                    mapName = split[0];
                    spawnName = split[1];
                }

                rectangles.add(new InteractionRectangle(mapName, spawnName, InteractionType.valueOf(type), rectangle));
            }
        }


        interactions = rectangles;
    }

    public ArrayList<InteractionRectangle> getInteractions() {
        return interactions;
    }
}
