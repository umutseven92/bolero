import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.NPCLoader;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.exceptions.FileFormatException;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class NpcDTOTests {
  NpcsDTO npcsDTO;

  @Before
  public void setUp() throws FileFormatException {
    FileHandle fileHandle = Gdx.files.local("assets/npcs-test.yaml");
    NPCLoader loader = new NPCLoader();
    npcsDTO = loader.load(fileHandle);
  }

  // TODO: Write DTO tests
  //  @Test
  //  public void canNPCDtosConvertToNPCs() throws FileFormatException, FileNotFoundException {
  //    NpcDTO npcDTO = npcsDTO.getNpcs().get(0);
  //
  //    Vector2 spawnPos = new Vector2(0, 0);
  //    CharacterValues charVal = new CharacterValues(0, 0 , 0 , 0);
  //    NPC npc = new NPC("wizard", spawnPos, )
  //  }
}
