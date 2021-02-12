package com.bolero.game.dialog;

import java.util.List;

public class DialogTree {
  private final List<Dialog> dialogs;

  public DialogTree(List<Dialog> dialogs) {
    this.dialogs = dialogs;
  }

  public Dialog getInitialDialog() {
    // Since dialogs are written in reverse in the yaml file, the initial dialog is the last one.
    return dialogs.get(dialogs.size() - 1);
  }

  public boolean isEmpty() {
    return this.dialogs.isEmpty();
  }
}
