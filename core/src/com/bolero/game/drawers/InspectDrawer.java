package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

public class InspectDrawer extends AbstractDrawer implements Disposable {
  private final Table table;
  private final Texture buttonTexture;
  private final Label textLabel;

  public InspectDrawer() {
    super();
    buttonTexture = new Texture(Gdx.files.internal("buttons/green-E.png"));
    Image buttonImage = new Image(buttonTexture);

    table = new Table();
    table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    table.bottom();
    table.padBottom(Gdx.graphics.getHeight() / 10f);

    textLabel = new Label("", uiSkin);
    Label label2 = new Label("to continue", uiSkin);

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
