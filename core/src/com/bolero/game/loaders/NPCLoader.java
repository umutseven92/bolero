package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.NpcsDTO;
import java.io.FileNotFoundException;
import org.yaml.snakeyaml.constructor.Constructor;

public class NPCLoader extends AbstractLoader implements Loader<NpcsDTO> {
  public NpcsDTO load(FileHandle file) throws FileNotFoundException {
    return super.load(file, new Constructor(NpcsDTO.class));
  }
}
