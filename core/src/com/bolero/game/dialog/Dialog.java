package com.bolero.game.dialog;

import java.util.ArrayList;

public class Dialog {
  private final String text;
  private final ArrayList<Choice> choices;

  public Dialog(String text) {
    this.text = text;
    this.choices = new ArrayList<>();
  }

  public void addChoice(Choice choice) {
    choices.add(choice);
  }

  public String getText() {
    return text;
  }

  public ArrayList<Choice> getChoices() {
    return choices;
  }
}
