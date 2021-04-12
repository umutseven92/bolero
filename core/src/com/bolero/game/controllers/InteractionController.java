package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.interactions.AbstractRectangle;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import com.bolero.game.mappers.InteractionMapper;
import com.bolero.game.mixins.FileLoader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public class InteractionController implements Disposable, FileLoader {
  private List<TransitionRectangle> transitionRectangles;
  private List<InspectRectangle> inspectRectangles;

  private final InteractionMapper mapper;

  private Sound inspectSound;

  public InteractionController(TiledMap map) throws FileNotFoundException {
    transitionRectangles = new ArrayList<>();
    inspectRectangles = new ArrayList<>();
    mapper = new InteractionMapper(map);

  }


  public void playInspectSound(InspectRectangle inspectRectangle) {
    // Each inspection can have a different sound effect, so we load the sound effect each time.
    val file = inspectRectangle.getSoundFile();

    if (file == null) {
      return;
    }

    inspectSound = Gdx.audio.newSound(file);
    inspectSound.play();
  }

  public void load()
      throws MissingPropertyException, ConfigurationNotLoadedException, FileNotFoundException {
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

  @Override
  public void dispose() {
    if (inspectSound != null) {
      inspectSound.dispose();
    }
  }
}
