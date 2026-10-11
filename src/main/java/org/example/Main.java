package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the JavaFX ECS Game.
 * Launches the JavaFX application and starts the game.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Game game = new Game(primaryStage);
        game.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}