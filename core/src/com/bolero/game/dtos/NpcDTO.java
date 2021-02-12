package com.bolero.game.dtos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.characters.NPC;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.dialog.DialogTree;
import com.bolero.game.exceptions.FileFormatException;
import com.bolero.game.managers.BundleManager;
import com.bolero.game.schedule.Schedule;
import com.bolero.game.schedule.ScheduleList;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NpcDTO {
  private String name;
  private String spawn;
  private String spriteSheet;
  private List<ScheduleDTO> schedules = new ArrayList<>();
  private List<DialogDTO> dialogs = new ArrayList<>();
  private SizeDTO size;
  private MovementDTO movement;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSpawn() {
    return spawn;
  }

  public void setSpawn(String spawn) {
    this.spawn = spawn;
  }

  public String getSpriteSheet() {
    return spriteSheet;
  }

  public void setSpriteSheet(String spriteSheet) {
    this.spriteSheet = spriteSheet;
  }

  public List<DialogDTO> getDialogs() {
    return dialogs;
  }

  public void setDialogs(List<DialogDTO> dialogs) {
    this.dialogs = dialogs;
  }

  public List<ScheduleDTO> getSchedules() {
    return schedules;
  }

  public void setSchedules(List<ScheduleDTO> schedules) {
    this.schedules = schedules;
  }

  public SizeDTO getSize() {
    return size;
  }

  public void setSize(SizeDTO size) {
    this.size = size;
  }

  public MovementDTO getMovement() {
    return movement;
  }

  public void setMovement(MovementDTO movement) {
    this.movement = movement;
  }

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

      // TODO: Raise exception if schedule does not exist
      Vector2 position = scheduleMap.get(scheduleDTO.getNode());
      Schedule schedule = scheduleDTO.toSchedule(position);
      scheduleList.add(schedule);
    }

    return new ScheduleList(scheduleList);
  }

  public NPC toNPC(
      Vector2 spawnPos, Map<String, Vector2> scheduleMap, World world, BundleManager bundleManager)
      throws FileNotFoundException, FileFormatException {
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
