package com.bolero.game.dialog;

import java.util.List;

public class Dialog extends AbstractText {
  private List<Choice> choices;

  public List<Choice> getChoices() {
    return choices;
  }

  public void setChoices(List<Choice> choices) {
    this.choices = choices;
  }
}
