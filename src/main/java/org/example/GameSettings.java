package org.example;

import javafx.scene.paint.Color;

/**
 * GameSettings - stores game settings that persist across scenes
 */
public class GameSettings {
    private static GameSettings instance;

    private Color playerColor;
    private Difficulty difficulty;
    private int lastScore;

    // Available player colors
    public static final Color[] AVAILABLE_COLORS = {
        Color.DODGERBLUE,
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.PURPLE,
        Color.ORANGE,
        Color.CYAN,
        Color.MAGENTA
    };

    public static final String[] COLOR_NAMES = {
        "Blue",
        "Red",
        "Green",
        "Yellow",
        "Purple",
        "Orange",
        "Cyan",
        "Magenta"
    };

    private int currentColorIndex;

    private GameSettings() {
        this.playerColor = Color.DODGERBLUE;
        this.difficulty = Difficulty.EASY;
        this.lastScore = 0;
        this.currentColorIndex = 0;
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
    }

    public void nextColor() {
        currentColorIndex = (currentColorIndex + 1) % AVAILABLE_COLORS.length;
        playerColor = AVAILABLE_COLORS[currentColorIndex];
    }

    public void previousColor() {
        currentColorIndex = (currentColorIndex - 1 + AVAILABLE_COLORS.length) % AVAILABLE_COLORS.length;
        playerColor = AVAILABLE_COLORS[currentColorIndex];
    }

    public String getCurrentColorName() {
        return COLOR_NAMES[currentColorIndex];
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }
}

