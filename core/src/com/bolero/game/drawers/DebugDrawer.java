package com.bolero.game.drawers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.bolero.game.BoleroGame;
import com.bolero.game.Clock;
import com.bolero.game.characters.NPC;
import com.bolero.game.characters.Player;
import com.bolero.game.data.PathNode;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.interactions.AbstractRectangle;
import com.bolero.game.pathfinding.PathGraph;
import java.util.List;
import lombok.val;

public class DebugDrawer extends AbstractDrawer implements Disposable {
  private final ShapeRenderer debugRenderer;
  private final Box2DDebugRenderer box2DDebugRenderer;

  private Camera camera;

  private final Label mapLabel;
  private final Label playerPosLabel;
  private final Label playerStateLabel;
  private final Label clockLabel;
  private final Label zoomLevelLabel;

  public DebugDrawer(Camera camera) {
    super();
    this.camera = camera;

    debugRenderer = new ShapeRenderer();
    box2DDebugRenderer = new Box2DDebugRenderer();

    mapLabel = new Label("", uiSkin);
    playerPosLabel = new Label("", uiSkin);
    playerStateLabel = new Label("", uiSkin);
    clockLabel = new Label("", uiSkin);
    zoomLevelLabel = new Label("", uiSkin);
  }

  public void init(Camera camera) {
    super.initTable();

    table.top();

    table.pad(20);
    table.add(mapLabel).expandX().left();
    table.add(playerPosLabel).expandX().right();
    table.row();
    table.add(clockLabel).expandX().left();
    table.add(playerStateLabel).expandX().right();
    table.row();
    table.add(zoomLevelLabel).expandX().left();

    this.camera = camera;
  }

  public void drawDebugInfo(Player player, String mapName, float zoomLevel, Clock clock) {
    mapLabel.setText(String.format("Map: %s", mapName));
    playerPosLabel.setText(
        String.format("Player Pos: %f, %f", player.getPosition().x, player.getPosition().y));
    playerStateLabel.setText(String.format("Player State: %s", player.getState().toString()));
    clockLabel.setText(
        String.format(
            "Time: %02d:%02d, %s",
            clock.getCurrentHour(), clock.getCurrentMinute(), clock.getCurrentDay()));
    zoomLevelLabel.setText(String.format("Zoom Level: %s", zoomLevel));

    super.draw();
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
    super.dispose();
    debugRenderer.dispose();
    box2DDebugRenderer.dispose();
  }
}
