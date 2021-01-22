package com.bolero.game.dialog;

import java.util.List;

public class DialogTree {
  private List<Dialog> dialogs;

  public Dialog getInitialDialog() {
    return dialogs.get(0);
  }

  public void setDialogs(List<Dialog> dialogs) {
    this.dialogs = dialogs;
  }
}
