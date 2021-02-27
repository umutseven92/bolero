package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.dtos.KeysDTO;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;

public abstract class AbstractDrawer implements Disposable {
  protected final Skin uiSkin;
  protected final KeysDTO keys;

  public AbstractDrawer() throws ConfigurationNotLoadedException {
    keys = BoleroGame.config.getConfig().getKeys();
    uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
  }

  @Override
  public void dispose() {
    uiSkin.dispose();
  }
}
