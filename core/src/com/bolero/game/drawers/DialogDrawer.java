package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.dialog.Choice;
import com.bolero.game.dialog.Dialog;

public class DialogDrawer extends UIDrawer implements Disposable {
    private static final float SLIDE_SPEED = 0.3f;

    private final Table table;
    private final Texture buttonTexture;
    private final Label nameLabel;
    private final Label textLabel;
    private final VerticalGroup choiceGroup;
    private final Sprite playerSprite;

    private final float unit;
    private final float npcPosXGoal;
    private final float playerPosXGoal;

    private float npcPosX;
    private float playerPosX;

    private boolean activated;
    private NPC npc;
    private Sprite npcSprite;

    private Dialog currentDialog;

    private int activeIndex;

    public boolean isActivated() {
        return activated;
    }

    public DialogDrawer(Player player, float unit) {
        super();
        this.unit = unit;
        this.activated = false;
        this.playerSprite = player.getDialogSprite();

        this.playerPosXGoal = (Gdx.graphics.getWidth() / (unit)) - unit * 2.2f;
        this.npcPosXGoal = Gdx.graphics.getWidth() / (12 * unit);

        this.playerPosX = Gdx.graphics.getWidth() / (unit * 2.5f);
        this.playerSprite.setPosition(playerPosX, Gdx.graphics.getHeight() / (5 * unit));

        buttonTexture = new Texture(Gdx.files.internal("buttons/green-E.png"));
        Image buttonImage = new Image(buttonTexture);

        table = new Table();
        table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.bottom();
        table.padBottom(Gdx.graphics.getHeight() / 10f);

        nameLabel = new Label("", uiSkin);
        textLabel = new Label("", uiSkin);
        Label buttonLabel = new Label("to choose", uiSkin);

        textLabel.setWrap(true);
        table.add(nameLabel).width(Gdx.graphics.getWidth() / 1.2f);
        table.row();
        table.add(textLabel).width(Gdx.graphics.getWidth() / 1.2f);
        table.row();

        choiceGroup = new VerticalGroup();
        choiceGroup.left();

        table.add(choiceGroup).width(Gdx.graphics.getWidth() / 1.2f);

        table.add(buttonImage).right();
        table.add(buttonLabel).right();
    }

    public void activate(NPC npc) {
        this.activated = true;

        this.npcSprite = npc.getDialogSprite();
        this.npcPosX = 0;
        this.npcSprite.setPosition(npcPosX, Gdx.graphics.getHeight() / (5 * unit));
        this.playerPosX = Gdx.graphics.getWidth() / (unit * 2.5f);

        this.npc = npc;
        this.currentDialog = npc.getDialogTree().getInitialDialog();

        resetButtons();
    }

    public void drawCharacters(SpriteBatch batch) {
        if (npcPosX <= npcPosXGoal) {
            npcPosX += SLIDE_SPEED;
            this.npcSprite.setX(npcPosX);
        }

        if (playerPosX > playerPosXGoal) {
            playerPosX -= SLIDE_SPEED;
            this.playerSprite.setX(playerPosX);
        }

        this.npcSprite.draw(batch);
        this.playerSprite.draw(batch);
    }

    public void draw(SpriteBatch hudBatch) {

        nameLabel.setText(npc.getName() + ":");
        textLabel.setText(currentDialog.getText());

        table.draw(hudBatch, 1f);
    }

    public void checkForInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (activeIndex > 0) {
                activeIndex--;
                setActiveIndex(activeIndex);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (activeIndex < choiceGroup.getChildren().size - 1) {
                activeIndex++;
                setActiveIndex(activeIndex);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Dialog leadTo = currentDialog.getChoices().get(activeIndex).getLeadsTo();
            if (leadTo == null) {
                quit();
            } else {
                currentDialog = leadTo;

                resetButtons();
            }
        }
    }

    private void quit() {
        this.activated = false;
    }

    private void setActiveIndex(int index) {
        activeIndex = index;
        choiceGroup.getChildren().forEach(b -> ((TextButton) b).setChecked(false));
        ((TextButton) choiceGroup.getChildren().get(activeIndex)).setChecked(true);
    }

    private void resetButtons() {
        choiceGroup.clear();

        for (Choice choice : currentDialog.getChoices()) {
            TextButton button = new TextButton(choice.getText(), uiSkin);
            button.getStyle().checkedFontColor = Color.YELLOW;
            choiceGroup.addActor(button);
        }

        choiceGroup.invalidate();
        setActiveIndex(0);
    }


    @Override
    public void dispose() {
        super.dispose();
        buttonTexture.dispose();
    }

}
