package com.bolero.game.data;

import com.badlogic.gdx.graphics.Color;
import com.bolero.game.enums.LightTime;

public class ConeLightValues extends BaseLightValues {
    private final float directionDegree;
    private final float coneDegree;

    public ConeLightValues(float distance, Color color, int rays, float directionDegree, float coneDegree, LightTime time) {
        super(distance, color, rays, time);
        this.directionDegree = directionDegree;
        this.coneDegree = coneDegree;
    }

    public float getDirectionDegree() {
        return directionDegree;
    }

    public float getConeDegree() {
        return coneDegree;
    }
}
