package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.ConfigDTO;
import com.bolero.game.dtos.SunDTO;
import com.bolero.game.exceptions.InvalidConfigurationException;
import java.io.FileNotFoundException;
import org.yaml.snakeyaml.constructor.Constructor;

public class ConfigLoader extends AbstractLoader implements Loader<ConfigDTO> {
  @Override
  public ConfigDTO load(FileHandle file)
      throws FileNotFoundException, InvalidConfigurationException {
    ConfigDTO dto = super.load(file, new Constructor(ConfigDTO.class));

    SunDTO sun = dto.getSun();
    if (sun.getDayLight() < 0 || sun.getDayLight() > 1) {
      throw new InvalidConfigurationException("dayLight", "> 0, < 1");
    }

    if (sun.getNightLight() < 0 || sun.getNightLight() > 1) {
      throw new InvalidConfigurationException("nightLight", "> 0, < 1");
    }

    // TODO: More validation
    return dto;
  }
}
