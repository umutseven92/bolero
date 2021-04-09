package com.bolero.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.dtos.ConfigDTO;
import com.bolero.game.exceptions.InvalidConfigurationException;
import com.bolero.game.loaders.ConfigLoader;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.screens.GameScreen;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class BoleroGame extends Game {
  public static final float UNIT = 16f;
  public static final float TILE_SIZE = 32f;
  public static final String SPAWN_INITIAL_OBJ = "initial";

  private ArrayList<GameScreen> screenPool;
  private HashMap<String, String> screens;
  @Getter private BundleManager bundleManager;

  @Getter private static ConfigDTO config;

  @Getter private SpriteBatch batch;

  @Getter private GameScreen currentScreen;

  /* Thing that should be persistent across all screens;
   * 1) Clock
   * 2) NPC controllers (NPC's should continue their schedules off-screen)
   * 3) Player
   * 4) Debug mode
   */
  @Getter private Clock clock;
  @Getter @Setter private Boolean debugMode = false;

  @Override
  public void create() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    bundleManager = new BundleManager();

    batch = new SpriteBatch();

    screens = new HashMap<>();
    screenPool = new ArrayList<>();

    try {
      config = loadConfig();
    } catch (FileNotFoundException | InvalidConfigurationException e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
    }

    clock = new Clock(bundleManager, config.getClock());
    loadMaps(config.getMaps().getPath());
    loadRoute(config.getMaps().getInitial(), SPAWN_INITIAL_OBJ);
  }

  private ConfigDTO loadConfig() throws FileNotFoundException, InvalidConfigurationException {
    val loader = new ConfigLoader();
    val file = Gdx.files.internal("./config/game.yaml");

    return loader.load(file);
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
      Gdx.app.log(GameScreen.class.getName(), String.format("Loading route %s", screenName));

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

    for (GameScreen screen : screenPool) {
      screen.dispose();
    }
  }
}
