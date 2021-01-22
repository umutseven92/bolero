package com.bolero.game.dialog;

public abstract class AbstractText {
  private String textID;

  private String text;

  public String getTextID() {
    return textID;
  }

  @SuppressWarnings("unused") // Used by snakeyaml
  public void setTextID(String textID) {
    this.textID = textID;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
