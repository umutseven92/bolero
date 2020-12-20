package com.bolero.game.screens;

import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MissingInteractionTypeException;
import com.bolero.game.exceptions.MissingSpawnTypeException;
import com.bolero.game.exceptions.WrongInteractionTypeException;

public class HouseScreen extends GameScreen {
    public HouseScreen(BoleroGame game) throws MissingInteractionTypeException, WrongInteractionTypeException, MissingSpawnTypeException {
        super(game, "map/house1.tmx");
    }
}
