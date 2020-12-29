package com.bolero.game.controllers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.BoleroGame;
import com.bolero.game.enums.InteractionType;
import com.bolero.game.exceptions.MissingInteractionTypeException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.WrongInteractionTypeException;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.InteractionRectangle;
import com.bolero.game.interactions.TransitionRectangle;

import java.util.ArrayList;

public class InteractionController {
    private final TiledMap map;
    private final ArrayList<TransitionRectangle> transitionRectangles;
    private final ArrayList<InspectRectangle> inspectRectangles;

    public InteractionController(TiledMap map) {
        this.map = map;
        transitionRectangles = new ArrayList<>();
        inspectRectangles = new ArrayList<>();
    }

    public void map() throws MissingInteractionTypeException, WrongInteractionTypeException, MissingPropertyException {

        MapLayer layer = map.getLayers().get(BoleroGame.INT_LAYER);

        if (layer == null) {
            return;
        }

        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            MapProperties props = object.getProperties();

            String type = props.get("type", String.class);

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
                    generateTransitionRectangle(rectangle, props);
                    break;
                case inspect:
                    generateInspectRectangle(rectangle, props);
                    break;
                default:
                    throw new WrongInteractionTypeException(type);
            }
        }
    }

    private void generateTransitionRectangle(Rectangle rectangle, MapProperties props) throws MissingPropertyException {
        if (!props.containsKey("map_id")) {
            throw new MissingPropertyException("map_id");
        }

        String mapName = props.get("map_id", String.class);

        String spawnProperty = props.get("spawn_id", String.class);
        String spawnName = spawnProperty == null ? BoleroGame.SPAWN_INITIAL_OBJ : spawnProperty;
        transitionRectangles.add(new TransitionRectangle(mapName, spawnName, rectangle));
    }

    private void generateInspectRectangle(Rectangle rectangle, MapProperties props) throws MissingPropertyException {
        if (!props.containsKey("string_id")) {
            throw new MissingPropertyException("string_id");
        }

        String stringID = props.get("string_id", String.class);

        inspectRectangles.add(new InspectRectangle(rectangle, stringID));
    }

    public ArrayList<InteractionRectangle> getAllRectangles() {
        ArrayList<InteractionRectangle> allRectangles = new ArrayList<InteractionRectangle>(transitionRectangles);
        allRectangles.addAll(inspectRectangles);

        return allRectangles;
    }

    public TransitionRectangle checkIfInInteractionRectangle(Vector2 playerPosPixels) {
        return checkIfInTriangle(playerPosPixels, transitionRectangles);
    }

    public InspectRectangle checkIfInInspectRectangle(Vector2 playerPosPixels) {
        return checkIfInTriangle(playerPosPixels, inspectRectangles);
    }

    private <E extends InteractionRectangle> E checkIfInTriangle(Vector2 playerPosPixels, ArrayList<E> rectangles) {
        for (E intRectangle : rectangles) {

            if (intRectangle.getRectangle().contains(playerPosPixels)) {
                return intRectangle;
            }
        }

        return null;
    }
}
