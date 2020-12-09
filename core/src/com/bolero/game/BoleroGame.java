package com.bolero.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.screens.GameScreen;

public class BoleroGame extends Game {
    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public BitmapFont font;
    private GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        font = new BitmapFont();
        gameScreen = new GameScreen(this);

        this.setScreen(gameScreen);
    }

    @Override
    public void render() {
        super.render();
    }


    @Override
    public void dispose() {
        batch.dispose();
        hudBatch.dispose();
        font.dispose();
        gameScreen.dispose();
    }
}
