package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.InteractionRectangle;
import com.bolero.game.Player;

import java.util.ArrayList;

public class DebugDrawer implements Disposable {
    private final ShapeRenderer debugRenderer;
    private final Box2DDebugRenderer box2DDebugRenderer;

    private final Camera camera;
    private final float unit;

    public DebugDrawer(float unit, Camera camera) {
        this.unit = unit;
        this.camera = camera;

        debugRenderer = new ShapeRenderer();
        box2DDebugRenderer = new Box2DDebugRenderer();
    }

    public void drawDebugInfo(BitmapFont font, SpriteBatch batch, Player player, String mapName) {
        float cameraRight = Gdx.graphics.getWidth() - unit * 15;
        float cameraLeft = unit * 2;
        float cameraY = Gdx.graphics.getHeight() - unit * 2;
        float camera3Y = Gdx.graphics.getHeight() - unit * 3;

        font.draw(batch, "Map: " + mapName, cameraLeft, cameraY);
        font.draw(batch, "Player Pos: " + player.getPosition().x + ", " + player.getPosition().y, cameraRight, cameraY);
        font.draw(batch, "Camera Pos: " + camera.position.x + ", " + camera.position.y, cameraRight, camera3Y);
    }


    public void drawInteractionZones(ArrayList<InteractionRectangle> interactions) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        debugRenderer.setProjectionMatrix(camera.combined);

        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(1, 0, 0, 0.5f);

        for (InteractionRectangle intRectangle : interactions) {
            Rectangle rectangle = intRectangle.getRectangle();
            debugRenderer.rect(rectangle.x / unit, rectangle.y / unit, rectangle.width / unit, rectangle.height / unit);

        }
        debugRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawBox2DBodies(World world) {
        box2DDebugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        box2DDebugRenderer.dispose();
    }
}
