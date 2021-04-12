package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.icons.InteractButtonImage;
import com.bolero.game.managers.BundleManager;
import java.util.ArrayList;
import lombok.val;

public class PauseDrawer extends AbstractChoiceDrawer implements Disposable, InteractButtonImage {

  private final ArrayList<Label> choices;

  public PauseDrawer(BundleManager bundleManager) {
    super(false);
    super.setOnSubmit(this::submit);
    val cont = new Label(bundleManager.getString("continue"), uiSkin);
    val quit = new Label(bundleManager.getString("quit"), uiSkin);

    choices = new ArrayList<>();
    choices.add(cont);
    choices.add(quit);
  }

  public void init() {
    super.initTable();

    table.center();
    table.pad(20);

    choiceGroup.columnCenter();

    table.add(choiceGroup).expand();

    table.row();
    resetButtons();
  }

  private void resetButtons() {
    choiceGroup.clear();

    for (val choice : choices) {
      choiceGroup.addActor(choice);
    }

    choiceGroup.invalidate();
    super.setActiveIndex(0);
  }

  private void submit(int index) {
    if (index == 0) {
      super.deactivate();
    } else {
      Gdx.app.exit();
    }
  }
}
