package com.bolero.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

public class BundleManager {

  private final I18NBundle bundle;

  public BundleManager() {
    I18NBundle.setExceptionOnMissingKey(true);
    bundle = I18NBundle.createBundle(Gdx.files.internal("locale/strings"));
  }

  public String getString(String key) {
    return this.bundle.get(key);
  }
}
