package com.bolero.game.exceptions;

public class MissingSpawnTypeException extends MapperException {
    public MissingSpawnTypeException() {
        super("Type missing for spawn point.");
    }
}
