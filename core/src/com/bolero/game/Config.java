package com.bolero.game;

import lombok.Data;

@Data
public class Config {
  private String initialMap;
  private String mapsPath;
  private Keys keys;
}
