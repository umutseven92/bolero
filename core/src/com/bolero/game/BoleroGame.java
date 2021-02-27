package com.bolero.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.screens.GameScreen;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.val;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

// TODO: Refactor the public fields
public class BoleroGame extends Game {

  private ArrayList<GameScreen> screenPool;
  private HashMap<String, String> screens;
  private BundleManager bundleManager;

  public static final float UNIT = 16f;
  public static final String COL_LAYER = "Collision";
  public static final String SPAWN_LAYER = "Spawn";
  public static final String SCHEDULE_LAYER = "Schedule";
  public static final String INT_LAYER = "Interaction";
  public static final String LIGHT_LAYER = "Lights";
  public static final String PATH_LAYER = "Path";
  public static final String SPAWN_INITIAL_OBJ = "initial";

  public Config config;
  public SpriteBatch batch;
  public SpriteBatch hudBatch;
  public BitmapFont font;

  public Boolean debugMode = false;

  public GameScreen currentScreen;

  public Clock clock;

  public BundleManager getBundleController() {
    return bundleManager;
  }

  @Override
  public void create() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    bundleManager = new BundleManager();

    batch = new SpriteBatch();
    hudBatch = new SpriteBatch();
    font = new BitmapFont();

    screens = new HashMap<>();
    screenPool = new ArrayList<>();

    loadConfig();

    clock = new Clock(bundleManager, config.getClock().getSpeed());
    loadMaps();
    loadRoute(this.config.getInitialMap(), SPAWN_INITIAL_OBJ);
  }

  public void loadMaps() {
    // Load all map files (*.tmx) from assets/map
    val files = Gdx.files.internal(this.config.getMapsPath());

    for (val file : files.list(".tmx")) {
      screens.put(file.nameWithoutExtension(), file.path());
    }
  }

  public void loadRoute(String screenName, String spawnName) {
    GameScreen toLoad = null;

    try {
      Gdx.app.log(GameScreen.class.getName(), String.format("Loading map %s", screenName));

      if (!screens.containsKey(screenName)) {
        throw new Exception(String.format("Screen %s does not exist in screens.", screenName));
      }

      val path = screens.get(screenName);

      toLoad = new GameScreen(this, screenName, path, spawnName);
    } catch (Exception e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
    }

    screenPool.add(toLoad);
    this.setScreen(toLoad);

    currentScreen = toLoad;
  }

  private void loadConfig() {
    val file = Gdx.files.internal("./config/game.yaml");

    val yaml = new Yaml(new Constructor(Config.class));

    this.config = yaml.load(file.readString());
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
