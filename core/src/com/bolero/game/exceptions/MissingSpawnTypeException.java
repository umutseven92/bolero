package com.bolero.game.exceptions;

public class MissingSpawnTypeException extends Exception {
    public MissingSpawnTypeException() {
        super("Type missing for spawn point.");
    }
}
