package com.bolero.game.icons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.characters.Player;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import lombok.val;

public class InteractIcon implements InteractButtonImage, Disposable {
  private final Texture texture;
  private final Sprite sprite;
  private final Player player;

  public InteractIcon(Player player) throws ConfigurationNotLoadedException {
    this.player = player;
    val keyfile = getInteractButtonImage();
    this.texture = new Texture(keyfile);
    this.sprite = new Sprite(texture);
    sprite.setSize(1.2f, 1);
  }

  public void draw(SpriteBatch batch) {
    sprite.setPosition(player.getPosition().x + 0.5f, player.getPosition().y + 1);
    sprite.draw(batch);
  }

  @Override
  public void dispose() {
    texture.dispose();
  }
}
