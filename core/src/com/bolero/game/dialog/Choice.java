package com.bolero.game.dialog;

public class Choice extends AbstractText {
  private Dialog next;

  public Dialog getNext() {
    return next;
  }

  @SuppressWarnings("unused") // Used by snakeyaml
  public void setNext(Dialog next) {
    this.next = next;
  }
}
