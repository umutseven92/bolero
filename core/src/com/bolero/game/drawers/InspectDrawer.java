package com.bolero.game.drawers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.icons.InteractButtonImage;
import com.bolero.game.interactions.InspectRectangle;
import com.bolero.game.managers.BundleManager;
import lombok.val;

public class InspectDrawer extends AbstractDrawer implements Disposable, InteractButtonImage {
  private final Texture buttonTexture;
  private final Label contentLabel; // Label for the main content text
  private final Label continueLabel;

  private final Image buttonImage;
  private final BundleManager bundleManager;

  public InspectDrawer(BundleManager bundleManager) throws ConfigurationNotLoadedException {
    super();
    this.bundleManager = bundleManager;
    val file = getInteractButtonImage();
    buttonTexture = new Texture(file);
    buttonImage = new Image(buttonTexture);

    contentLabel = new Label("", uiSkin);
    continueLabel = new Label(bundleManager.getString("continue"), uiSkin);

    contentLabel.setWrap(true);
  }

  public void init(int width) {
    super.initTable();
    table.bottom();
    table.padBottom(20);
    table.padRight(20);
    table.padLeft(20);
    table.add(contentLabel).width(width - 100).left();
    table.row();
    table.add(buttonImage).right();
    table.add(continueLabel).right();
  }

  public void activate(InspectRectangle inspectRectangle) {
    super.activate();
    val text = bundleManager.getString(inspectRectangle.getStringID());
    contentLabel.setText(text);
  }

  @Override
  public void dispose() {
    super.dispose();
    buttonTexture.dispose();
  }
}
