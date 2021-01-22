package com.bolero.game.exceptions;

import java.util.Arrays;

public class FileFormatException extends Exception {
  public FileFormatException(String fileName, String[] validFormats) {
    super(
        String.format(
            "%s has invalid file type. Valid types: %s", fileName, Arrays.toString(validFormats)));
  }
}
