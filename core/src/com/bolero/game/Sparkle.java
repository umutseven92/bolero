package com.bolero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import java.io.FileNotFoundException;
import lombok.val;

public class Sparkle implements Disposable {
  private final Texture spriteSheet;
  private final Sprite sprite;
  private float animationTime;
  private Animation<TextureRegion> idleAnimation;

  public Sparkle(Vector2 position, Vector2 size) throws FileNotFoundException {
    val file = Gdx.files.internal("images/sparkle.png");

    if (!file.exists()) {
      throw new FileNotFoundException("images/sparkle.png does not exist.");
    }

    this.spriteSheet = new Texture(file);

    loadAnimationsFromSpiteSheet();

    this.sprite = new Sprite();
    this.sprite.setPosition(
        (position.x / BoleroGame.UNIT) - (size.x / 2),
        (position.y / BoleroGame.UNIT) - (size.y / 2));
    sprite.setSize(size.x, size.y);
  }

  private void loadAnimationsFromSpiteSheet() {
    TextureRegion[][] textureMatrix =
        TextureRegion.split(spriteSheet, spriteSheet.getWidth() / 8, spriteSheet.getHeight() / 4);
    TextureRegion[] idleFrames = new TextureRegion[16];

    int index = 0;
    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < 4; i++) {
        idleFrames[index] = textureMatrix[j][i];
        index++;
      }
    }

    idleAnimation = new Animation<>(0.1f, idleFrames);

    animationTime = 0f;
  }

  public void draw(SpriteBatch batch) {
    animationTime += Gdx.graphics.getDeltaTime();

    TextureRegion currentFrame = idleAnimation.getKeyFrame(animationTime, true);

    sprite.setRegion(currentFrame);
    sprite.draw(batch);
  }

  @Override
  public void dispose() {
    spriteSheet.dispose();
  }
}
