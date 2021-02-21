package com.bolero.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.PathNode;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.dtos.MovementDTO;
import com.bolero.game.dtos.SizeDTO;
import com.bolero.game.enums.CharacterState;
import com.bolero.game.enums.Direction;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.val;

public abstract class AbstractCharacter implements Disposable {
  private static final float DIALOG_SPRITE_SIZE_MULTIPLIER = 10f;

  private CharacterState state;
  private Direction direction;

  private final SpriteSheetValues ssValues;

  private Animation<TextureRegion> walkAnimation;
  private Animation<TextureRegion> idleAnimation;

  private final Texture spriteSheet;
  private final SizeDTO size;
  private final MovementDTO movement;

  private final Sprite sprite;
  private final Sprite dialogSprite;

  protected final Body body;
  private float animationTime;

  private CircleShape circle;

  private Array<PathNode> goals;
  private int goalIndex;
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

  public AbstractCharacter(
      Vector2 position,
      World box2DWorld,
      SizeDTO sizeDTO,
      MovementDTO movementDTO,
      String texturePath,
      SpriteSheetValues ssValues,
      BodyDef.BodyType bodyType)
      throws FileNotFoundException {
    this.size = sizeDTO;
    this.movement = movementDTO;
    this.position = position;
    this.direction = Direction.right;
    this.state = CharacterState.idle;
    val file = Gdx.files.internal(texturePath);

    if (!file.exists()) {
      throw new FileNotFoundException(String.format("%s does not exist.", texturePath));
    }

    this.spriteSheet = new Texture(file);
    this.ssValues = ssValues;

    loadAnimationsFromSpiteSheet();

    this.sprite = new Sprite();
    this.dialogSprite = new Sprite();
    sprite.setSize(size.getWidth(), sizeDTO.getHeight());
    dialogSprite.setSize(
        size.getWidth() * DIALOG_SPRITE_SIZE_MULTIPLIER * BoleroGame.UNIT,
        size.getHeight() * DIALOG_SPRITE_SIZE_MULTIPLIER * BoleroGame.UNIT);
    body = createBody(box2DWorld, bodyType);
    goals = new Array<>();
    goalIndex = 0;
  }

  private void loadAnimationsFromSpiteSheet() {
    TextureRegion[][] textureMatrix =
        TextureRegion.split(
            spriteSheet,
            spriteSheet.getWidth() / ssValues.getCols(),
            spriteSheet.getHeight() / ssValues.getRows());
    TextureRegion[] walkFrames = new TextureRegion[ssValues.getCols()];
    TextureRegion[] idleFrames = new TextureRegion[ssValues.getCols()];

    int index = 0;
    for (int j = 0; j < ssValues.getCols(); j++) {
      idleFrames[index] = textureMatrix[ssValues.getIdleRow()][j];
      walkFrames[index] = textureMatrix[ssValues.getWalkRow()][j];
      index++;
    }

    idleAnimation = new Animation<>(0.3f, idleFrames);
    walkAnimation = new Animation<>(0.1f, walkFrames);

    animationTime = 0f;
  }

  public void startTalking() {
    stopMovement();
    this.state = CharacterState.talking;
  }

  public void stopTalking() {
    this.state = CharacterState.idle;
  }

  public void startInspecting() {
    stopMovement();
    this.state = CharacterState.inspecting;
  }

  public void stopInspecting() {
    this.state = CharacterState.idle;
  }

  public void setPosition() {
    this.position = this.body.getPosition();
    this.sprite.setPosition(
        this.position.x - this.size.getWidth() / 2, this.position.y - this.size.getHeight() / 2);

    handleSchedule();
  }

  private void handleSchedule() {
    if (goals.isEmpty()) {
      return;
    }

    val goal = goals.get(goalIndex);

    if (goal != null && this.state != CharacterState.talking) {
      boolean xReached = false;
      boolean yReached = false;

      if (MathUtils.isEqual(this.position.y, goal.getX(), 0.5f)) {
        stopYMovement();
        yReached = true;
      }

      if (MathUtils.isEqual(this.position.x, goal.getX(), 0.5f)) {
        stopXMovement();
        xReached = true;
      }

      if (yReached && xReached) {
        goalIndex++;
        if (goalIndex >= goals.size) {
          goals.clear();
        }
      } else {
        float impulseX = goal.getX() - this.position.x;
        float impulseY = goal.getY() - this.position.y;

        if (impulseX > 0) {
          direction = Direction.right;
        } else {
          direction = Direction.left;
        }

        Vector2 vel = body.getLinearVelocity();

        if (Math.abs(vel.x) >= movement.getMaxVelocity()) {
          impulseX = 0;
        } else {
          impulseX = MathUtils.clamp(impulseX, -movement.getSpeed(), movement.getSpeed());
        }

        if (Math.abs(vel.y) >= movement.getMaxVelocity()) {
          impulseY = 0;
        } else {
          impulseY = MathUtils.clamp(impulseY, -movement.getSpeed(), movement.getSpeed());
        }

        this.applyMovement(impulseX, impulseY);
      }
    }
  }

  private Body createBody(World world, BodyDef.BodyType bodyType) {
    val bodyDef = new BodyDef();
    bodyDef.type = bodyType;
    bodyDef.position.set(position.x + size.getWidth() / 2, position.y + size.getHeight() / 2);
    val body = world.createBody(bodyDef);

    circle = new CircleShape();
    circle.setRadius(1);

    val fixtureDef = new FixtureDef();
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

    val vel = body.getLinearVelocity();

    if (vel.x > movement.getMaxVelocity()) {
      return;
    }

    applyMovement(movement.getSpeed(), 0);
  }

  public void applyLeftMovement() {
    this.direction = Direction.left;

    val vel = body.getLinearVelocity();
    if (vel.x < -movement.getMaxVelocity()) {
      return;
    }

    applyMovement(-movement.getSpeed(), 0);
  }

  public void applyUpMovement() {
    val vel = body.getLinearVelocity();

    if (vel.y > movement.getMaxVelocity()) {
      return;
    }
    applyMovement(0, movement.getSpeed());
  }

  public void applyDownMovement() {
    val vel = body.getLinearVelocity();

    if (vel.y < -movement.getMaxVelocity()) {
      return;
    }

    applyMovement(0, -movement.getSpeed());
  }

  public void stopMovement() {
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

  public void setGoals(Array<PathNode> nodes) {
    this.goals = nodes;
    this.goalIndex = 0;
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
