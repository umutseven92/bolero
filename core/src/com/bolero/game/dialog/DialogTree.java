package com.bolero.game.dialog;

import java.util.List;

public class DialogTree {
  private List<Dialog> dialogs;

  public List<Dialog> getDialogs() {
    return dialogs;
  }

  @SuppressWarnings("unused") // Used by snakeyaml
  public void setDialogs(List<Dialog> dialogs) {
    this.dialogs = dialogs;
  }

  public Dialog getInitialDialog() {
    // Since dialogs are written in reverse in the yaml file, the initial dialog is the last one.
    return dialogs.get(dialogs.size() - 1);
  }
}
