package com.bolero.game.data;

import com.badlogic.gdx.graphics.Color;
import com.bolero.game.enums.LightTime;

public class PointLightValues extends BaseLightValues {
    public PointLightValues(float distance, Color color, int rays, LightTime time) {
        super(distance, color, rays, time);
    }
}
