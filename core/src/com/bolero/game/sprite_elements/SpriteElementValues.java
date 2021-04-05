package com.bolero.game.sprite_elements;

import lombok.Data;

@Data
public class SpriteElementValues {
  // How many rows the sprite sheet has.
  private final int rows;

  // How many columns the sprite sheet has.
  private final int cols;

  // Which row to start from.
  private final int rowStartIndex;

  // How many rows to iterate over.
  private final int rowAmount;

  // Which column to start from.
  private final int colStartIndex;

  // How many columns to iterate over.
  private final int colAmount;
}
