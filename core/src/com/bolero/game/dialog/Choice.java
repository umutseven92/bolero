package com.bolero.game.dialog;

public class Choice extends AbstractText {
  private Dialog next;

  public Dialog getNext() {
    return next;
  }

  public void setNext(Dialog next) {
    this.next = next;
  }
}
