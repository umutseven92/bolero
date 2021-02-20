package com.bolero.game.data;

import lombok.Data;

@Data
public class SpriteSheetValues {
  private final int rows;
  private final int cols;
  private final int idleRow;
  private final int walkRow;

  public SpriteSheetValues(int rows, int cols, int idleRow, int walkRow) {
    this.rows = rows;
    this.cols = cols;
    this.idleRow = idleRow;
    this.walkRow = walkRow;
  }
}
