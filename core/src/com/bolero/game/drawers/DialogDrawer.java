package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.icons.InteractButtonImage;
import com.bolero.game.managers.BundleManager;
import lombok.val;

public class DialogDrawer extends AbstractDrawer implements Disposable, InteractButtonImage {
  private static final String CHOICE_PREFIX = "-> ";

  private final Texture buttonTexture;
  private final Label nameLabel;
  private final Label textLabel;
  private final Label buttonLabel;
  private final Image playerImage;
  private final Image buttonImage;

  private Image npcImage;
  private VerticalGroup choiceGroup;
  private Dialog currentDialog;
  private NPC npc;

  private boolean activated;
  private int activeIndex;

  public boolean isActivated() {
    return activated;
  }

  public DialogDrawer(Player player, BundleManager bundleManager)
      throws ConfigurationNotLoadedException {
    super();
    this.activated = false;
    Sprite playerSprite = player.getDialogSprite();

    val file = getInteractButtonImage();
    buttonTexture = new Texture(file);
    buttonImage = new Image(buttonTexture);
    playerImage = new Image(new SpriteDrawable(playerSprite));

    npcImage = new Image();
    nameLabel = new Label("", uiSkin);
    textLabel = new Label("", uiSkin);
    buttonLabel = new Label(bundleManager.getString("choose"), uiSkin);

    textLabel.setWrap(true);
  }

  public void init(int width) {
    super.initTable();

    table.bottom();
    table.padBottom(20);
    table.padRight(20);
    table.padLeft(20);
    table.add(npcImage).expandX().center();

    table.add(playerImage).expandX().center();

    table.row();
    table.add(nameLabel).colspan(2).expandX().left();
    table.row();
    table.add(textLabel).colspan(2).width(width - 100).left();
    table.row();

    choiceGroup = new VerticalGroup();
    choiceGroup.left();
    choiceGroup.columnLeft();

    table.add(choiceGroup).expandX().left();

    table.row();
    Table buttonTable = new Table();

    buttonTable.add(buttonImage).expandX().right();
    buttonTable.add(buttonLabel).right();
    table.add(buttonTable).colspan(2).expandX().right();

    if (currentDialog != null) {
      resetButtons();
    }
  }

  public void activate(NPC npc) {
    this.activated = true;

    Sprite npcSprite = npc.getDialogSprite();
    npcImage = new Image(new SpriteDrawable(npcSprite));

    this.npc = npc;
    this.currentDialog = npc.getDialogTree().getInitialDialog();
    init(Gdx.graphics.getWidth());
  }

  public void draw() {
    nameLabel.setText(npc.getName() + ":");
    textLabel.setText(currentDialog.getText());

    super.draw();
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
