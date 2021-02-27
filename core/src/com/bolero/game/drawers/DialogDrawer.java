package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.icons.InteractButtonImage;
import lombok.val;

public class DialogDrawer extends AbstractDrawer implements Disposable, InteractButtonImage {
  private static final float SLIDE_SPEED = 5f;
  private static final String CHOICE_PREFIX = "-> ";

  private final SpriteBatch batch;
  private final Table table;
  private final Texture buttonTexture;
  private final Label nameLabel;
  private final Label textLabel;
  private final VerticalGroup choiceGroup;
  private final Sprite playerSprite;

  private final float npcPosXGoal;
  private final float playerPosXGoal;
  private final float spriteY;

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

  public DialogDrawer(Player player, OrthographicCamera camera)
      throws ConfigurationNotLoadedException {
    super();
    this.batch = new SpriteBatch();
    this.activated = false;
    this.playerSprite = player.getDialogSprite();
    this.spriteY = Gdx.graphics.getHeight() / 5f;

    this.npcPosXGoal = camera.viewportWidth / 2f;

    this.playerPosXGoal = Gdx.graphics.getWidth() - this.playerSprite.getWidth();
    this.playerPosX = playerPosXGoal + 10f;

    this.playerSprite.setPosition(playerPosX, spriteY);

    val file = getInteractButtonImage();
    buttonTexture = new Texture(file);
    val buttonImage = new Image(buttonTexture);

    table = new Table();
    table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    table.bottom();
    table.padBottom(Gdx.graphics.getHeight() / 10f);

    nameLabel = new Label("", uiSkin);
    textLabel = new Label("", uiSkin);
    val buttonLabel = new Label("to choose", uiSkin);

    textLabel.setWrap(true);
    table.add(nameLabel).width(Gdx.graphics.getWidth() / 1.2f);
    table.row();
    table.add(textLabel).width(Gdx.graphics.getWidth() / 1.2f);
    table.row();

    choiceGroup = new VerticalGroup();
    choiceGroup.left();
    choiceGroup.columnLeft();

    table.add(choiceGroup).width(Gdx.graphics.getWidth() / 1.2f);

    table.add(buttonImage).right();
    table.add(buttonLabel).right();
  }

  public void activate(NPC npc) {
    this.activated = true;

    this.npcSprite = npc.getDialogSprite();
    this.npcPosX = npcPosXGoal - 100f;
    this.npcSprite.setPosition(npcPosX, spriteY);
    this.playerPosX = playerPosXGoal + 100f;

    this.npc = npc;
    this.currentDialog = npc.getDialogTree().getInitialDialog();

    resetButtons();
  }

  public void drawCharacters() {
    if (npcPosX <= npcPosXGoal) {
      npcPosX += SLIDE_SPEED;
      this.npcSprite.setX(npcPosX);
    }

    if (playerPosX > playerPosXGoal) {
      playerPosX -= SLIDE_SPEED;
      this.playerSprite.setX(playerPosX);
    }

    this.batch.begin();

    // NPC sprite during dialog should always face right.
    if (npcSprite.isFlipX()) {
      npcSprite.flip(true, false);
    }

    // Player sprite during dialog should always face left.
    if (!playerSprite.isFlipX()) {
      playerSprite.flip(true, false);
    }

    this.npcSprite.draw(batch);
    this.playerSprite.draw(batch);

    this.batch.end();
  }

  public void drawUI(SpriteBatch hudBatch) {

    nameLabel.setText(npc.getName() + ":");
    textLabel.setText(currentDialog.getText());

    table.draw(hudBatch, 1f);
  }

  public void checkForInput() {
    if (Gdx.input.isKeyJustPressed(keys.getUpInput())) {
      if (activeIndex > 0) {
        activeIndex--;
        setActiveIndex(activeIndex);
      }
    } else if (Gdx.input.isKeyJustPressed(keys.getDownInput())) {
      if (activeIndex < choiceGroup.getChildren().size - 1) {
        activeIndex++;
        setActiveIndex(activeIndex);
      }
    } else if (Gdx.input.isKeyJustPressed(keys.getInteractInput())) {
      val leadTo = currentDialog.getChoices().get(activeIndex).getNext();
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
    choiceGroup.getChildren().forEach(this::clearChoice);

    choose(index);
  }

  private void clearChoice(Actor choice) {
    val label = (Label) choice;
    val text = label.getText().toString();
    if (text.startsWith(CHOICE_PREFIX)) {
      label.setText(text.substring(CHOICE_PREFIX.length()));
    }
    choice.setColor(Color.WHITE);
  }

  private void choose(int index) {
    val chosen = (Label) choiceGroup.getChildren().get(index);
    chosen.setColor(Color.YELLOW);
    val text = chosen.getText().toString();

    chosen.setText(CHOICE_PREFIX + text);
  }

  private void resetButtons() {
    choiceGroup.clear();

    for (val choice : currentDialog.getChoices()) {
      val button = new Label(choice.getText(), uiSkin);
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
