package com.bolero.game;

import com.badlogic.gdx.math.Rectangle;

public class InteractionRectangle {

    private final String mapName;
    private final String spawnName;
    private final Rectangle rectangle;
    private final InteractionType type;

    public InteractionRectangle(String mapName, String spawnName, InteractionType type, Rectangle rectangle) {
        this.mapName = mapName;
        this.spawnName = spawnName;
        this.rectangle = rectangle;
        this.type = type;
    }

    public String getName() {
        return mapName;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public String getSpawnName() {
        return spawnName;
    }

    public InteractionType getType() {
        return type;
    }
}
