package com.bolero.game.characters;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.dialog.DialogTree;
import com.bolero.game.dtos.MovementDTO;
import com.bolero.game.dtos.SizeDTO;
import com.bolero.game.dtos.SpriteSheetDTO;
import com.bolero.game.schedule.ScheduleList;
import java.io.FileNotFoundException;
import lombok.Getter;

public class NPC extends AbstractCharacter {
  @Getter private final Circle talkCircle;
  @Getter private final String name;
  @Getter private final DialogTree dialogTree;
  @Getter private final ScheduleList scheduleList;

  public NPC(
      String name,
      Vector2 position,
      World box2DWorld,
      SizeDTO sizeDTO,
      MovementDTO movementDTO,
      SpriteSheetDTO ssDTO,
      DialogTree dialogTree,
      ScheduleList scheduleList)
      throws FileNotFoundException {
    super(position, box2DWorld, sizeDTO, movementDTO, ssDTO, BodyDef.BodyType.DynamicBody);
    talkCircle = new Circle(position, 4f);
    this.name = name;
    this.dialogTree = dialogTree;
    this.scheduleList = scheduleList;
  }

  @Override
  public void setPosition() {
    talkCircle.setPosition(this.body.getPosition());
    super.setPosition();
  }

  public boolean hasDialog() {
    return !this.dialogTree.isEmpty();
  }
}
