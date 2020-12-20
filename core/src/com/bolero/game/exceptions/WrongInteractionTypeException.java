package com.bolero.game.exceptions;

public class WrongInteractionTypeException extends Exception {
    public WrongInteractionTypeException(String type) {
        super(String.format("Type %s is invalid.", type));
    }

}
