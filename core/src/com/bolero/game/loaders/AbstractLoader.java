package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import java.io.FileNotFoundException;
import lombok.val;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public abstract class AbstractLoader {
  public <T> T load(FileHandle file, Constructor constructor) throws FileNotFoundException {
    if (!file.extension().equals("yaml")) {
      throw new FileNotFoundException(
          String.format("%s has wrong file type, supported file types are: yaml", file.path()));
    }

    val repr = new Representer();
    repr.getPropertyUtils().setSkipMissingProperties(true);
    val yaml = new Yaml(constructor, repr);

    return yaml.load(file.readString());
  }
}
