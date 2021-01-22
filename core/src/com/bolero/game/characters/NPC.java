package com.bolero.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.controllers.BundleController;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.dialog.DialogLoader;
import com.bolero.game.dialog.DialogTree;
import com.bolero.game.enums.CharacterState;
import com.bolero.game.exceptions.FileFormatException;

import java.io.FileNotFoundException;

public class NPC extends Character {
  public final Circle talkCircle;
  private final String name;
  private final DialogTree dialogTree;
  private final BundleController bundleController;
  private final DialogLoader dialogLoader;

  public NPC(
      String name,
      String scriptName,
      Vector2 position,
      World box2DWorld,
      CharacterValues characterValues,
      String texturePath,
      BundleController bundleController,
      DialogLoader dialogLoader)
      throws FileNotFoundException, FileFormatException {
    super(
        position,
        box2DWorld,
        characterValues,
        texturePath,
        new SpriteSheetValues(10, 10, 5, 7),
        BodyDef.BodyType.DynamicBody);
    this.bundleController = bundleController;
    super.setState(CharacterState.idle);
    talkCircle = new Circle(position, 4f);
    this.name = name;
    this.dialogLoader = dialogLoader;
    this.dialogTree = loadDialogTree(scriptName);
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

  private DialogTree loadDialogTree(String scriptName)
      throws FileNotFoundException, FileFormatException {
    String fileName = String.format("dialog/%s", scriptName);
    FileHandle file = Gdx.files.internal(fileName);

    if (!file.exists()) {
      throw new FileNotFoundException(String.format("%s does not exist.", fileName));
    }

    DialogTree dialogTree = dialogLoader.load(file, bundleController);

    return dialogTree;
  }
}
