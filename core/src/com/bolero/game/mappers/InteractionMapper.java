package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.bolero.game.enums.InteractionType;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.InteractionRectangle;
import com.bolero.game.interactions.SpawnRectangle;

import java.util.ArrayList;

public class InteractionMapper {
    private final TiledMap map;
    private final ArrayList<SpawnRectangle> spawnRectangles;
    private final ArrayList<InspectRectangle> inspectRectangles;

    public InteractionMapper(TiledMap map) {
        this.map = map;
        spawnRectangles = new ArrayList<>();
        inspectRectangles = new ArrayList<>();
    }

    public void createInteractionMap(String intLayer, String fallbackSpawn) {

        MapLayer layer = map.getLayers().get(intLayer);

        if (layer != null) {
            MapObjects objects = layer.getObjects();
            for (MapObject object : objects) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                String type = object.getProperties().get("type", String.class);
                InteractionType interactionType = InteractionType.valueOf(type);

                switch (interactionType) {
                    case spawn:
                        generateSpawnRectangle(rectangle, object, fallbackSpawn);
                        break;
                    case inspect:
                        generateInspectRectangle(rectangle, object);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + interactionType);
                }


            }
        }
    }

    private void generateSpawnRectangle(Rectangle rectangle, MapObject object, String fallbackSpawn) {
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

        spawnRectangles.add(new SpawnRectangle(mapName, spawnName, rectangle));
    }

    private void generateInspectRectangle(Rectangle rectangle, MapObject object) {
        String stringID = object.getProperties().get("string_id", String.class);

        inspectRectangles.add(new InspectRectangle(rectangle, stringID));
    }

    public ArrayList<InteractionRectangle> getAllRectangles() {
        ArrayList<InteractionRectangle> allRectangles = new ArrayList<InteractionRectangle>(spawnRectangles);
        allRectangles.addAll(inspectRectangles);

        return allRectangles;
    }

    public ArrayList<SpawnRectangle> getSpawnRectangles() {
        return spawnRectangles;
    }

    public ArrayList<InspectRectangle> getInspectRectangles() {
        return inspectRectangles;
    }
}
