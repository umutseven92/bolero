package com.bolero.game.characters;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class NPC extends Character {
    public final Circle talkCircle;

    public NPC(Vector2 position, World box2DWorld, float width, float height, float maxVelocity, float speed, String texturePath) {
        super(position, box2DWorld, width, height, maxVelocity, speed, texturePath, BodyDef.BodyType.StaticBody);

        talkCircle = new Circle(position, 4f);
    }

    public Circle getTalkCircle() {
        return talkCircle;
    }

    @Override
    public void setPosition() {
        talkCircle.setPosition(this.body.getPosition());
        super.setPosition();
    }
}
