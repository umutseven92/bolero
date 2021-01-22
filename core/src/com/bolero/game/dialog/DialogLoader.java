package com.bolero.game.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.exceptions.FileFormatException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DialogLoader {

  public DialogTree load(FileHandle file, BundleController bundleController)
      throws FileFormatException {
    if (!file.extension().equals("yaml")) {
      throw new FileFormatException(file.name(), new String[] {"yaml"});
    }
    Yaml yaml = new Yaml(new Constructor(DialogTree.class));

    DialogTree dTree = yaml.load(file.readString());

    return dTree;
  }
}
