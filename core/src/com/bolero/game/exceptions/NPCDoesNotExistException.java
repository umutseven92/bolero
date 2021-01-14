package com.bolero.game.exceptions;

public class NPCDoesNotExistException extends MapperException {
    public NPCDoesNotExistException(String npcName) {
        super(String.format("NPC with the name %s does not exist.", npcName));
    }
}
