package com.bolero.game.mappers;

import com.bolero.game.exceptions.MissingPropertyException;
import java.io.FileNotFoundException;

/** Mappers read nodes from a TiledMap and convert it to @param <T> */
public interface Mapper<T> {
  T map() throws MissingPropertyException, FileNotFoundException;
}
