package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.exceptions.FileFormatException;
import org.yaml.snakeyaml.constructor.Constructor;

public class NPCLoader extends AbstractLoader implements Loader<NpcsDTO> {
  public NpcsDTO load(FileHandle file) throws FileFormatException {
    return super.load(file, new Constructor(NpcsDTO.class));
  }
}
