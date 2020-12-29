package com.bolero.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.screens.BoleroScreen;
import com.bolero.game.screens.GameScreen;
import com.bolero.game.screens.HouseScreen;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class BoleroGame extends Game {
    private ArrayList<GameScreen> screenPool;
    private HashMap<String, Class> screens;
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

    public GameScreen currentScreen;

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

        screens = new HashMap<>();
        screenPool = new ArrayList<>();

        screens.put("bolero", BoleroScreen.class);
        screens.put("house1", HouseScreen.class);

        loadRoute("bolero", "initial");
    }


    public void loadRoute(String screenName, String spawnName) {
        GameScreen toLoad = null;

        try {
            Class<GameScreen> clazz = screens.get(screenName);

            Constructor<GameScreen> c = clazz.getDeclaredConstructor(BoleroGame.class, String.class);
            toLoad = c.newInstance(this, spawnName);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        screenPool.add(toLoad);
        this.setScreen(toLoad);

        currentScreen = toLoad;
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

        for (GameScreen screen : screenPool) {
            screen.dispose();
        }
    }
}
