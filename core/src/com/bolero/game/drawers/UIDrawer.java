package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public abstract class UIDrawer implements Disposable {
  protected final Skin uiSkin;

  public UIDrawer() {
    uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
  }

  @Override
  public void dispose() {
    uiSkin.dispose();
  }
}
