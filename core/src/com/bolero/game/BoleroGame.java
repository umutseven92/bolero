package com.bolero.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.screens.BoleroScreen;
import com.bolero.game.screens.HouseScreen;

public class BoleroGame extends Game {
    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public BitmapFont font;
    private BoleroScreen gameScreen;
    public HouseScreen houseScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        font = new BitmapFont();
        gameScreen = new BoleroScreen(this);
        houseScreen = new HouseScreen(this);

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
        houseScreen.dispose();
    }
}
