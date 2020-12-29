package com.bolero.game.screens;

import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MapperException;

public class BoleroScreen extends GameScreen {

    public BoleroScreen(BoleroGame game, String spawnPos) throws MapperException {
        super(game, "bolero", "map/bolero.tmx", spawnPos);
    }
}
