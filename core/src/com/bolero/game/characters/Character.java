package com.bolero.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.enums.CharacterState;

abstract public class Character implements Disposable {
    private CharacterState state = CharacterState.idle;
    private Direction direction = Direction.right;

    private final SpriteSheetValues ssValues;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> idleAnimation;

    private final Texture spriteSheet;
    private final CharacterValues characterValues;

    private final Sprite sprite;
    protected final Body body;
    private float animationTime;

    private CircleShape circle;

    private Vector2 position;

    public Vector2 getPosition() {
        return position;
    }

    public CharacterState getState() {
        return state;
    }

    public void setState(CharacterState state) {
        this.stop();
        this.state = state;
    }

    public Character(Vector2 position, World box2DWorld, CharacterValues characterValues,
                     String texturePath, SpriteSheetValues ssValues, BodyDef.BodyType bodyType) {
        this.characterValues = characterValues;
        this.position = position;
        this.spriteSheet = new Texture(Gdx.files.internal(texturePath));
        this.ssValues = ssValues;

        loadAnimationsFromSpiteSheet();

        this.sprite = new Sprite();
        sprite.setSize(characterValues.width, characterValues.height);
        body = createPlayerBody(box2DWorld, bodyType);
    }

    private void loadAnimationsFromSpiteSheet() {
        TextureRegion[][] textureMatrix = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / ssValues.cols,
                spriteSheet.getHeight() / ssValues.rows);

        TextureRegion[] walkFrames = new TextureRegion[ssValues.cols];
        TextureRegion[] idleFrames = new TextureRegion[ssValues.cols];

        int index = 0;
        for (int j = 0; j < ssValues.cols; j++) {
            idleFrames[index] = textureMatrix[ssValues.idleRow][j];
            walkFrames[index] = textureMatrix[ssValues.walkRow][j];
            index++;
        }

        idleAnimation = new Animation<>(0.3f, idleFrames);
        walkAnimation = new Animation<>(0.1f, walkFrames);

        animationTime = 0f;
    }

    public void setPosition() {
        this.position = this.body.getPosition();
        this.sprite.setPosition(this.position.x - this.characterValues.width / 2, this.position.y - this.characterValues.height / 2);
    }

    private Body createPlayerBody(World world, BodyDef.BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(position.x + characterValues.width / 2, position.y + characterValues.height / 2);

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
        this.direction = Direction.right;

        Vector2 vel = body.getLinearVelocity();

        if (vel.x > characterValues.maxVelocity) {
            return;
        }

        applyMovement(characterValues.speed, 0);
    }

    public void applyLeftMovement() {
        this.direction = Direction.left;

        Vector2 vel = body.getLinearVelocity();
        if (vel.x < -characterValues.maxVelocity) {
            return;
        }

        applyMovement(-characterValues.speed, 0);
    }

    public void applyUpMovement() {
        Vector2 vel = body.getLinearVelocity();

        if (vel.y > characterValues.maxVelocity) {
            return;
        }
        applyMovement(0, characterValues.speed);
    }

    public void applyDownMovement() {
        Vector2 vel = body.getLinearVelocity();

        if (vel.y < -characterValues.maxVelocity) {
            return;
        }

        applyMovement(0, -characterValues.speed);
    }

    public void stop() {
        stopXMovement();
        stopYMovement();
    }

    private void checkForIdle() {
        if (body.getLinearVelocity().isZero()) {
            this.state = CharacterState.idle;
        }
    }

    public void stopXMovement() {
        body.setLinearVelocity(0, body.getLinearVelocity().y);
        checkForIdle();
    }

    public void stopYMovement() {
        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        checkForIdle();
    }

    private void applyMovement(float impulseX, float impulseY) {
        this.state = CharacterState.walking;
        Vector2 pos = body.getPosition();

        body.applyLinearImpulse(impulseX, impulseY, pos.x, pos.y, true);
    }

    public void draw(SpriteBatch batch) {
        animationTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame;
        if (this.state == CharacterState.walking) {
            currentFrame = walkAnimation.getKeyFrame(animationTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(animationTime, true);
        }

        if (this.direction == Direction.left) {
            if (!currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }
        } else {
            if (currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }
        }

        sprite.setRegion(currentFrame);
        sprite.draw(batch);
    }

    @Override
    public void dispose() {
        spriteSheet.dispose();
        circle.dispose();
    }

}
