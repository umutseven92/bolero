package com.bolero.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

public class CollisionMap implements Disposable {

    private final World world;
    private final TiledMap map;
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public CollisionMap(World world, TiledMap map) {
        this.world = world;
        this.map = map;
    }

    private Shape getShapeFromRectangle(Rectangle rectangle, float unit) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.width * 0.5F / unit, rectangle.height * 0.5F / unit);

        shapes.add(polygonShape);
        return polygonShape;
    }

    private Vector2 getTransformedCenterForRectangle(Rectangle rectangle, float unit) {
        Vector2 center = new Vector2();
        rectangle.getCenter(center);
        return center.scl(1 / unit);
    }

    public void createCollisions(float unit, MapValues mapValues) {
        createWalls(unit, mapValues);
        createCollisionsFromMap(unit);
    }

    private void createCollisionsFromMap(float unit) {
        MapObjects objects = map.getLayers().get("Collision").getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            //create a dynamic within the world body (also can be KinematicBody or StaticBody
            BodyDef bodyDef = new BodyDef();
            Body body = world.createBody(bodyDef);

            //create a fixture for each body from the shape
            body.createFixture(getShapeFromRectangle(rectangle, unit), 0.0f);

            //setting the position of the body's origin. In this case with zero rotation
            body.setTransform(getTransformedCenterForRectangle(rectangle, unit), 0);
        }
    }

    private void createWalls(float unit, MapValues mapValues) {
        float heightModifier = mapValues.tileHeightPixels / unit;
        float widthModifier = mapValues.tileWidthPixels / unit;

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
