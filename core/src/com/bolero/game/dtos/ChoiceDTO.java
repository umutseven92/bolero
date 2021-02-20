package com.bolero.game.dtos;

import com.bolero.game.dialog.Choice;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.managers.BundleManager;
import lombok.Data;

@Data
public class ChoiceDTO {
  private String textID;
  private DialogDTO next;

  public Choice toChoice(BundleManager bundleManager) {
    Choice choice = new Choice();
    String text = bundleManager.getString(getTextID());
    choice.setText(text);

    if (next != null) {
      Dialog dialog = next.toDialog(bundleManager);
      choice.setNext(dialog);
    }

    return choice;
  }
}
