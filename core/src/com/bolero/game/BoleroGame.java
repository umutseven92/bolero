package com.bolero.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bolero.game.dtos.ConfigDTO;
import com.bolero.game.exceptions.InvalidConfigurationException;
import com.bolero.game.loaders.ConfigLoader;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.mixins.FileLoader;
import com.bolero.game.screens.GameScreen;
import com.bolero.game.screens.LoadingScreen;
import java.io.FileNotFoundException;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class BoleroGame extends Game implements FileLoader {
  public static final float UNIT = 16f;
  public static final float TILE_SIZE = 32f;
  public static final String SPAWN_INITIAL_OBJ = "initial";

  private HashMap<String, String> screens;

  @Getter private SpriteBatch batch;

  /* Thing that should be persistent across all screens;
   * 1) Clock
   * 2) NPC controllers (NPC's should continue their schedules off-screen)
   * 3) Player
   * 4) Debug mode
   * 5) Pause menu
   * 6) Bundle manager
   * 7) Config
   * 8) Audio effects that need to persist between stages
   */
  @Getter private Clock clock;
  @Getter @Setter private Boolean debugMode = false;
  @Getter private BundleManager bundleManager;
  @Getter private Sound transitionSound;

  @Getter private static ConfigDTO config;

  @Override
  public void create() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    bundleManager = new BundleManager();

    batch = new SpriteBatch();

    screens = new HashMap<>();

    val soundPath = "sound_effects/door_open.ogg";
    FileHandle soundFile;

    try {
      soundFile = getFile(soundPath);

      config = loadConfig();
    } catch (FileNotFoundException | InvalidConfigurationException e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
      return;
    }

    clock = new Clock(bundleManager, config.getClock());
    loadMaps(config.getMaps().getPath());
    loadRouteLoading(config.getMaps().getInitial(), SPAWN_INITIAL_OBJ);

    // This is static for now- same sound effect for every transition.
    transitionSound = Gdx.audio.newSound(soundFile);
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

  public void playTransitionSound() {
    transitionSound.play();
  }

  // Load the screen with a loading screen before it.
  public void loadRouteLoading(String screenName, String spawnName) {

    try {
      if (!screens.containsKey(screenName)) {
        throw new Exception(String.format("Screen %s does not exist in screens.", screenName));
      }
    } catch (Exception e) {
      Gdx.app.error(GameScreen.class.getName(), e.toString(), e);
      e.printStackTrace();
      System.exit(1);
      return;
    }

    val path = screens.get(screenName);

    val loadingScreen = new LoadingScreen(this, screenName, path, spawnName);
    val currentScreen = this.getScreen();
    this.setScreen(loadingScreen);

    if (currentScreen != null) {
      currentScreen.dispose();
    }
  }

  @Override
  public void dispose() {
    transitionSound.dispose();
    batch.dispose();
  }
}
