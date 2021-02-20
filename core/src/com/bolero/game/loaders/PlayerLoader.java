package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.PlayerDTO;
import com.bolero.game.exceptions.FileFormatException;
import org.yaml.snakeyaml.constructor.Constructor;

public class PlayerLoader extends AbstractLoader implements Loader<PlayerDTO> {
  @Override
  public PlayerDTO load(FileHandle file) throws FileFormatException {
    return super.load(file, new Constructor(PlayerDTO.class));
  }
}
