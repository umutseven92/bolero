package com.bolero.game.dialog;

import java.util.List;

public class Dialog {
  private String text;
  private List<Choice> choices;

  public String getText() {
    return text;
  }

  public List<Choice> getChoices() {
    return choices;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setChoices(List<Choice> choices) {
    this.choices = choices;
  }
}
