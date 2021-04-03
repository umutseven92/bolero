package com.bolero.game.mappers;

import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.bolero.game.BoleroGame;
import com.bolero.game.data.PathNode;
import com.bolero.game.exceptions.ConfigurationNotLoadedException;
import com.bolero.game.pathfinding.PathGraph;
import java.io.FileNotFoundException;
import lombok.val;

public class PathMapper extends AbstractMapper implements Mapper<PathGraph> {
  TiledMap map;

  public PathMapper(TiledMap map) {
    super(map);
    this.map = map;
  }

  @Override
  public PathGraph map() throws FileNotFoundException, ConfigurationNotLoadedException {
    val layer =
        (TiledMapTileLayer)
            map.getLayers().get(BoleroGame.config.getConfig().getMaps().getLayers().getPath());

    if (layer == null) {
      // There is no path layer in the map.
      return new PathGraph(new Array<>());
    }

    final int numRows = layer.getWidth();
    final int numCols = layer.getHeight();

    final PathNode[][] nodes = new PathNode[numCols][numRows];
    final Array<PathNode> indexedNodes = new Array<>(numCols * numRows);

    int index = 0;
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        val cell = layer.getCell(i, j);
        if (cell != null) {
          nodes[i][j] =
              new PathNode(
                  index,
                  i * (layer.getTileWidth() / BoleroGame.UNIT)
                      + (layer.getTileWidth() / (BoleroGame.UNIT * 2)),
                  j * (layer.getTileHeight() / BoleroGame.UNIT)
                      + (layer.getTileHeight() / (BoleroGame.UNIT * 2)));
          indexedNodes.add(nodes[i][j]);
          index++;
        }
      }
    }

    for (int x = 0; x < numRows; x++) {
      for (int y = 0; y < numCols; y++) {
        if (layer.getCell(x, y) == null) {
          continue;
        }

        if (x - 1 >= 0 && layer.getCell(x - 1, y) != null) {
          nodes[x][y].getConnections().add(new DefaultConnection<>(nodes[x][y], nodes[x - 1][y]));
        }

        if (x + 1 < numCols && layer.getCell(x + 1, y) != null) {
          nodes[x][y].getConnections().add(new DefaultConnection<>(nodes[x][y], nodes[x + 1][y]));
        }

        if (y - 1 >= 0 && layer.getCell(x, y - 1) != null) {
          nodes[x][y].getConnections().add(new DefaultConnection<>(nodes[x][y], nodes[x][y - 1]));
        }

        if (y + 1 < numRows && layer.getCell(x, y + 1) != null) {
          nodes[x][y].getConnections().add(new DefaultConnection<>(nodes[x][y], nodes[x][y + 1]));
        }
      }
    }

    return new PathGraph(indexedNodes);
  }
}
