package com.bolero.game.exceptions;

public class MissingInteractionTypeException extends MapperException {
    public MissingInteractionTypeException() {
        super("Type missing for interaction.");
    }
}
