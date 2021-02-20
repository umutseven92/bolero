package com.bolero.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bolero.game.BoleroGame;

public class DesktopLauncher {
  public static void main(String[] arg) {

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "Bolero";
    config.foregroundFPS = 60;
    config.width = 800;
    config.height = 640;
    config.forceExit = true;
    new LwjglApplication(new BoleroGame(), config);
  }
}
