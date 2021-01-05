package com.bolero.game.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.bolero.game.controllers.BundleController;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogTree {
    private final ArrayList<Dialog> dialogList;

    public DialogTree() {
        this.dialogList = new ArrayList<>();
    }

    public void load(FileHandle file, BundleController bundleController) {
        JsonReader json = new JsonReader();
        JsonValue base = json.parse(file);

        HashMap<String, Dialog> dialogMap = new HashMap<String, Dialog>();

        for (JsonValue component : base.get("dialogs")) {
            String id = component.getString("id");
            String textID = component.getString("text");
            String text = bundleController.getString(textID);

            Dialog dialog = new Dialog(text);
            this.addDialog(dialog);
            dialogMap.put(id, dialog);
        }

        for (JsonValue component : base.get("choices")) {
            String textID = component.getString("text");
            String text = bundleController.getString(textID);

            Choice choice;
            if (component.has("leadsTo")) {
                String leadsTo = component.getString("leadsTo");
                Dialog dialog = dialogMap.get(leadsTo);
                choice = new Choice(text, dialog);
            } else {
                choice = new Choice(text);
            }
            for (String under : component.get("under").asStringArray()) {
                Dialog dialog = dialogMap.get(under);

                dialog.addChoice(choice);
            }

        }
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
