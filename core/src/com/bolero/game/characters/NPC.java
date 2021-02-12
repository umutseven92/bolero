package com.bolero.game.characters;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.dialog.DialogTree;
import com.bolero.game.dtos.MovementDTO;
import com.bolero.game.dtos.SizeDTO;
import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.schedule.ScheduleList;
import java.io.FileNotFoundException;

public class NPC extends AbstractCharacter {
  public final Circle talkCircle;
  private final String name;
  private final DialogTree dialogTree;
  private final ScheduleList scheduleList;

  public NPC(
      String name,
      Vector2 position,
      World box2DWorld,
      SizeDTO sizeDTO,
      MovementDTO movementDTO,
      String texturePath,
      DialogTree dialogTree,
      ScheduleList scheduleList)
      throws FileNotFoundException, FileFormatException {
    super(
        position,
        box2DWorld,
        sizeDTO,
        movementDTO,
        texturePath,
        new SpriteSheetValues(10, 10, 5, 7),
        BodyDef.BodyType.DynamicBody);
    talkCircle = new Circle(position, 4f);
    this.name = name;
    this.dialogTree = dialogTree;
    this.scheduleList = scheduleList;
  }

  public Circle getTalkCircle() {
    return talkCircle;
  }

  @Override
  public void setPosition() {
    talkCircle.setPosition(this.body.getPosition());
    super.setPosition();
  }

  public String getName() {
    return name;
  }

  public DialogTree getDialogTree() {
    return dialogTree;
  }

  public ScheduleList getScheduleList() {
    return scheduleList;
  }

  public boolean hasDialog() {
    return !this.dialogTree.isEmpty();
  }
}
