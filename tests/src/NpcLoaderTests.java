import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.NPCLoader;
import com.bolero.game.dtos.ChoiceDTO;
import com.bolero.game.dtos.DialogDTO;
import com.bolero.game.dtos.NpcDTO;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.dtos.ScheduleDTO;
import com.bolero.game.exceptions.FileFormatException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class NpcLoaderTests {
  FileHandle fileHandle;

  @Before
  public void setUp() {
    fileHandle = Gdx.files.local("assets/npcs-test.yaml");
  }

  @Test
  public void canLoadNpcs() throws FileFormatException {
    NPCLoader loader = new NPCLoader();
    NpcsDTO npcsDTO = loader.load(fileHandle);

    assertEquals(npcsDTO.getNpcs().size(), 2);

    NpcDTO wizard = npcsDTO.getNpcs().get(0);
    NpcDTO villager = npcsDTO.getNpcs().get(1);

    assertEquals(wizard.getName(), "Wizard");
    assertEquals(villager.getName(), "Villager");

    assertEquals(wizard.getSpawn(), "wizard1");
    assertEquals(villager.getSpawn(), "villager1");

    assertEquals(wizard.getSpriteSheet(), "npc.png");
    assertEquals(villager.getSpriteSheet(), "npc.png");

    assertEquals(wizard.getSize().getHeight(), 2.5f, 0.01f);
    assertEquals(wizard.getSize().getWidth(), 2.7f, 0.01f);
    assertEquals(wizard.getMovement().getMaxVelocity(), 5f, 0.01f);
    assertEquals(wizard.getMovement().getSpeed(), 0.5f, 0.01f);

    assertEquals(villager.getSize().getHeight(), 3.2f, 0.01f);
    assertEquals(villager.getSize().getWidth(), 3f, 0.01f);
    assertEquals(villager.getMovement().getMaxVelocity(), 7f, 0.01f);
    assertEquals(villager.getMovement().getSpeed(), 1f, 0.01f);

    List<DialogDTO> wizardDialog = wizard.getDialogs();

    assertEquals(wizardDialog.size(), 3);
    assertEquals(villager.getDialogs().size(), 0);

    DialogDTO firstDialog = wizardDialog.get(0);
    assertEquals(firstDialog.getTextID(), "dialog3");
    assertEquals(firstDialog.getChoices().size(), 1);

    ChoiceDTO firstDialogChoice = firstDialog.getChoices().get(0);
    assertEquals(firstDialogChoice.getTextID(), "choice3");
    assertNull(firstDialogChoice.getNext());

    DialogDTO secondDialog = wizardDialog.get(1);
    assertEquals(secondDialog.getTextID(), "dialog2");
    assertEquals(secondDialog.getChoices().size(), 1);

    ChoiceDTO secondDialogChoice = secondDialog.getChoices().get(0);
    assertEquals(secondDialogChoice.getTextID(), "choice3");
    assertNull(secondDialogChoice.getNext());

    DialogDTO thirdDialog = wizardDialog.get(2);
    assertEquals(thirdDialog.getTextID(), "dialog1");
    assertEquals(thirdDialog.getChoices().size(), 2);

    ChoiceDTO thirdDialogFirstChoice = thirdDialog.getChoices().get(0);
    assertEquals(thirdDialogFirstChoice.getTextID(), "choice1");
    assertEquals(thirdDialogFirstChoice.getNext(), firstDialog);

    ChoiceDTO thirdDialogSecondChoice = thirdDialog.getChoices().get(1);
    assertEquals(thirdDialogSecondChoice.getTextID(), "choice2");
    assertEquals(thirdDialogSecondChoice.getNext(), secondDialog);

    assertEquals(wizard.getSchedules().size(), 0);

    assertEquals(villager.getSchedules().size(), 1);

    ScheduleDTO villagerSchedule = villager.getSchedules().get(0);

    assertEquals(villagerSchedule.getHour(), 0);
    assertEquals(villagerSchedule.getMinute(), 30);

    assertEquals(villagerSchedule.getNodes().size(), 2);
    assertEquals(villagerSchedule.getNodes().get(0).getId(), "villager1s");
    assertEquals(villagerSchedule.getNodes().get(1).getId(), "villager2s");
  }
}
