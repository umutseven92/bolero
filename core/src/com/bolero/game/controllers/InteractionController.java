package com.bolero.game.controllers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.data.Tuple;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.InteractionRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import com.bolero.game.mappers.InteractionMapper;

import java.util.ArrayList;
import java.util.List;

public class InteractionController {
  private List<TransitionRectangle> transitionRectangles;
  private List<InspectRectangle> inspectRectangles;

  private final InteractionMapper mapper;

  public InteractionController(TiledMap map) {
    transitionRectangles = new ArrayList<>();
    inspectRectangles = new ArrayList<>();
    mapper = new InteractionMapper(map);
  }

  public void load() throws MissingPropertyException {
    Tuple<List<TransitionRectangle>, List<InspectRectangle>> rectangles = mapper.map();
    transitionRectangles = rectangles.x;
    inspectRectangles = rectangles.y;
  }

  public List<InteractionRectangle> getAllRectangles() {
    List<InteractionRectangle> allRectangles = new ArrayList<>(transitionRectangles);
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
      Vector2 playerPosPixels, List<E> rectangles) {
    for (E intRectangle : rectangles) {

      if (intRectangle.getRectangle().contains(playerPosPixels)) {
        return intRectangle;
      }
    }

    return null;
  }
}
