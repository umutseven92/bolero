package com.bolero.game.dtos;

import lombok.Data;

@Data
public class SpriteSheetValuesDTO {
  private int rows;
  private int cols;
  private int idleRow;
  private int walkRow;
}
