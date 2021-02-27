package com.bolero.game.exceptions;

public class ConfigurationNotLoadedException extends ConfigurationException {

  public ConfigurationNotLoadedException() {
    super("Please call load() before accessing configuration!");
  }
}
