package com.bolero.game;

public class Config {
  private String initialMap;

  @SuppressWarnings("unused") // Used by snakeyaml
  public String getInitialMap() {
    return initialMap;
  }

  @SuppressWarnings("unused") // Used by snakeyaml
  public void setInitialMap(String initialMap) {
    this.initialMap = initialMap;
  }
}
