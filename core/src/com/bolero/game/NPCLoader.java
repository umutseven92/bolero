package com.bolero.game;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.exceptions.FileFormatException;
import lombok.val;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public class NPCLoader {
  public NpcsDTO load(FileHandle file) throws FileFormatException {
    if (!file.extension().equals("yaml")) {
      throw new FileFormatException(file.name(), new String[] {"yaml"});
    }

    val repr = new Representer();
    repr.getPropertyUtils().setSkipMissingProperties(true);
    val yaml = new Yaml(new Constructor(NpcsDTO.class), repr);

    return yaml.load(file.readString());
  }
}
