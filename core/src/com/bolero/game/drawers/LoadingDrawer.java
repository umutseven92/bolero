package com.bolero.game.drawers;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.managers.BundleManager;

public class LoadingDrawer extends AbstractDrawer implements Disposable {

  private final Label loadingLabel;

  public LoadingDrawer(BundleManager bundleManager) {
    super();
    loadingLabel = new Label(bundleManager.getString("loading"), uiSkin);
  }

  public void init() {
    super.initTable();

    table.pad(20);
    table.bottom();
    table.add(loadingLabel).expandX().right();
  }
}
