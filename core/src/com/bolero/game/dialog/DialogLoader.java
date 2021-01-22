package com.bolero.game.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.exceptions.FileFormatException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.util.MissingResourceException;

public class DialogLoader {

  public DialogTree load(FileHandle file, BundleController bundleController)
      throws FileFormatException {
    if (!file.extension().equals("yaml")) {
      throw new FileFormatException(file.name(), new String[] {"yaml"});
    }

    Representer representer = new Representer();
    representer.getPropertyUtils().setSkipMissingProperties(true);
    Yaml yaml = new Yaml(new Constructor(DialogTree.class), representer);

    DialogTree dTree = yaml.load(file.readString());

    validateTextValues(dTree, bundleController);

    return dTree;
  }

  private void validateTextValues(DialogTree dialogTree, BundleController bundleController)
      throws MissingResourceException {
    for (Dialog dialog : dialogTree.getDialogs()) {
      // Throws MissingResourceException if id does not exist in string*.properties.
      String text = bundleController.getString(dialog.getTextID());
      dialog.setText(text);

      for (Choice choice : dialog.getChoices()) {
        String choiceText = bundleController.getString(choice.getTextID());
        choice.setText(choiceText);
      }
    }
  }
}
