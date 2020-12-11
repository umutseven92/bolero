package com.bolero.game.icons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.Player;

public class ButtonIcon implements Disposable {
    private final Texture texture;
    private final Sprite sprite;
    private final Player player;

    public ButtonIcon(Player player) {
        this.player = player;
        this.texture = new Texture(Gdx.files.internal("buttons/green-E.png"));
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
