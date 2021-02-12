package com.bolero.game;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.exceptions.FileFormatException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public class NPCLoader {
  public NpcsDTO load(FileHandle file) throws FileFormatException {
    if (!file.extension().equals("yaml")) {
      throw new FileFormatException(file.name(), new String[] {"yaml"});
    }

    Representer representer = new Representer();
    representer.getPropertyUtils().setSkipMissingProperties(true);
    Yaml yaml = new Yaml(new Constructor(NpcsDTO.class), representer);

    return yaml.load(file.readString());
  }
}
