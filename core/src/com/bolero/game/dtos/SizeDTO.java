package com.bolero.game.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SizeDTO {
  private float width;
  private float height;

  public SizeDTO(float width, float height) {
    this.width = width;
    this.height = height;
  }
}
