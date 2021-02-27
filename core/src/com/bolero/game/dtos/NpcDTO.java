package com.bolero.game.dtos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.characters.NPC;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.dialog.DialogTree;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.schedule.Schedule;
import com.bolero.game.schedule.ScheduleList;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class NpcDTO {
  private String name;
  private String spawn;
  private List<ScheduleDTO> schedules = new ArrayList<>();
  private List<DialogDTO> dialogs = new ArrayList<>();
  private SpriteSheetDTO spriteSheet;
  private SizeDTO size;
  private MovementDTO movement;

  private DialogTree getDialogTree(BundleManager bundleManager) {

    List<Dialog> dialogList = new ArrayList<>();
    for (DialogDTO dialogDTO : dialogs) {
      Dialog dialog = dialogDTO.toDialog(bundleManager);
      dialogList.add(dialog);
    }

    return new DialogTree(dialogList);
  }

  private ScheduleList getScheduleList(Map<String, Vector2> scheduleMap) {

    List<Schedule> scheduleList = new ArrayList<>();
    for (ScheduleDTO scheduleDTO : schedules) {
      List<Vector2> positions = new ArrayList<>();

      // TODO: Raise exception if schedule does not exist
      for (NodeDTO node : scheduleDTO.getNodes()) {
        Vector2 position = scheduleMap.get(node.getId());
        positions.add(position);
      }

      Schedule schedule = scheduleDTO.toSchedule(positions);
      scheduleList.add(schedule);
    }

    return new ScheduleList(scheduleList);
  }

  public NPC toNPC(
      Vector2 spawnPos, Map<String, Vector2> scheduleMap, World world, BundleManager bundleManager)
      throws FileNotFoundException {
    DialogTree dialogTree = getDialogTree(bundleManager);
    ScheduleList scheduleList = getScheduleList(scheduleMap);

    return new NPC(
        getName(),
        spawnPos,
        world,
        getSize(),
        getMovement(),
        getSpriteSheet(),
        dialogTree,
        scheduleList);
  }
}
