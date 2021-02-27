package loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.PlayerDTO;
import com.bolero.game.loaders.PlayerLoader;
import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(src.GdxTestRunner.class)
public class PlayerLoaderTests {
  FileHandle fileHandle;

  @Before
  public void setUp() {
    fileHandle = Gdx.files.local("assets/player-test.yaml");
  }

  @Test
  public void canLoadPlayer() throws FileNotFoundException {
    PlayerLoader loader = new PlayerLoader();
    PlayerDTO playerDTO = loader.load(fileHandle);

    Assert.assertEquals(playerDTO.getSpriteSheet(), "player.png");

    Assert.assertEquals(playerDTO.getSize().getHeight(), 2.5f, 0.01f);
    Assert.assertEquals(playerDTO.getSize().getWidth(), 2.7f, 0.01f);
    Assert.assertEquals(playerDTO.getMovement().getMaxVelocity(), 5.5f, 0.01f);
    Assert.assertEquals(playerDTO.getMovement().getSpeed(), 0.7f, 0.01f);
  }
}
