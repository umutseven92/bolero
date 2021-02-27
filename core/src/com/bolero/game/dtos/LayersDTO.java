package com.bolero.game.dtos;

import lombok.Data;

@Data
public class LayersDTO {
  private String collision;
  private String spawn;
  private String schedule;
  private String interaction;
  private String lights;
  private String path;
}
