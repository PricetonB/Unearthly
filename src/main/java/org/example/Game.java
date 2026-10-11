package org.example;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Game class - main game controller.
 * Manages the game window, game loop, and scenes.
 * Similar to the Game class in the C++ implementation.
 */
public class Game {
    private final Stage stage;
    private final Canvas canvas;
    private GameScene currentScene;
    private AnimationTimer gameLoop;
    private boolean isRunning;

    // Window configuration
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String WINDOW_TITLE = "JavaFX ECS Game";

    public Game(Stage stage) {
        this.stage = stage;
        this.isRunning = true;
        this.canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        init();
    }

    /**
     * Initialize the game
     */
    private void init() {
        // Create the scene with the canvas
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Start with the menu scene
        currentScene = new MenuScene(canvas);

        // Set up input handling
        scene.setOnKeyPressed(event -> {
            if (currentScene != null) {
                currentScene.onKeyPressed(event.getCode());
            }

            // Handle escape key to close the game (only from play scene)
            if (event.getCode() == KeyCode.ESCAPE && currentScene instanceof PlayScene) {
                changeScene(SceneType.MENU);
            }
        });

        scene.setOnKeyReleased(event -> {
            if (currentScene != null) {
                currentScene.onKeyReleased(event.getCode());
            }
        });

        scene.setOnMouseClicked(event -> {
            if (currentScene != null) {
                currentScene.onMouseClicked(event.getX(), event.getY());
            }
        });

        scene.setOnMouseMoved(event -> {
            if (currentScene != null) {
                currentScene.onMouseMoved(event.getX(), event.getY());
            }
        });

        // Configure the stage
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.setResizable(false);

        // Handle window close
        stage.setOnCloseRequest(event -> stop());

        // Create the game loop using AnimationTimer
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isRunning && currentScene != null) {
                    currentScene.update();

                    // Check for scene transitions
                    SceneType nextScene = currentScene.getNextScene();
                    if (nextScene != null) {
                        currentScene.resetNextScene();
                        changeScene(nextScene);
                    }
                }
            }
        };
    }

    /**
     * Change to a new scene
     */
    private void changeScene(SceneType sceneType) {
        switch (sceneType) {
            case MENU:
                currentScene = new MenuScene(canvas);
                break;
            case SETTINGS:
                currentScene = new SettingsScene(canvas);
                break;
            case DIFFICULTY_SELECT:
                currentScene = new DifficultySelectScene(canvas);
                break;
            case PLAY:
                currentScene = new PlayScene(canvas);
                break;
            case GAME_OVER:
                currentScene = new GameOverScene(canvas);
                break;
        }
    }

    /**
     * Run the game - shows the window and starts the game loop
     */
    public void run() {
        stage.show();
        gameLoop.start();
    }

    /**
     * Stop the game
     */
    public void stop() {
        isRunning = false;
        gameLoop.stop();
        stage.close();
    }

    /**
     * Check if the game is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Get the current scene
     */
    public GameScene getCurrentScene() {
        return currentScene;
    }

    /**
     * Get the window width
     */
    public static int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    /**
     * Get the window height
     */
    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }
}

