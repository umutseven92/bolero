package com.bolero.game.exceptions;

public class MissingInteractionTypeException extends Exception {
    public MissingInteractionTypeException() {
        super("Type missing for interaction.");
    }
}
