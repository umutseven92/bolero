package com.bolero.game.screens;

import com.bolero.game.BoleroGame;

public class HouseScreen extends GameScreen {
    public HouseScreen(BoleroGame game) {
        super(game, "map/house1.tmx", new int[]{0, 1, 2}, new int[]{3});
    }
}
