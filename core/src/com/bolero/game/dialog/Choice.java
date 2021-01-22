package com.bolero.game.dialog;

public class Choice {
  private String text;
  private Dialog next;
  private String leadsTo;

  public String getText() {
    return text;
  }

  public Dialog getNext() {
    return next;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setNext(Dialog next) {
    this.next = next;
  }

  public String getLeadsTo() {
    return leadsTo;
  }

  public void setLeadsTo(String leadsTo) {
    this.leadsTo = leadsTo;
  }
}
