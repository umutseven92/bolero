package com.bolero.game.dialog;

public class Choice {
  private final String text;

  private final Dialog leadsTo;

  public Choice(String text) {
    this(text, null);
  }

  public Choice(String text, Dialog leadsTo) {
    this.text = text;
    this.leadsTo = leadsTo;
  }

  public String getText() {
    return text;
  }

  public Dialog getLeadsTo() {
    return leadsTo;
  }
}
