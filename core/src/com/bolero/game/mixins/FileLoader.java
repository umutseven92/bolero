package com.bolero.game.mixins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.FileNotFoundException;
import lombok.val;

public interface FileLoader {
  // Gets the FileHandle from a full path within the assets directory.
  // Will throw FileNotFoundException if the file does not exist.
  default FileHandle getFile(String fullPath) throws FileNotFoundException {

    val file = Gdx.files.internal(fullPath);

    if (!file.exists()) {
      throw new FileNotFoundException(String.format("%s does not exist.", fullPath));
    }

    return file;
  }
}
