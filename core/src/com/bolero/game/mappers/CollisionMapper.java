package com.bolero.game.mappers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MissingPropertyException;

import java.util.ArrayList;
import java.util.List;

public class CollisionMapper extends AbstractMapper implements Mapper<List<Shape>> {
  private final World world;

  public CollisionMapper(TiledMap map, World world) {
    super(map);
    this.world = world;
  }

  @Override
  public List<Shape> map() throws MissingPropertyException {
    MapObjects objects = super.getLayer(BoleroGame.COL_LAYER);

    ArrayList<Shape> shapes = new ArrayList<>();

    for (MapObject object : objects) {
      Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

      Shape shape = getShapeFromRectangle(rectangle);
      shapes.add(shape);

      Vector2 center = getTransformedCenterForRectangle(rectangle);

      BodyDef bodyDef = new BodyDef();
      Body body = world.createBody(bodyDef);

      body.createFixture(shape, 0.0f);

      body.setTransform(center, 0);
    }

    return shapes;
  }

  private Shape getShapeFromRectangle(Rectangle rectangle) {
    PolygonShape polygonShape = new PolygonShape();
    polygonShape.setAsBox(
        rectangle.width * 0.5F / BoleroGame.UNIT, rectangle.height * 0.5F / BoleroGame.UNIT);

    return polygonShape;
  }

  private Vector2 getTransformedCenterForRectangle(Rectangle rectangle) {
    Vector2 center = new Vector2();
    rectangle.getCenter(center);
    return center.scl(1 / BoleroGame.UNIT);
  }
}
