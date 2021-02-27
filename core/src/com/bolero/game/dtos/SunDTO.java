package com.bolero.game.dtos;

import lombok.Data;

@Data
public class SunDTO {
  private float dayLight;
  private float nightLight;
  private int dawnStart;
  private int dawnEnd;
  private int duskStart;
  private int duskEnd;
}
