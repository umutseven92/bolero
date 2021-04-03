package com.bolero.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.InvalidConfigurationException;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.screens.GameScreen;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.SneakyThrows;
import lombok.val;

// TODO: Refactor the public fields
public class BoleroGame extends Game {

  private ArrayList<GameScreen> screenPool;
  private HashMap<String, String> screens;
  private BundleManager bundleManager;

  public static final float UNIT = 16f;
  public static final String SPAWN_INITIAL_OBJ = "initial";

  public static Config config;

  public SpriteBatch batch;
  public BitmapFont font;

  public Boolean debugMode = false;

  public GameScreen currentScreen;

  public Clock clock;

  public BundleManager getBundleController() {
    return bundleManager;
  }

  @SneakyThrows(ConfigurationNotLoadedException.class)
  @Override
  public void create() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    bundleManager = new BundleManager();

    batch = new SpriteBatch();
    font = new BitmapFont();

    screens = new HashMap<>();
    screenPool = new ArrayList<>();

    config = new Config();

    try {
      config.load();
    } catch (FileNotFoundException | InvalidConfigurationException e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
    }

    clock = new Clock(bundleManager, config.getConfig().getClock());
    loadMaps(config.getConfig().getMaps().getPath());
    loadRoute(config.getConfig().getMaps().getInitial(), SPAWN_INITIAL_OBJ);
  }

  public void loadMaps(String mapPath) {
    // Load all map files (*.tmx) from assets/map
    val files = Gdx.files.internal(mapPath);

    for (val file : files.list(".tmx")) {
      screens.put(file.nameWithoutExtension(), file.path());
    }
  }

  // TODO: Remember NPC positions during transition
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

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    batch.dispose();
    font.dispose();

    for (GameScreen screen : screenPool) {
      screen.dispose();
    }
  }
}
