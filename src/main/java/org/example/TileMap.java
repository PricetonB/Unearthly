package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tile map made of named, transparent layers. Tile coordinates are zero-based
 * coordinates in tiles, rather than pixel coordinates in the source image.
 */
public final class TileMap {
    private final int columns;
    private final int rows;
    private final int tileSize;
    private final Map<String, Image> tileSheets = new LinkedHashMap<>();
    private final List<Layer> layers = new ArrayList<>();

    public TileMap(int columns, int rows, int tileSize) {
        if (columns <= 0 || rows <= 0 || tileSize <= 0) {
            throw new IllegalArgumentException("Map dimensions and tile size must be positive");
        }
        this.columns = columns;
        this.rows = rows;
        this.tileSize = tileSize;
    }

    public void addTileSheet(String id, String resourcePath) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Tilesheet id must not be empty");
        }
        if (tileSheets.containsKey(id)) {
            throw new IllegalArgumentException("Tilesheet id is already registered: " + id);
        }

        InputStream stream = TileMap.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Tilesheet resource not found: " + resourcePath);
        }

        Image image;
        try (InputStream imageStream = stream) {
            image = new Image(imageStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not load tilesheet: " + resourcePath, e);
        }
        if (image.isError()) {
            throw new IllegalArgumentException("Could not load tilesheet: " + resourcePath, image.getException());
        }
        if (image.getWidth() < tileSize || image.getHeight() < tileSize) {
            throw new IllegalArgumentException("Tilesheet is smaller than one tile: " + resourcePath);
        }
        tileSheets.put(id, image);
    }

    /**
     * Adds a layer in draw order. Null cells are transparent, revealing lower layers.
     */
    public void addLayer(String name, Tile[][] cells) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Layer name must not be empty");
        }
        if (cells == null || cells.length != rows) {
            throw new IllegalArgumentException("Layer must contain exactly " + rows + " rows");
        }

        Tile[][] copy = new Tile[rows][columns];
        for (int y = 0; y < rows; y++) {
            if (cells[y] == null || cells[y].length != columns) {
                throw new IllegalArgumentException("Layer row " + y + " must contain exactly " + columns + " tiles");
            }
            for (int x = 0; x < columns; x++) {
                Tile tile = cells[y][x];
                if (tile != null) {
                    Image sheet = tileSheets.get(tile.sheetId);
                    if (sheet == null) {
                        throw new IllegalArgumentException("Unknown tilesheet: " + tile.sheetId);
                    }
                    double sourceX = (double) tile.x * tileSize;
                    double sourceY = (double) tile.y * tileSize;
                    if (sourceX + tileSize > sheet.getWidth() || sourceY + tileSize > sheet.getHeight()) {
                        throw new IllegalArgumentException(
                            "Tile (" + tile.x + ", " + tile.y + ") is outside tilesheet: " + tile.sheetId);
                    }
                    copy[y][x] = tile;
                }
            }
        }
        layers.add(new Layer(name, copy));
    }

    public void render(GraphicsContext graphics) {
        // Smoothing blends in neighbouring sheet pixels at tile edges, causing visible seams.
        boolean smoothing = graphics.isImageSmoothing();
        graphics.setImageSmoothing(false);
        for (Layer layer : layers) {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    Tile tile = layer.cells[y][x];
                    if (tile == null) {
                        continue;
                    }
                    graphics.drawImage(
                        tileSheets.get(tile.sheetId),
                        tile.x * tileSize, tile.y * tileSize, tileSize, tileSize,
                        x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }
        graphics.setImageSmoothing(smoothing);
    }

    public static final class Tile {
        private final String sheetId;
        private final int x;
        private final int y;

        public Tile(String sheetId, int x, int y) {
            if (sheetId == null || sheetId.trim().isEmpty()) {
                throw new IllegalArgumentException("Tilesheet id must not be empty");
            }
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Tile coordinates must not be negative");
            }
            this.sheetId = sheetId;
            this.x = x;
            this.y = y;
        }
    }

    private static final class Layer {
        private final String name;
        private final Tile[][] cells;

        private Layer(String name, Tile[][] cells) {
            this.name = name;
            this.cells = cells;
        }
    }
}
