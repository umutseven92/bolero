package com.bolero.game.data;

import com.badlogic.gdx.graphics.Color;
import com.bolero.game.enums.LightTime;

public abstract class BaseLightValues {
    private final float distance;
    private final Color color;
    private final int rays;
    private final LightTime time;

    protected BaseLightValues(float distance, Color color, int rays, LightTime time) {
        this.distance = distance;
        this.color = color;
        this.rays = rays;
        this.time = time;
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

    public LightTime getTime() {
        return time;
    }
}
