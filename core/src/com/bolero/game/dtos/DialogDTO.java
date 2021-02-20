package com.bolero.game.dtos;

import com.bolero.game.dialog.Choice;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.managers.BundleManager;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DialogDTO {
  private List<ChoiceDTO> choices;

  private String textID;

  private List<Choice> getChoiceList(BundleManager bundleManager) {
    List<Choice> choiceList = new ArrayList<>();

    for (ChoiceDTO choiceDTO : choices) {
      Choice choice = choiceDTO.toChoice(bundleManager);
      choiceList.add(choice);
    }

    return choiceList;
  }

  public Dialog toDialog(BundleManager bundleManager) {
    String text = bundleManager.getString(getTextID());
    List<Choice> choices = getChoiceList(bundleManager);
    Dialog dialog = new Dialog();

    dialog.setText(text);
    dialog.setChoices(choices);
    return dialog;
  }
}
