package com.bolero.game.controllers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.interactions.AbstractRectangle;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import com.bolero.game.mappers.InteractionMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public class InteractionController {
  private List<TransitionRectangle> transitionRectangles;
  private List<InspectRectangle> inspectRectangles;

  private final InteractionMapper mapper;

  public InteractionController(TiledMap map) {
    transitionRectangles = new ArrayList<>();
    inspectRectangles = new ArrayList<>();
    mapper = new InteractionMapper(map);
  }

  public void load() throws MissingPropertyException, ConfigurationNotLoadedException {
    val rectangles = mapper.map();
    transitionRectangles = rectangles.x;
    inspectRectangles = rectangles.y;
  }

  public List<AbstractRectangle> getAllRectangles() {
    val allRectangles = new ArrayList<AbstractRectangle>(transitionRectangles);
    allRectangles.addAll(inspectRectangles);

    return allRectangles;
  }

  public TransitionRectangle checkIfInInteractionRectangle(Vector2 playerPosPixels) {
    return checkIfInTriangle(playerPosPixels, transitionRectangles);
  }

  public InspectRectangle checkIfInInspectRectangle(Vector2 playerPosPixels) {
    return checkIfInTriangle(playerPosPixels, inspectRectangles);
  }

  private <E extends AbstractRectangle> E checkIfInTriangle(
      Vector2 playerPosPixels, List<E> rectangles) {
    for (E intRectangle : rectangles) {

      if (intRectangle.getRectangle().contains(playerPosPixels)) {
        return intRectangle;
      }
    }

    return null;
  }
}
