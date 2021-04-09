package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.Tuple;
import com.bolero.game.enums.InteractionType;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import com.bolero.game.mixins.FileLoader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.val;

public class InteractionMapper extends AbstractMapper
    implements Mapper<Tuple<List<TransitionRectangle>, List<InspectRectangle>>>, FileLoader {

  public InteractionMapper(TiledMap map) {
    super(map);
  }

  @Override
  public Tuple<List<TransitionRectangle>, List<InspectRectangle>> map()
      throws MissingPropertyException, ConfigurationNotLoadedException, FileNotFoundException {
    val objects =
        super.getLayer(BoleroGame.getConfig().getMaps().getLayers().getInteraction());

    val transitionRectangles = new ArrayList<TransitionRectangle>();
    val inspectRectangles = new ArrayList<InspectRectangle>();

    for (val object : objects) {
      val rectangle = ((RectangleMapObject) object).getRectangle();
      val props = object.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));

      val type = props.get("type", String.class);
      val interactionType = InteractionType.valueOf(type);

      val hidden = props.get("hidden", false, Boolean.class);

      switch (interactionType) {
        case transition:
          super.checkMissingProperties(props, Collections.singletonList("map_id"));
          val transitionRectangle = generateTransitionRectangle(rectangle, hidden, props);

          transitionRectangles.add(transitionRectangle);
          break;
        case inspect:
          super.checkMissingProperties(props, Collections.singletonList("string_id"));
          val inspectRectangle = generateInspectRectangle(rectangle, hidden, props);

          val sound = props.get("sound", String.class);
          if (sound != null) {
            val fullPath = String.format("sound_effects/%s", sound);
            val file = getFile(fullPath);
            inspectRectangle.setSoundFile(file);
          }
          inspectRectangles.add(inspectRectangle);
          break;
      }
    }

    return new Tuple<>(transitionRectangles, inspectRectangles);
  }

  private TransitionRectangle generateTransitionRectangle(
      Rectangle rectangle, boolean hidden, MapProperties props) {

    val mapName = props.get("map_id", String.class);

    val spawnProperty = props.get("spawn_id", String.class);
    val spawnName = spawnProperty == null ? BoleroGame.SPAWN_INITIAL_OBJ : spawnProperty;

    return new TransitionRectangle(mapName, spawnName, rectangle, hidden);
  }

  private InspectRectangle generateInspectRectangle(
      Rectangle rectangle, boolean hidden, MapProperties props) {
    val stringID = props.get("string_id", String.class);

    return new InspectRectangle(rectangle, stringID, hidden);
  }
}
