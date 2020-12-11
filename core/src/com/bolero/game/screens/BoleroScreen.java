package com.bolero.game.screens;

import com.bolero.game.BoleroGame;

public class BoleroScreen extends GameScreen {

    public BoleroScreen(BoleroGame game) {
        super(game, "map/bolero.tmx", new int[]{0, 1, 2, 3}, new int[]{4});
    }
}
