package com.bolero.game.screens;

import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.MapperException;

import java.io.FileNotFoundException;

public class BoleroScreen extends GameScreen {

    public BoleroScreen(BoleroGame game, String spawnPos) throws MapperException, FileNotFoundException {
        super(game, "bolero", "map/bolero.tmx", spawnPos);
    }
}
