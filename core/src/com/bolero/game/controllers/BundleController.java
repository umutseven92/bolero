package com.bolero.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

public class BundleController {

    private final I18NBundle bundle;

    public BundleController() {
        bundle = I18NBundle.createBundle(Gdx.files.internal("locale/strings"));
    }

    public I18NBundle getBundle() {
        return bundle;
    }
}
