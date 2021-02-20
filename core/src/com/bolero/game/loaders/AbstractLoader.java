package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.exceptions.FileFormatException;
import lombok.val;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public abstract class AbstractLoader {
  public <T> T load(FileHandle file, Constructor constructor) throws FileFormatException {
    if (!file.extension().equals("yaml")) {
      throw new FileFormatException(file.name(), new String[] {"yaml"});
    }

    val repr = new Representer();
    repr.getPropertyUtils().setSkipMissingProperties(true);
    val yaml = new Yaml(constructor, repr);

    return yaml.load(file.readString());
  }
}
