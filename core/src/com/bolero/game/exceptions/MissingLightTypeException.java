package com.bolero.game.exceptions;

public class MissingLightTypeException extends MapperException {
    public MissingLightTypeException() {
        super("Type missing for light.");
    }
}
