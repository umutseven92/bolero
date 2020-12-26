package com.bolero.game.exceptions;

public class WrongInteractionTypeException extends MapperException {
    public WrongInteractionTypeException(String type) {
        super(String.format("Type %s is invalid.", type));
    }

}
