package com.bolero.game.icons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.BoleroGame;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import lombok.val;
import lombok.var;

public interface InteractButtonImage {

  default FileHandle getInteractButtonImage() throws ConfigurationNotLoadedException {
    val key = BoleroGame.getConfig().getKeys().getInteract();
    var keyfile = Gdx.files.internal(String.format("buttons/green-%s.png", key));

    if (!keyfile.exists()) {
      keyfile = Gdx.files.internal("buttons/green-blank.png");
    }

    return keyfile;
  }
}
