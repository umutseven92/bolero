package com.bolero.game.data;

import com.badlogic.gdx.graphics.Color;

public abstract class BaseLightValues {
    private final float distance;
    private final Color color;
    private final int rays;

    protected BaseLightValues(float distance, Color color, int rays) {
        this.distance = distance;
        this.color = color;
        this.rays = rays;
    }

    public int getRays() {
        return rays;
    }

    public Color getColor() {
        return color;
    }

    public float getDistance() {
        return distance;
    }
}
