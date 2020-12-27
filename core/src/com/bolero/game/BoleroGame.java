package com.bolero.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.exceptions.MapperException;
import com.bolero.game.screens.BoleroScreen;
import com.bolero.game.screens.GameScreen;
import com.bolero.game.screens.HouseScreen;

import java.util.HashMap;

public class BoleroGame extends Game {
    private HashMap<String, GameScreen> screens;
    private BundleController bundleController;

    public final static String COL_LAYER = "Collision";
    public final static String SPAWN_LAYER = "Spawn";
    public final static String INT_LAYER = "Interaction";
    public final static String LIGHT_LAYER = "Lights";
    public final static String SPAWN_INITIAL_OBJ = "initial";
    public final static int DAWN_START = 6;
   public final static int DAWN_END = 8;

    public final static int DUSK_START = 18;
    public final static int DUSK_END = 20;

    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public BitmapFont font;

    public Boolean debugMode = false;

    public String currentScreen;

    public Clock clock;

    public BundleController getBundleController() {
        return bundleController;
    }

    @Override
    public void create() {
        bundleController = new BundleController();

        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        font = new BitmapFont();

        clock = new Clock(bundleController);
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
