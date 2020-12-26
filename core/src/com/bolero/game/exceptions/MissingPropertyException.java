package com.bolero.game.exceptions;

public class MissingPropertyException extends MapperException {
    public MissingPropertyException(String propertyName) {
        super(propertyName + " is missing.");
    }
}
