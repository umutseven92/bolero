package com.bolero.game.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MovementDTO {
  private float speed;
  private float maxVelocity;

  public MovementDTO(float speed, float maxVelocity) {
    this.speed = speed;
    this.maxVelocity = maxVelocity;
  }
}
