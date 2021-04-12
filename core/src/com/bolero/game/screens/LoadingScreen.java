package com.bolero.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.bolero.game.BoleroGame;
import com.bolero.game.drawers.LoadingDrawer;
import lombok.val;

// Loads the next screen while displaying a `Loading..` label.
// Once loading is done, sets the next screen & disposes itself.
public class LoadingScreen implements Screen {

  private final BoleroGame game;
  private final LoadingDrawer loadingDrawer;
  private final String screenName;
  private final String path;
  private final String spawnName;

  public LoadingScreen(BoleroGame game, String screenName, String path, String spawnName) {
    this.game = game;
    this.screenName = screenName;
    this.path = path;
    this.spawnName = spawnName;
    this.loadingDrawer = new LoadingDrawer(game.getBundleManager());
  }

  @Override
  public void show() {
    // https://github.com/libgdx/libgdx/wiki/Threading
    new Thread(
            () -> {
              Gdx.app.postRunnable(
                  () -> {
                    try {
                      val screen = new GameScreen(game, screenName, path, spawnName);
                      game.setScreen(screen);
                      dispose();
                    } catch (Exception e) {
                      Gdx.app.error(LoadingScreen.class.getName(), e.toString(), e);
                      e.printStackTrace();
                      System.exit(1);
                    }
                  });
            })
        .start();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    loadingDrawer.draw();
  }

  @Override
  public void resize(int width, int height) {
    loadingDrawer.init();
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    loadingDrawer.dispose();
  }
}
