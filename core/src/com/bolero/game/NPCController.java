package com.bolero.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.enums.SpawnType;

import java.util.ArrayList;

public class NPCController implements Disposable {
    private final TiledMap map;

    private final ArrayList<NPC> npcs;

    public NPCController(TiledMap map) {
        this.map = map;
        npcs = new ArrayList<>();
    }

    public void spawnNPCs(String spawnLayer, float unit, World world) {
        MapObjects spawnObjects = map.getLayers().get(spawnLayer).getObjects();
        for (MapObject spawn : spawnObjects) {
            MapProperties props = spawn.getProperties();
            String type = (String) props.get("type");
            if (SpawnType.valueOf(type) == SpawnType.npc) {
                Vector2 spawnPosition = new Vector2((float) props.get("x") / unit, (float) props.get("y") / unit);
                NPC npc = new NPC(spawnPosition, world, 2.5f, 2.3f, 5f, 0.5f, "npc.png");
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

    @Override
    public void dispose() {
        for (NPC npc : npcs) {
            npc.dispose();
        }
    }
}
