package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;

public abstract class AbstractDrawer implements Disposable {
  private final boolean debug;
  protected final Skin uiSkin;

  protected Stage stage;
  protected Table table;
  @Getter private boolean activated;

  public AbstractDrawer() {
    this(false);
  }

  public AbstractDrawer(boolean debug) {
    this.debug = debug;
    this.activated = false;
    uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
  }

  protected void activate() {
    this.activated = true;
  }

  protected void deactivate() {
    this.activated = false;
  }

  protected void initTable() {
    stage = new Stage();
    table = new Table();
    table.setDebug(debug);
    table.setFillParent(true);
    stage.addActor(table);
  }

  public void draw() {
    stage.draw();
  }

  @Override
  public void dispose() {
    uiSkin.dispose();
    stage.dispose();
  }
}
