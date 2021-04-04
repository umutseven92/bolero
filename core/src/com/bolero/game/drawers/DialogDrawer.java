package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.icons.InteractButtonImage;
import com.bolero.game.managers.BundleManager;
import lombok.val;

public class DialogDrawer extends AbstractChoiceDrawer implements Disposable, InteractButtonImage {

  private final Texture buttonTexture;
  private final Label nameLabel;
  private final Label textLabel;
  private final Label buttonLabel;
  private final Image playerImage;
  private final Image buttonImage;

  private Image npcImage;
  private Dialog currentDialog;

  public DialogDrawer(Player player, BundleManager bundleManager)
      throws ConfigurationNotLoadedException {
    super();
    super.setOnSubmit(this::submit);

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
    super.activate();

    Sprite npcSprite = npc.getDialogSprite();
    this.npcImage = new Image(new SpriteDrawable(npcSprite));
    this.currentDialog = npc.getDialogTree().getInitialDialog();

    nameLabel.setText(npc.getName() + ":");
    textLabel.setText(currentDialog.getText());

    init(Gdx.graphics.getWidth());
  }

  private void submit(int activeIndex) {
    val leadTo = currentDialog.getChoices().get(activeIndex).getNext();
    if (leadTo == null) {
      quit();
    } else {
      currentDialog = leadTo;

      resetButtons();
    }
  }

  private void quit() {
    super.deactivate();
  }

  private void resetButtons() {
    choiceGroup.clear();

    for (val choice : currentDialog.getChoices()) {
      val button = new Label(choice.getText(), uiSkin);
      choiceGroup.addActor(button);
    }

    choiceGroup.invalidate();
    super.setActiveIndex(0);
  }

  @Override
  public void dispose() {
    super.dispose();
    buttonTexture.dispose();
  }
}
