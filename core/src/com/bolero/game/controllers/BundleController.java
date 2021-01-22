package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

public class BundleController {

  private final I18NBundle bundle;

  public BundleController() {
    I18NBundle.setExceptionOnMissingKey(true);
    bundle = I18NBundle.createBundle(Gdx.files.internal("locale/strings"));
  }

  public String getString(String key) {
    return this.bundle.get(key);
  }
}
