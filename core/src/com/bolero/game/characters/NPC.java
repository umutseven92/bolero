package com.bolero.game.characters;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.data.SpriteSheetValues;
import com.bolero.game.dialog.Choice;
import com.bolero.game.dialog.Dialog;
import com.bolero.game.dialog.DialogTree;
import com.bolero.game.enums.CharacterState;

public class NPC extends Character {
    public final Circle talkCircle;

    private final String name;
    private final DialogTree dialogTree;

    public NPC(String name, Vector2 position, World box2DWorld, CharacterValues characterValues, String texturePath) {
        super(position, box2DWorld, characterValues, texturePath, new SpriteSheetValues(10, 10, 5, 7), BodyDef.BodyType.StaticBody);
        super.setState(CharacterState.idle);
        talkCircle = new Circle(position, 4f);
        this.name = name;
        this.dialogTree = loadDialogTree();
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

    private DialogTree loadDialogTree() {
        DialogTree dialogTree = new DialogTree();
        Dialog dialog = new Dialog("Whats up?");
        Dialog dialog2 = new Dialog("Thats good to hear. Im also doing ok.");
        Dialog dialog3 = new Dialog("Im sorry to hear that.");

        Choice choice1 = new Choice("Im good, whats up with you?", dialog2);
        Choice choice2 = new Choice("Pretty shit.", dialog3);
        Choice choice3 = new Choice("Bye.");

        dialog.addChoice(choice1);
        dialog.addChoice(choice2);

        dialog2.addChoice(choice3);
        dialog3.addChoice(choice3);

        dialogTree.addDialog(dialog);
        dialogTree.addDialog(dialog2);
        dialogTree.addDialog(dialog3);

        return dialogTree;
    }

}
