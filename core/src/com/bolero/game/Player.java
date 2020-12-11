package com.bolero.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Character {

    public Player(Vector2 position, World box2DWorld) {
        super(position, box2DWorld, 2.2f, 2, 5.5f, 0.7f, "tim.png", BodyDef.BodyType.DynamicBody);
    }

}
