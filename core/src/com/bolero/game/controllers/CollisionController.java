package com.bolero.game.controllers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.MapValues;

import java.util.ArrayList;

public class CollisionController extends BaseMapper implements Disposable {
    private final World world;
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public CollisionController(World world, TiledMap map) {
        super(map);
        this.world = world;
    }

    private Shape getShapeFromRectangle(Rectangle rectangle) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.width * 0.5F / BoleroGame.UNIT, rectangle.height * 0.5F / BoleroGame.UNIT);

        shapes.add(polygonShape);
        return polygonShape;
    }

    private Vector2 getTransformedCenterForRectangle(Rectangle rectangle) {
        Vector2 center = new Vector2();
        rectangle.getCenter(center);
        return center.scl(1 / BoleroGame.UNIT);
    }

    public void map(MapValues mapValues) {
        createWalls(mapValues);
        createCollisionsFromMap();
    }

    private void createCollisionsFromMap() {
        MapObjects objects = super.getLayer(BoleroGame.COL_LAYER);

        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            BodyDef bodyDef = new BodyDef();
            Body body = world.createBody(bodyDef);

            body.createFixture(getShapeFromRectangle(rectangle), 0.0f);

            body.setTransform(getTransformedCenterForRectangle(rectangle), 0);
        }
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
        eastWallDef.position.set(new Vector2((mapValues.mapWidthUnit * widthModifier) + 1, mapValues.mapHeightUnit));

        BodyDef westWallDef = new BodyDef();
        westWallDef.position.set(-1, mapValues.mapHeightUnit);

        BodyDef northWallDef = new BodyDef();
        northWallDef.position.set(new Vector2(mapValues.mapWidthUnit, (mapValues.mapHeightUnit * heightModifier) + 1));

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
