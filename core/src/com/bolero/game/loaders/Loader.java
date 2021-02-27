package com.bolero.game.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.exceptions.InvalidConfigurationException;
import java.io.FileNotFoundException;

public interface Loader<T> {
  T load(FileHandle file) throws FileNotFoundException, InvalidConfigurationException;
}
