package com.bolero.game.screens;

import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MapperException;

public class HouseScreen extends GameScreen {
    public HouseScreen(BoleroGame game, String spawnPos) throws MapperException {
        super(game, "house1", "map/house1.tmx", spawnPos);
    }
}
