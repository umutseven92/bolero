package com.bolero.game.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.characters.NPC;
import com.bolero.game.data.CharacterValues;
import com.bolero.game.enums.SpawnType;
import com.bolero.game.exceptions.MissingPropertyException;
import com.bolero.game.exceptions.MissingSpawnTypeException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class NPCController implements Disposable {
    private final TiledMap map;

    private final ArrayList<NPC> npcs;
    private final BundleController bundleController;

    public NPCController(TiledMap map, BundleController bundleController) {
        this.map = map;
        this.bundleController = bundleController;
        npcs = new ArrayList<>();
    }

    public void spawnNPCs(float unit, World world) throws MissingSpawnTypeException, FileNotFoundException, MissingPropertyException {
        MapObjects spawnObjects = map.getLayers().get(BoleroGame.SPAWN_LAYER).getObjects();
        for (MapObject spawn : spawnObjects) {
            MapProperties props = spawn.getProperties();
            String type = props.get("type", String.class);

            if (type == null) {
                throw new MissingSpawnTypeException();
            }

            if (SpawnType.valueOf(type) == SpawnType.npc) {
                String name = props.get("name", String.class);

                if (name == null) {
                    throw new MissingPropertyException("name");
                }

                Vector2 spawnPosition = new Vector2((float) props.get("x") / unit, (float) props.get("y") / unit);
                NPC npc = new NPC(name, spawnPosition, world, new CharacterValues(2.7f, 2.5f, 5f, 0.5f), "npc.png", bundleController, unit);
                npcs.add(npc);
            }
        }
    }

    public void setPositions() {
        for (NPC npc : npcs) {
            npc.setPosition();
        }
    }

    public ArrayList<NPC> getNpcs() {
        return npcs;
    }

    public void drawNPCs(SpriteBatch batch) {
        for (NPC npc : npcs) {
            npc.draw(batch);
        }
    }

    public NPC checkIfNearNPC(Vector2 playerPos) {
        for (NPC npc : this.npcs) {

            if (npc.getTalkCircle().contains(playerPos)) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public void dispose() {
        for (NPC npc : npcs) {
            npc.dispose();
        }
    }
}
