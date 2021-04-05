package com.bolero.game.sprite_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import java.io.FileNotFoundException;
import lombok.val;

public abstract class AbstractSpriteElement implements Disposable {

  private final Texture spriteSheet;
  private final Sprite sprite;
  private final float frameDuration;

  private float animationTime;
  private Animation<TextureRegion> idleAnimation;

  public AbstractSpriteElement(
      String assetPath,
      Vector2 position,
      Vector2 size,
      float frameDuration,
      SpriteElementValues ssValues)
      throws FileNotFoundException {
    val file = Gdx.files.internal(assetPath);

    if (!file.exists()) {
      throw new FileNotFoundException(String.format("%s does not exist.", assetPath));
    }

    this.spriteSheet = new Texture(file);
    this.frameDuration = frameDuration;
    this.animationTime = 0f;
    loadAnimationsFromSpiteSheet(ssValues);

    this.sprite = new Sprite();
    this.sprite.setPosition(
        (position.x / BoleroGame.UNIT) - (size.x / 2),
        (position.y / BoleroGame.UNIT) - (size.y / 2));
    sprite.setSize(size.x, size.y);
  }

  // Divide up the sprite sheet into animation strips.
  private void loadAnimationsFromSpiteSheet(SpriteElementValues ssValues) {
    TextureRegion[][] textureMatrix =
        TextureRegion.split(
            spriteSheet,
            spriteSheet.getWidth() / ssValues.getCols(),
            spriteSheet.getHeight() / ssValues.getRows());
    TextureRegion[] idleFrames =
        new TextureRegion
            [(ssValues.getColAmount() - ssValues.getColStartIndex())
                * (ssValues.getRowAmount() - ssValues.getRowStartIndex())];

    int index = 0;
    for (int j = ssValues.getRowStartIndex();
        j < ssValues.getRowStartIndex() + ssValues.getRowAmount();
        j++) {
      for (int i = ssValues.getColStartIndex();
          i < ssValues.getColStartIndex() + ssValues.getColAmount();
          i++) {
        idleFrames[index] = textureMatrix[j][i];
        index++;
      }
    }

    idleAnimation = new Animation<>(frameDuration, idleFrames);
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
