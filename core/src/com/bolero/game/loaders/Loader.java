package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.exceptions.FileFormatException;

public interface Loader<T> {
  T load(FileHandle file) throws FileFormatException;
}
