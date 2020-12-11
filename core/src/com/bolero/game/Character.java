package com.bolero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

abstract public class Character implements Disposable {

    private final float MAX_VELOCITY;
    private final float SPEED;

    private final float width;
    private final float height;

    private final Texture texture;
    private final Sprite sprite;
    protected final Body body;

    private CircleShape circle;

    private Vector2 position;

    public Vector2 getPosition() {
        return position;
    }

    public Character(Vector2 position, World box2DWorld, float width, float height, float maxVelocity, float speed, String texturePath, BodyDef.BodyType bodyType) {
        this.width = width;
        this.height = height;
        this.MAX_VELOCITY = maxVelocity;
        this.SPEED = speed;
        this.position = position;
        this.texture = new Texture(Gdx.files.internal(texturePath));

        this.sprite = new Sprite(texture);
        sprite.setSize(width, height);
        body = createPlayerBody(box2DWorld, bodyType);
    }

    public void setPosition() {
        this.position = this.body.getPosition();
        this.sprite.setPosition(this.position.x - this.width / 2, this.position.y - this.height / 2);
    }

    private Body createPlayerBody(World world, BodyDef.BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(position.x + width / 2, position.y + height / 2);

        Body body = world.createBody(bodyDef);

        circle = new CircleShape();
        circle.setRadius(1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.81f;
        fixtureDef.restitution = 0.3f;

        body.createFixture(fixtureDef);

        return body;
    }

    public void respawn(Vector2 position) {
        this.position = position;
        this.body.setLinearVelocity(0, 0);
        this.body.setTransform(position, this.body.getAngle());
        this.sprite.setPosition(position.x, position.y);
    }

    public void applyRightMovement() {
        if (sprite.isFlipX()) {
            sprite.flip(true, false);
        }

        Vector2 vel = body.getLinearVelocity();

        if (vel.x > MAX_VELOCITY) {
            return;
        }

        applyMovement(SPEED, 0);
    }

    public void applyLeftMovement() {
        if (!sprite.isFlipX()) {
            sprite.flip(true, false);
        }

        Vector2 vel = body.getLinearVelocity();
        if (vel.x < -MAX_VELOCITY) {
            return;
        }

        applyMovement(-SPEED, 0);
    }

    public void applyUpMovement() {
        Vector2 vel = body.getLinearVelocity();

        if (vel.y > MAX_VELOCITY) {
            return;
        }
        applyMovement(0, SPEED);
    }

    public void applyDownMovement() {
        Vector2 vel = body.getLinearVelocity();

        if (vel.y < -MAX_VELOCITY) {
            return;
        }

        applyMovement(0, -SPEED);
    }

    public void stopXMovement() {
        body.setLinearVelocity(0, body.getLinearVelocity().y);
    }


    public void stopYMovement() {
        body.setLinearVelocity(body.getLinearVelocity().x, 0);
    }

    private void applyMovement(float impulseX, float impulseY) {
        Vector2 pos = body.getPosition();

        body.applyLinearImpulse(impulseX, impulseY, pos.x, pos.y, true);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void dispose() {
        texture.dispose();
        circle.dispose();
    }

}
