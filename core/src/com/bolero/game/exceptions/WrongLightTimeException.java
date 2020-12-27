package com.bolero.game.exceptions;

public class WrongLightTimeException extends MapperException {
    public WrongLightTimeException(String time) {
        super(String.format("Time %s is invalid.", time));
    }
}
