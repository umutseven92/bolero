package com.bolero.game.mappers;

import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.NPCDoesNotExistException;

import java.io.FileNotFoundException;

public interface Mapper<T> {
  T map() throws MissingPropertyException, NPCDoesNotExistException, FileNotFoundException, FileFormatException;
}
