package com.bolero.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.exceptions.MapperException;
import com.bolero.game.screens.BoleroScreen;
import com.bolero.game.screens.GameScreen;
import com.bolero.game.screens.HouseScreen;

import java.io.Console;
import java.util.HashMap;

public class BoleroGame extends Game {
    private HashMap<String, GameScreen> screens;

    public final String COL_LAYER = "Collision";
    public final String SPAWN_LAYER = "Spawn";
    public final String INT_LAYER = "Interaction";
    public final String LIGHT_LAYER = "Lights";
    public final String SPAWN_INITIAL_OBJ = "initial";

    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public BitmapFont font;

    public Boolean debugMode = false;

    public String currentScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        font = new BitmapFont();

        BoleroScreen boleroScreen = null;
        HouseScreen houseScreen = null;
        try {
            boleroScreen = new BoleroScreen(this);
            houseScreen = new HouseScreen(this);
        } catch (MapperException e) {
            e.printStackTrace();
            System.exit(1);
        }

        screens = new HashMap<>();

        screens.put("bolero", boleroScreen);
        screens.put("house1", houseScreen);

        currentScreen = "bolero";
        this.setScreen(boleroScreen);
    }

    public void loadRoute(String screenName, String spawnName) {
        GameScreen toLoad = screens.get(screenName);

        toLoad.setPlayerSpawnPoint(spawnName);
        currentScreen = screenName;
        this.setScreen(toLoad);
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

        for (GameScreen screen : screens.values()) {
            screen.dispose();
        }
    }
}
