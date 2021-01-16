package com.bolero.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.enums.CharacterState;

import java.io.FileNotFoundException;

public abstract class Character implements Disposable {
  private static final float DIALOG_SPRITE_SIZE_MULTIPLIER = 10f;
  private CharacterState state = CharacterState.idle;
  private Direction direction = Direction.right;

  private final SpriteSheetValues ssValues;

  private Animation<TextureRegion> walkAnimation;
  private Animation<TextureRegion> idleAnimation;

  private final Texture spriteSheet;
  private final CharacterValues characterValues;

  private final Sprite sprite;
  private final Sprite dialogSprite;

  protected final Body body;
  private float animationTime;

  private CircleShape circle;

  private Vector2 goal;
  private Vector2 position;

  public Vector2 getPosition() {
    return position;
  }

  public CharacterState getState() {
    return state;
  }

  public Sprite getDialogSprite() {
    return dialogSprite;
  }

  public void setState(CharacterState state) {
    this.stop();
    this.state = state;
  }

  public Character(
      Vector2 position,
      World box2DWorld,
      CharacterValues characterValues,
      String texturePath,
      SpriteSheetValues ssValues,
      BodyDef.BodyType bodyType)
      throws FileNotFoundException {
    this.characterValues = characterValues;
    this.position = position;

    FileHandle file = Gdx.files.internal(texturePath);

    if (!file.exists()) {
      throw new FileNotFoundException(String.format("%s does not exist.", texturePath));
    }

    this.spriteSheet = new Texture(file);
    this.ssValues = ssValues;

    loadAnimationsFromSpiteSheet();

    this.sprite = new Sprite();
    this.dialogSprite = new Sprite();
    sprite.setSize(characterValues.width, characterValues.height);
    dialogSprite.setSize(
        characterValues.width * DIALOG_SPRITE_SIZE_MULTIPLIER * BoleroGame.UNIT,
        characterValues.height * DIALOG_SPRITE_SIZE_MULTIPLIER * BoleroGame.UNIT);
    body = createBody(box2DWorld, bodyType);
  }

  private void loadAnimationsFromSpiteSheet() {
    TextureRegion[][] textureMatrix =
        TextureRegion.split(
            spriteSheet,
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
    this.sprite.setPosition(
        this.position.x - this.characterValues.width / 2,
        this.position.y - this.characterValues.height / 2);

    if (goal != null && this.state != CharacterState.talking) {
      this.state = CharacterState.walking;
      boolean xReached = false;
      boolean yReached = false;

      if (MathUtils.isEqual(this.position.y, goal.y, 0.5f)) {
        stopYMovement();
        yReached = true;
      }

      if (MathUtils.isEqual(this.position.x, goal.x, 0.5f)) {
        stopXMovement();
        xReached = true;
      }

      if (yReached && xReached) {
        goal = null;
      } else {
        float impulseX = goal.x - this.position.x;
        float impulseY = goal.y - this.position.y;

        if (impulseX > 0) {
          direction = Direction.right;
        } else {
          direction = Direction.left;
        }

        Vector2 vel = body.getLinearVelocity();

        if (Math.abs(vel.x) >= characterValues.maxVelocity) {
          impulseX = 0;
        } else {
          impulseX = MathUtils.clamp(impulseX, -characterValues.speed, characterValues.speed);
        }

        if (Math.abs(vel.y) >= characterValues.maxVelocity) {
          impulseY = 0;
        } else {
          impulseY = MathUtils.clamp(impulseY, -characterValues.speed, characterValues.speed);
        }

        body.applyLinearImpulse(impulseX, impulseY, this.position.x, this.position.y, true);
      }
    }
  }

  private Body createBody(World world, BodyDef.BodyType bodyType) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = bodyType;
    bodyDef.position.set(
        position.x + characterValues.width / 2, position.y + characterValues.height / 2);
    Body body = world.createBody(bodyDef);

    circle = new CircleShape();
    circle.setRadius(1);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circle;
    fixtureDef.density = 0.5f;
    fixtureDef.friction = 0.81f;
    fixtureDef.restitution = 0.3f;

    // We don't want characters to collide with each other, so we set their group index to -1.
    // https://stackoverflow.com/a/937557/3894455
    fixtureDef.filter.groupIndex = -1;
    body.createFixture(fixtureDef);

    return body;
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

  public void setGoal(Vector2 goal) {
    this.goal = goal;
    this.state = CharacterState.walking;
  }

  public void draw(SpriteBatch batch) {
    animationTime += Gdx.graphics.getDeltaTime();

    TextureRegion currentFrame;
    TextureRegion dialogFrame;
    if (this.state == CharacterState.walking) {
      currentFrame = walkAnimation.getKeyFrame(animationTime, true);
    } else {
      currentFrame = idleAnimation.getKeyFrame(animationTime, true);
    }
    // Dialog sprite is always idle.
    dialogFrame = idleAnimation.getKeyFrame(animationTime, true);

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
    dialogSprite.setRegion(dialogFrame);
    sprite.draw(batch);
  }

  @Override
  public void dispose() {
    spriteSheet.dispose();
    circle.dispose();
  }
}
