package com.bolero.game.dtos;

import lombok.Data;

@Data
public class ConfigDTO {
  private MapsDTO maps;
  private KeysDTO keys;
  private SunDTO sun;
  private ClockDTO clock;
}
