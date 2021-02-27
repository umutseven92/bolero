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
import com.bolero.game.BoleroGame;
import com.bolero.game.Clock;
import com.bolero.game.PathGraph;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.data.PathNode;
import com.bolero.game.interactions.AbstractRectangle;
import java.util.List;
import lombok.val;

public class DebugDrawer implements Disposable {
  private final ShapeRenderer debugRenderer;
  private final Box2DDebugRenderer box2DDebugRenderer;

  private final Camera camera;

  public DebugDrawer(Camera camera) {
    this.camera = camera;

    debugRenderer = new ShapeRenderer();
    box2DDebugRenderer = new Box2DDebugRenderer();
  }

  public void drawDebugInfo(
      BitmapFont font,
      SpriteBatch batch,
      Player player,
      String mapName,
      float zoomLevel,
      Clock clock) {
    float cameraRight = Gdx.graphics.getWidth() - BoleroGame.UNIT * 15;
    float cameraLeft = BoleroGame.UNIT * 2;
    float cameraY = Gdx.graphics.getHeight() - BoleroGame.UNIT * 2;
    float camera3Y = Gdx.graphics.getHeight() - BoleroGame.UNIT * 3;
    float camera4Y = Gdx.graphics.getHeight() - BoleroGame.UNIT * 4;
    float camera5Y = Gdx.graphics.getHeight() - BoleroGame.UNIT * 5;

    font.draw(batch, "Map: " + mapName, cameraLeft, cameraY);
    font.draw(batch, "Player State: " + player.getState(), cameraLeft, camera3Y);
    font.draw(
        batch,
        String.format(
            "Time: %02d:%02d, %s",
            clock.getCurrentHour(), clock.getCurrentMinute(), clock.getCurrentDay()),
        cameraLeft,
        camera4Y);
    font.draw(
        batch,
        "Player Pos: " + player.getPosition().x + ", " + player.getPosition().y,
        cameraRight,
        cameraY);
    font.draw(
        batch,
        "Camera Pos: " + camera.position.x + ", " + camera.position.y,
        cameraRight,
        camera3Y);
    font.draw(batch, "Zoom Level: " + zoomLevel, cameraRight, camera4Y);
    font.draw(batch, "O to zoom out, P to zoom in", cameraRight, camera5Y);
  }

  public void drawDebugShapes(
      List<AbstractRectangle> interactions, List<NPC> npcs, PathGraph nodes) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    debugRenderer.setProjectionMatrix(camera.combined);

    debugRenderer.begin(ShapeRenderer.ShapeType.Filled);

    drawInteractionZones(interactions);
    drawTalkCircles(npcs);
    drawPathVertices(nodes);

    debugRenderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  private void drawInteractionZones(List<AbstractRectangle> interactions) {
    debugRenderer.setColor(1, 0, 0, 0.5f);

    for (AbstractRectangle intRectangle : interactions) {
      Rectangle rectangle = intRectangle.getRectangle();
      debugRenderer.rect(
          rectangle.x / BoleroGame.UNIT,
          rectangle.y / BoleroGame.UNIT,
          rectangle.width / BoleroGame.UNIT,
          rectangle.height / BoleroGame.UNIT);
    }
  }

  private void drawTalkCircles(List<NPC> npcs) {
    debugRenderer.setColor(0, 0, 1, 0.5f);

    for (NPC npc : npcs) {
      val talkCircle = npc.getTalkCircle();
      debugRenderer.circle(talkCircle.x, talkCircle.y, talkCircle.radius);
    }
  }

  private void drawPathVertices(PathGraph nodes) {
    debugRenderer.setColor(0, 1, 0, 0.5f);

    for (PathNode node : nodes.getNodes()) {
      debugRenderer.circle(node.getX(), node.getY(), 1);
    }
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
