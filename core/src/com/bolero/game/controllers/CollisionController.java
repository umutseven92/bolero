package com.bolero.game.controllers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.MapValues;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.mappers.CollisionMapper;
import lombok.val;

public class CollisionController implements Disposable {
  private final World world;

  private final CollisionMapper mapper;

  public CollisionController(World world, TiledMap map) {
    this.world = world;
    mapper = new CollisionMapper(map, world);
  }

  public void load(MapValues mapValues)
      throws MissingPropertyException, ConfigurationNotLoadedException {
    createWalls(mapValues);
    createCollisionsFromMap();
  }

  private void createCollisionsFromMap()
      throws MissingPropertyException, ConfigurationNotLoadedException {
    mapper.map();
  }

  private void createWalls(MapValues mapValues) {
    float heightModifier = mapValues.getTileHeightPixels() / BoleroGame.UNIT;
    float widthModifier = mapValues.getTileWidthPixels() / BoleroGame.UNIT;

    val verticalMapWall = new PolygonShape();
    verticalMapWall.setAsBox(1, mapValues.getMapHeightUnit());

    val horizontalMapWall = new PolygonShape();
    horizontalMapWall.setAsBox(mapValues.getMapWidthUnit(), 1);

    val eastWallDef = new BodyDef();
    eastWallDef.position.set(
        new Vector2(
            (mapValues.getMapWidthUnit() * widthModifier) + 1, mapValues.getMapHeightUnit()));

    val westWallDef = new BodyDef();
    westWallDef.position.set(-1, mapValues.getMapHeightUnit());

    val northWallDef = new BodyDef();
    northWallDef.position.set(
        new Vector2(
            mapValues.getMapWidthUnit(), (mapValues.getMapHeightUnit() * heightModifier) + 1));

    val southWallDef = new BodyDef();
    southWallDef.position.set(mapValues.getMapWidthUnit(), -1);

    world.createBody(eastWallDef).createFixture(verticalMapWall, 0.0f);
    world.createBody(westWallDef).createFixture(verticalMapWall, 0.0f);
    world.createBody(northWallDef).createFixture(horizontalMapWall, 0.0f);
    world.createBody(southWallDef).createFixture(horizontalMapWall, 0.0f);

    verticalMapWall.dispose();
    horizontalMapWall.dispose();
  }

  @Override
  public void dispose() {}
}
