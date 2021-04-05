package com.bolero.game.mappers;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public class CollisionMapper extends AbstractMapper implements Mapper<List<Shape>> {
  private final World world;

  public CollisionMapper(TiledMap map, World world) {
    super(map);
    this.world = world;
  }

  @Override
  public List<Shape> map() throws MissingPropertyException, ConfigurationNotLoadedException {
    val objects =
        super.getLayer(BoleroGame.config.getConfig().getMaps().getLayers().getCollision());

    for (val object : objects) {
      val rectangle = ((RectangleMapObject) object).getRectangle();

      val shape = getShapeFromRectangle(rectangle);

      val center = getTransformedCenterForRectangle(rectangle);

      val bodyDef = new BodyDef();
      val body = world.createBody(bodyDef);

      body.createFixture(shape, 0.0f);

      body.setTransform(center, 0);

      shape.dispose();
    }

    return new ArrayList<>();
  }

  private Shape getShapeFromRectangle(Rectangle rectangle) {
    val polygonShape = new PolygonShape();
    polygonShape.setAsBox(
        rectangle.width * 0.5F / BoleroGame.UNIT, rectangle.height * 0.5F / BoleroGame.UNIT);

    return polygonShape;
  }

  private Vector2 getTransformedCenterForRectangle(Rectangle rectangle) {
    val center = new Vector2();
    rectangle.getCenter(center);
    return center.scl(1 / BoleroGame.UNIT);
  }
}
