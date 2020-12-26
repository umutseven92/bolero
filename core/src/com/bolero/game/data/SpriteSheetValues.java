package com.bolero.game.data;

public class SpriteSheetValues {
    public final int rows;
    public final int cols;
    public final int idleRow;
    public final int walkRow;

    public SpriteSheetValues(int rows, int cols, int idleRow, int walkRow) {
        this.rows = rows;
        this.cols = cols;
        this.idleRow = idleRow;
        this.walkRow = walkRow;
    }

}
