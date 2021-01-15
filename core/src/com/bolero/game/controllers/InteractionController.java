package com.bolero.game.controllers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.BoleroGame;
import com.bolero.game.enums.InteractionType;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.InteractionRectangle;
import com.bolero.game.interactions.TransitionRectangle;

import java.util.ArrayList;
import java.util.Collections;

public class InteractionController extends BaseMapper {
  private final ArrayList<TransitionRectangle> transitionRectangles;
  private final ArrayList<InspectRectangle> inspectRectangles;

  public InteractionController(TiledMap map) {
    super(map);
    transitionRectangles = new ArrayList<>();
    inspectRectangles = new ArrayList<>();
  }

  public void map() throws MissingPropertyException {

    MapObjects objects = super.getLayer(BoleroGame.INT_LAYER);

    for (MapObject object : objects) {
      Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
      MapProperties props = object.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));

      String type = props.get("type", String.class);

      InteractionType interactionType = InteractionType.valueOf(type);

      switch (interactionType) {
        case transition:
          super.checkMissingProperties(props, Collections.singletonList("map_id"));
          generateTransitionRectangle(rectangle, props);
          break;
        case inspect:
          super.checkMissingProperties(props, Collections.singletonList("string_id"));
          generateInspectRectangle(rectangle, props);
          break;
      }
    }
  }

  private void generateTransitionRectangle(Rectangle rectangle, MapProperties props) {

    String mapName = props.get("map_id", String.class);

    String spawnProperty = props.get("spawn_id", String.class);
    String spawnName = spawnProperty == null ? BoleroGame.SPAWN_INITIAL_OBJ : spawnProperty;
    transitionRectangles.add(new TransitionRectangle(mapName, spawnName, rectangle));
  }

  private void generateInspectRectangle(Rectangle rectangle, MapProperties props) {
    String stringID = props.get("string_id", String.class);

    inspectRectangles.add(new InspectRectangle(rectangle, stringID));
  }

  public ArrayList<InteractionRectangle> getAllRectangles() {
    ArrayList<InteractionRectangle> allRectangles =
        new ArrayList<InteractionRectangle>(transitionRectangles);
    allRectangles.addAll(inspectRectangles);

    return allRectangles;
  }

  public TransitionRectangle checkIfInInteractionRectangle(Vector2 playerPosPixels) {
    return checkIfInTriangle(playerPosPixels, transitionRectangles);
  }

  public InspectRectangle checkIfInInspectRectangle(Vector2 playerPosPixels) {
    return checkIfInTriangle(playerPosPixels, inspectRectangles);
  }

  private <E extends InteractionRectangle> E checkIfInTriangle(
      Vector2 playerPosPixels, ArrayList<E> rectangles) {
    for (E intRectangle : rectangles) {

      if (intRectangle.getRectangle().contains(playerPosPixels)) {
        return intRectangle;
      }
    }

    return null;
  }
}
