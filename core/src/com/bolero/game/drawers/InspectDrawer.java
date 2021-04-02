package com.bolero.game.drawers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.icons.InteractButtonImage;
import com.bolero.game.managers.BundleManager;
import lombok.val;

public class InspectDrawer extends AbstractDrawer implements Disposable, InteractButtonImage {
  private final Texture buttonTexture;
  private final Label contentLabel; // Label for the main content text

  public InspectDrawer(BundleManager bundleManager) throws ConfigurationNotLoadedException {
    super();
    val file = getInteractButtonImage();
    buttonTexture = new Texture(file);
    val buttonImage = new Image(buttonTexture);

    contentLabel = new Label("", uiSkin);
    val continueLabel = new Label(bundleManager.getString("continue"), uiSkin);

    contentLabel.setWrap(true);

    table.bottom();
    table.padBottom(20);
    table.padRight(20);
    table.padLeft(20);
    table.add(contentLabel).expandX().left();
    table.row();
    table.add(buttonImage).right();
    table.add(continueLabel).right();
  }

  public void draw(String text) {
    contentLabel.setText(text);
    super.draw();
  }

  @Override
  public void dispose() {
    super.dispose();
    buttonTexture.dispose();
  }
}
