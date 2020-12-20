package com.bolero.game.screens;

import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MissingInteractionTypeException;
import com.bolero.game.exceptions.MissingSpawnTypeException;
import com.bolero.game.exceptions.WrongInteractionTypeException;

public class BoleroScreen extends GameScreen {

    public BoleroScreen(BoleroGame game) throws MissingInteractionTypeException, WrongInteractionTypeException, MissingSpawnTypeException {
        super(game, "map/bolero.tmx");
    }
}
