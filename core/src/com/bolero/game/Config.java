package com.bolero.game;

import com.badlogic.gdx.Gdx;
import com.bolero.game.dtos.ConfigDTO;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.exceptions.InvalidConfigurationException;
import com.bolero.game.loaders.ConfigLoader;
import java.io.FileNotFoundException;
import lombok.val;

public class Config {
  private ConfigDTO config;

  public void load() throws FileNotFoundException, InvalidConfigurationException {
    val loader = new ConfigLoader();
    val file = Gdx.files.internal("./config/game.yaml");

    this.config = loader.load(file);
  }

  public ConfigDTO getConfig() throws ConfigurationNotLoadedException {
    if (config == null) {
      throw new ConfigurationNotLoadedException();
    }

    return config;
  }
}
