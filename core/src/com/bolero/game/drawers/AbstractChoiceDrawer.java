package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.bolero.game.BoleroGame;
import com.bolero.game.dtos.KeysDTO;
import java.util.function.Consumer;
import lombok.Setter;
import lombok.val;

public abstract class AbstractChoiceDrawer extends AbstractDrawer {
  private static final String CHOICE_PREFIX = "-> ";
  private int activeIndex;
  protected VerticalGroup choiceGroup;
  protected final KeysDTO keys;

  @Setter Consumer<Integer> onSubmit;

  public AbstractChoiceDrawer() {
    this(false);
  }

  public AbstractChoiceDrawer(boolean debug) {
    super(debug);

    keys = BoleroGame.getConfig().getKeys();
  }

  @Override
  protected void initTable() {
    choiceGroup = new VerticalGroup();
    super.initTable();
  }

  protected void setActiveIndex(int index) {
    activeIndex = index;
    choiceGroup.getChildren().forEach(this::clearChoice);

    choose(index);
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
      if (onSubmit != null) {
        onSubmit.accept(activeIndex);
      }
    }
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
}
