package com.bolero.game.screens;

import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MapperException;

public class HouseScreen extends GameScreen {
    public HouseScreen(BoleroGame game) throws MapperException {
        super(game, "map/house1.tmx");
    }
}
