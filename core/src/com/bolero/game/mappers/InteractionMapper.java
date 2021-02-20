package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.Tuple;
import com.bolero.game.enums.InteractionType;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.interactions.TransitionRectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.val;

public class InteractionMapper extends AbstractMapper
    implements Mapper<Tuple<List<TransitionRectangle>, List<InspectRectangle>>> {

  public InteractionMapper(TiledMap map) {
    super(map);
  }

  @Override
  public Tuple<List<TransitionRectangle>, List<InspectRectangle>> map()
      throws MissingPropertyException {
    val objects = super.getLayer(BoleroGame.INT_LAYER);

    val transitionRectangles = new ArrayList<TransitionRectangle>();
    val inspectRectangles = new ArrayList<InspectRectangle>();

    for (val object : objects) {
      val rectangle = ((RectangleMapObject) object).getRectangle();
      val props = object.getProperties();

      super.checkMissingProperties(props, Collections.singletonList("type"));

      val type = props.get("type", String.class);

      val interactionType = InteractionType.valueOf(type);

      switch (interactionType) {
        case transition:
          super.checkMissingProperties(props, Collections.singletonList("map_id"));
          val transitionRectangle = generateTransitionRectangle(rectangle, props);
          transitionRectangles.add(transitionRectangle);
          break;
        case inspect:
          super.checkMissingProperties(props, Collections.singletonList("string_id"));
          val inspectRectangle = generateInspectRectangle(rectangle, props);
          inspectRectangles.add(inspectRectangle);
          break;
      }
    }

    return new Tuple<>(transitionRectangles, inspectRectangles);
  }

  private TransitionRectangle generateTransitionRectangle(
      Rectangle rectangle, MapProperties props) {

    val mapName = props.get("map_id", String.class);

    val spawnProperty = props.get("spawn_id", String.class);
    val spawnName = spawnProperty == null ? BoleroGame.SPAWN_INITIAL_OBJ : spawnProperty;

    return new TransitionRectangle(mapName, spawnName, rectangle);
  }

  private InspectRectangle generateInspectRectangle(Rectangle rectangle, MapProperties props) {
    val stringID = props.get("string_id", String.class);

    return new InspectRectangle(rectangle, stringID);
  }
}
