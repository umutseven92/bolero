package com.bolero.game.controllers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.MapValues;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.mappers.CollisionMapper;

import java.util.ArrayList;
import java.util.List;

public class CollisionController implements Disposable {
  private final World world;
  private final ArrayList<Shape> shapes;

  private final CollisionMapper mapper;

  public CollisionController(World world, TiledMap map) {
    this.world = world;
    mapper = new CollisionMapper(map, world);
    shapes = new ArrayList<>();
  }

  public void load(MapValues mapValues) throws MissingPropertyException {
    createWalls(mapValues);
    createCollisionsFromMap();
  }

  private void createCollisionsFromMap() throws MissingPropertyException {
    List<Shape> collisionShapes = mapper.map();
    shapes.addAll(collisionShapes);
  }

  private void createWalls(MapValues mapValues) {
    float heightModifier = mapValues.tileHeightPixels / BoleroGame.UNIT;
    float widthModifier = mapValues.tileWidthPixels / BoleroGame.UNIT;

    PolygonShape verticalMapWall = new PolygonShape();
    verticalMapWall.setAsBox(1, mapValues.mapHeightUnit);

    shapes.add(verticalMapWall);

    PolygonShape horizontalMapWall = new PolygonShape();
    horizontalMapWall.setAsBox(mapValues.mapWidthUnit, 1);

    shapes.add(verticalMapWall);

    BodyDef eastWallDef = new BodyDef();
    eastWallDef.position.set(
        new Vector2((mapValues.mapWidthUnit * widthModifier) + 1, mapValues.mapHeightUnit));

    BodyDef westWallDef = new BodyDef();
    westWallDef.position.set(-1, mapValues.mapHeightUnit);

    BodyDef northWallDef = new BodyDef();
    northWallDef.position.set(
        new Vector2(mapValues.mapWidthUnit, (mapValues.mapHeightUnit * heightModifier) + 1));

    BodyDef southWallDef = new BodyDef();
    southWallDef.position.set(mapValues.mapWidthUnit, -1);

    world.createBody(eastWallDef).createFixture(verticalMapWall, 0.0f);
    world.createBody(westWallDef).createFixture(verticalMapWall, 0.0f);
    world.createBody(northWallDef).createFixture(horizontalMapWall, 0.0f);
    world.createBody(southWallDef).createFixture(horizontalMapWall, 0.0f);
  }

  @Override
  public void dispose() {
    for (Shape shape : shapes) {
      shape.dispose();
    }
  }
}
