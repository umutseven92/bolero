package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

public class DialogDrawer implements Disposable {
    private final Table table;
    private final Skin uiSkin;
    private final Texture buttonTexture;
    private final Image buttonImage;

    public DialogDrawer() {
        uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        buttonTexture = new Texture(Gdx.files.internal("buttons/green-E.png"));
        buttonImage = new Image(buttonTexture);

        table = new Table();
        table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.bottom();
        table.padBottom(Gdx.graphics.getHeight() / 10f);

        Label label = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", uiSkin);
        Label label2 = new Label("to continue", uiSkin);

        label.setWrap(true);
        table.add(label).width(Gdx.graphics.getWidth() / 1.2f);
        table.row();
        table.add(buttonImage).right();
        table.add(label2).right();
    }

    public void draw(SpriteBatch batch) {
        table.draw(batch, 1f);
    }

    @Override
    public void dispose() {
        buttonTexture.dispose();
        uiSkin.dispose();
    }
}
