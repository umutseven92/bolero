package com.bolero.game;

import com.bolero.game.dtos.ClockDTO;
import com.bolero.game.dtos.SunDTO;
import lombok.Data;

@Data
public class Config {
  private String initialMap;
  private String mapsPath;
  private Keys keys;
  private SunDTO sun;
  private ClockDTO clock;
}
