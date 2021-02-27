package com.bolero.game.dtos;

import lombok.Data;

@Data
public class MapsDTO {
  private String initial;
  private String path;
  private LayersDTO layers;
}
