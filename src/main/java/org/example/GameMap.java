package org.example;

/**
 * Editable starter map. Each layer is a 2D grid indexed as [row][column].
 * Tile coordinates refer to 32x32 cells in the selected tilesheet.
 */
public final class GameMap {
    private static final int TILE_SIZE = 32;
    private static final int COLUMNS = (Game.getWindowWidth() + TILE_SIZE - 1) / TILE_SIZE;
    private static final int ROWS = (Game.getWindowHeight() + TILE_SIZE - 1) / TILE_SIZE;

    private GameMap() {
    }

    public static TileMap create() {
        TileMap map = new TileMap(COLUMNS, ROWS, TILE_SIZE);
        map.addTileSheet("terrain", "/tiles/terrainTiles.png");

        // Edit this grid to change the ground; coordinates are [row][column].
        TileMap.Tile grass = new TileMap.Tile("terrain", 1, 11);
        TileMap.Tile[][] ground = filledLayer(grass);
        // Example: ground[5][6] = new TileMap.Tile("terrain", 13, 8);
        map.addLayer("ground", ground);

        // Put tiles in this grid to draw them over the ground. Null cells are transparent.
        // Example: details[5][6] = new TileMap.Tile("terrain", 3, 6);
        TileMap.Tile[][] details = new TileMap.Tile[ROWS][COLUMNS];
        TileMap.Tile lava = new TileMap.Tile("terrain", 17, 5);
        details[3][3] = lava;
        details[3][4] = lava;
        details[3][5] = lava;
        details[3][6] = lava;
        details[3][7] = lava;
        map.addLayer("details", details);

        // Solid tile types block movement wherever they are placed, on any layer.
        map.setSolid("terrain", 17, 5);

        return map;
    }

    private static TileMap.Tile[][] filledLayer(TileMap.Tile tile) {
        TileMap.Tile[][] layer = new TileMap.Tile[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                layer[row][column] = tile;
            }
        }
        return layer;
    }
}
