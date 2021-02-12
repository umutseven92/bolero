package com.bolero.game.dtos;

public class MovementDTO {
  private float speed;
  private float maxVelocity;

  public MovementDTO() {}

  public MovementDTO(float speed, float maxVelocity) {
    this.speed = speed;
    this.maxVelocity = maxVelocity;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float getMaxVelocity() {
    return maxVelocity;
  }

  public void setMaxVelocity(float maxVelocity) {
    this.maxVelocity = maxVelocity;
  }
}
