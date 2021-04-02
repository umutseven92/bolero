package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.dtos.KeysDTO;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;

public abstract class AbstractDrawer implements Disposable {
  protected final Skin uiSkin;
  protected final KeysDTO keys;
  protected final Stage stage;
  protected final Table table;

  public AbstractDrawer() throws ConfigurationNotLoadedException {
    this(false);
  }

  public AbstractDrawer(boolean debug) throws ConfigurationNotLoadedException {
    keys = BoleroGame.config.getConfig().getKeys();
    uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    stage = new Stage();

    table = new Table();
    table.setDebug(debug);
    table.setFillParent(true);
    stage.addActor(table);
  }

  protected void draw() {
    stage.draw();
  }

  @Override
  public void dispose() {
    uiSkin.dispose();
    stage.dispose();
  }
}
