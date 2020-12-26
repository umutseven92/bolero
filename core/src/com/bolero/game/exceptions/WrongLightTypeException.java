package com.bolero.game.exceptions;

public class WrongLightTypeException extends MapperException {
    public WrongLightTypeException(String type) {
        super(String.format("Type %s is invalid.", type));
    }
}
