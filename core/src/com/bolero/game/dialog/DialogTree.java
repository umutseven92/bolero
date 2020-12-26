package com.bolero.game.dialog;

import java.util.ArrayList;

public class DialogTree {
    private final ArrayList<Dialog> dialogList;

    public DialogTree() {
        this.dialogList = new ArrayList<>();
    }

    public void addDialog(Dialog dialog) {
        dialogList.add(dialog);
    }

    public Dialog getInitialDialog() {
        return getDialog(0);
    }

    public Dialog getDialog(int index) {
        return dialogList.get(index);
    }
}
