package com.bolero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private final float width;
    private final float height;
    private final float speed;

    private Vector2 position;

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        this.sprite.setPosition(position.x, position.y);
        this.collisionRectangle.setPosition(position);
    }

    public void setPositionX(float x) {
        this.position.x = x;
    }

    public void setPositionY(float y) {
        this.position.y = y;
    }

    private final Sprite sprite;

    public Rectangle collisionRectangle;

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }


    public Player(float width, float height, Vector2 position, float speed, Texture texture) {
        this.width = width;
        this.height = height;
        this.position = position;
        this.speed = speed;

        this.sprite = new Sprite(texture);
        sprite.setSize(width, height);
        collisionRectangle = new Rectangle(position.x, position.y, width, height);
        setSpritePos();
    }

    public void moveLeft(float deltaTime) {
        if (!sprite.isFlipX()) {
            sprite.flip(true, false);
        }

        this.position.x -= deltaTime * this.speed;
        setPositions();
    }

    public void moveRight(float deltaTime) {
        if (sprite.isFlipX()) {
            sprite.flip(true, false);
        }

        this.position.x += deltaTime * this.speed;
        setPositions();
    }

    public void moveUp(float deltaTime) {
        this.position.y += deltaTime * this.speed;
        setPositions();
    }

    public void moveDown(float deltaTime) {
        this.position.y -= deltaTime * this.speed;
        setPositions();
    }

    private void setPositions() {
        setSpritePos();
        setCollisionRectanglePos();
    }

    private void setSpritePos() {
        sprite.setPosition(this.position.x, this.position.y);
    }

    private void setCollisionRectanglePos() {
        collisionRectangle.setPosition(this.position);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
