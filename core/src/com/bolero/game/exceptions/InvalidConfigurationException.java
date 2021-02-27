package com.bolero.game.exceptions;

public class InvalidConfigurationException extends ConfigurationException {
  public InvalidConfigurationException(String value, String range) {
    super(String.format("Invalid configuration for value %s! Correct range: (%s)", value, range));
  }
}
