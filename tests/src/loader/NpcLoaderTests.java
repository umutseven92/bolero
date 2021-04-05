package loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bolero.game.dtos.ChoiceDTO;
import com.bolero.game.dtos.DialogDTO;
import com.bolero.game.dtos.NpcDTO;
import com.bolero.game.dtos.NpcsDTO;
import com.bolero.game.dtos.ScheduleDTO;
import com.bolero.game.loaders.NPCLoader;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import src.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class NpcLoaderTests {
  FileHandle fileHandle;

  @Before
  public void setUp() {
    fileHandle = Gdx.files.local("assets/npcs-test.yaml");
  }

  @Test
  public void canLoadNpcs() throws FileNotFoundException {
    NPCLoader loader = new NPCLoader();
    NpcsDTO npcsDTO = loader.load(fileHandle);

    Assert.assertEquals(npcsDTO.getNpcs().size(), 2);

    NpcDTO wizard = npcsDTO.getNpcs().get(0);
    NpcDTO villager = npcsDTO.getNpcs().get(1);

    Assert.assertEquals(wizard.getName(), "Wizard");
    Assert.assertEquals(villager.getName(), "Villager");

    Assert.assertEquals(wizard.getSpawn(), "wizard1");
    Assert.assertEquals(villager.getSpawn(), "villager1");

    Assert.assertEquals(wizard.getSpriteSheet().getPath(), "images/wizard.png");
    Assert.assertEquals(villager.getSpriteSheet().getPath(), "images/villager.png");

    Assert.assertEquals(wizard.getSize().getHeight(), 2.5f, 0.01f);
    Assert.assertEquals(wizard.getSize().getWidth(), 2.7f, 0.01f);
    Assert.assertEquals(wizard.getMovement().getMaxVelocity(), 5f, 0.01f);
    Assert.assertEquals(wizard.getMovement().getSpeed(), 0.5f, 0.01f);

    Assert.assertEquals(villager.getSize().getHeight(), 3.2f, 0.01f);
    Assert.assertEquals(villager.getSize().getWidth(), 3f, 0.01f);
    Assert.assertEquals(villager.getMovement().getMaxVelocity(), 7f, 0.01f);
    Assert.assertEquals(villager.getMovement().getSpeed(), 1f, 0.01f);

    List<DialogDTO> wizardDialog = wizard.getDialogs();

    Assert.assertEquals(wizardDialog.size(), 3);
    Assert.assertEquals(villager.getDialogs().size(), 0);

    DialogDTO firstDialog = wizardDialog.get(0);
    Assert.assertEquals(firstDialog.getTextID(), "dialog3");
    Assert.assertEquals(firstDialog.getChoices().size(), 1);

    ChoiceDTO firstDialogChoice = firstDialog.getChoices().get(0);
    Assert.assertEquals(firstDialogChoice.getTextID(), "choice3");
    Assert.assertNull(firstDialogChoice.getNext());

    DialogDTO secondDialog = wizardDialog.get(1);
    Assert.assertEquals(secondDialog.getTextID(), "dialog2");
    Assert.assertEquals(secondDialog.getChoices().size(), 1);

    ChoiceDTO secondDialogChoice = secondDialog.getChoices().get(0);
    Assert.assertEquals(secondDialogChoice.getTextID(), "choice3");
    Assert.assertNull(secondDialogChoice.getNext());

    DialogDTO thirdDialog = wizardDialog.get(2);
    Assert.assertEquals(thirdDialog.getTextID(), "dialog1");
    Assert.assertEquals(thirdDialog.getChoices().size(), 2);

    ChoiceDTO thirdDialogFirstChoice = thirdDialog.getChoices().get(0);
    Assert.assertEquals(thirdDialogFirstChoice.getTextID(), "choice1");
    Assert.assertEquals(thirdDialogFirstChoice.getNext(), firstDialog);

    ChoiceDTO thirdDialogSecondChoice = thirdDialog.getChoices().get(1);
    Assert.assertEquals(thirdDialogSecondChoice.getTextID(), "choice2");
    Assert.assertEquals(thirdDialogSecondChoice.getNext(), secondDialog);

    Assert.assertEquals(wizard.getSchedules().size(), 0);

    Assert.assertEquals(villager.getSchedules().size(), 1);

    ScheduleDTO villagerSchedule = villager.getSchedules().get(0);

    Assert.assertEquals(villagerSchedule.getTime().getHour(), 0);
    Assert.assertEquals(villagerSchedule.getTime().getMinute(), 30);

    Assert.assertEquals(villagerSchedule.getNodes().size(), 2);
    Assert.assertEquals(villagerSchedule.getNodes().get(0).getId(), "villager1s");
    Assert.assertEquals(villagerSchedule.getNodes().get(1).getId(), "villager2s");
  }
}
