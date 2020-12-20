package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.bolero.game.enums.InteractionType;
import com.bolero.game.exceptions.MissingInteractionTypeException;
import com.bolero.game.exceptions.WrongInteractionTypeException;
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

    public void createInteractionMap(String intLayer, String fallbackSpawn) throws MissingInteractionTypeException, WrongInteractionTypeException {

        MapLayer layer = map.getLayers().get(intLayer);

        if (layer != null) {
            MapObjects objects = layer.getObjects();
            for (MapObject object : objects) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                String type = object.getProperties().get("type", String.class);

                if (type == null) {
                    throw new MissingInteractionTypeException();
                }

                InteractionType interactionType;
                try {
                    interactionType = InteractionType.valueOf(type);
                } catch (IllegalArgumentException e) {
                    throw new WrongInteractionTypeException(type);
                }

                switch (interactionType) {
                    case transition:
                        generateTransitionRectangle(rectangle, object, fallbackSpawn);
                        break;
                    case inspect:
                        generateInspectRectangle(rectangle, object);
                        break;
                    default:
                        throw new WrongInteractionTypeException(type);
                }


            }
        }
    }

    private void generateTransitionRectangle(Rectangle rectangle, MapObject object, String fallbackSpawn) {
        String mapName = object.getProperties().get("map_id", String.class);
        String spawnProperty = object.getProperties().get("spawn_id", String.class);
        String spawnName = spawnProperty == null ? fallbackSpawn : spawnProperty;
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
