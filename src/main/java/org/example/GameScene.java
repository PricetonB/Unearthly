package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

/**
 * Base interface for all game scenes
 */
public interface GameScene {
    /**
     * Update the scene - called every frame
     */
    void update();

    /**
     * Handle key press event
     */
    void onKeyPressed(KeyCode code);

    /**
     * Handle key release event
     */
    void onKeyReleased(KeyCode code);

    /**
     * Handle mouse click event
     */
    void onMouseClicked(double x, double y);

    /**
     * Handle mouse move event
     */
    void onMouseMoved(double x, double y);

    /**
     * Get the next scene to transition to (null if no transition)
     */
    SceneType getNextScene();

    /**
     * Reset the next scene request
     */
    void resetNextScene();
}

