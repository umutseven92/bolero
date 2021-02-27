package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.icons.InteractButtonImage;
import lombok.val;

public class InspectDrawer extends AbstractDrawer implements Disposable, InteractButtonImage {
  private final Table table;
  private final Texture buttonTexture;
  private final Label textLabel;

  public InspectDrawer() throws ConfigurationNotLoadedException {
    super();
    val file = getInteractButtonImage();
    buttonTexture = new Texture(file);
    val buttonImage = new Image(buttonTexture);

    table = new Table();
    table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    table.bottom();
    table.padBottom(Gdx.graphics.getHeight() / 10f);

    textLabel = new Label("", uiSkin);
    val label2 = new Label("to continue", uiSkin);

    textLabel.setWrap(true);
    table.add(textLabel).width(Gdx.graphics.getWidth() / 1.2f);
    table.row();
    table.add(buttonImage).right();
    table.add(label2).right();
  }

  public void draw(SpriteBatch batch, String text) {
    textLabel.setText(text);
    table.draw(batch, 1f);
  }

  @Override
  public void dispose() {
    super.dispose();
    buttonTexture.dispose();
  }
}
