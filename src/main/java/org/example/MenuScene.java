package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.Components.*;

/**
 * MenuScene - the main menu scene with Play and Settings buttons
 */
public class MenuScene implements GameScene {
    private final EntityManager entityManager;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final double windowWidth;
    private final double windowHeight;
    private SceneType nextScene;
    private double mouseX;
    private double mouseY;

    public MenuScene(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entityManager = new EntityManager();
        this.windowWidth = canvas.getWidth();
        this.windowHeight = canvas.getHeight();
        this.nextScene = null;

        init();
    }

    private void init() {
        // Create title text entity
        Entity title = entityManager.addEntity("Title");
        title.cTransform = new CTransform(windowWidth / 2, 150, 0, 0, 0);
        title.cText = new CText("ECS SPACE GAME", 60, Color.WHITE);

        // Create Play button
        Entity playButton = entityManager.addEntity("Button");
        playButton.cTransform = new CTransform(windowWidth / 2, 350, 0, 0, 0);
        playButton.cButton = new CButton(windowWidth / 2, 350, 250, 60, "PLAY", () -> {
            nextScene = SceneType.DIFFICULTY_SELECT;
        });
        playButton.cRectangle = new CRectangle(250, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);

        // Create Settings button
        Entity settingsButton = entityManager.addEntity("Button");
        settingsButton.cTransform = new CTransform(windowWidth / 2, 450, 0, 0, 0);
        settingsButton.cButton = new CButton(windowWidth / 2, 450, 250, 60, "SETTINGS", () -> {
            nextScene = SceneType.SETTINGS;
        });
        settingsButton.cRectangle = new CRectangle(250, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);

        // Create Quit button
        Entity quitButton = entityManager.addEntity("Button");
        quitButton.cTransform = new CTransform(windowWidth / 2, 550, 0, 0, 0);
        quitButton.cButton = new CButton(windowWidth / 2, 550, 250, 60, "QUIT", () -> {
            System.exit(0);
        });
        quitButton.cRectangle = new CRectangle(250, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);
    }

    @Override
    public void update() {
        entityManager.update();
        sButtonHover();
        sRender();
    }

    private void sButtonHover() {
        for (Entity entity : entityManager.getEntities()) {
            if (entity.cButton != null) {
                entity.cButton.isHovered = entity.cButton.containsPoint(mouseX, mouseY);
            }
        }
    }

    private void sRender() {
        // Clear background
        gc.setFill(Color.rgb(20, 20, 35));
        gc.fillRect(0, 0, windowWidth, windowHeight);

        // Draw all entities
        for (Entity entity : entityManager.getEntities()) {
            // Draw text
            if (entity.cTransform != null && entity.cText != null) {
                gc.setFill(entity.cText.color);
                gc.setFont(new Font("Arial Bold", entity.cText.fontSize));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(entity.cText.text, entity.cTransform.positionX, entity.cTransform.positionY);
            }

            // Draw buttons
            if (entity.cTransform != null && entity.cButton != null && entity.cRectangle != null) {
                double x = entity.cTransform.positionX;
                double y = entity.cTransform.positionY;
                double width = entity.cRectangle.rectangle.getWidth();
                double height = entity.cRectangle.rectangle.getHeight();

                // Draw button background
                if (entity.cButton.isHovered) {
                    gc.setFill(Color.rgb(80, 80, 120));
                } else {
                    gc.setFill((Color) entity.cRectangle.rectangle.getFill());
                }
                gc.fillRect(x - width / 2, y - height / 2, width, height);

                // Draw button outline
                gc.setStroke((Color) entity.cRectangle.rectangle.getStroke());
                gc.setLineWidth(entity.cRectangle.rectangle.getStrokeWidth());
                gc.strokeRect(x - width / 2, y - height / 2, width, height);

                // Draw button text
                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial Bold", 24));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(entity.cButton.text, x, y + 8);
            }
        }

        // Draw decorative elements
        gc.setFill(Color.rgb(100, 100, 150, 0.3));
        for (int i = 0; i < 50; i++) {
            double starX = (i * 73) % windowWidth;
            double starY = (i * 97) % windowHeight;
            gc.fillOval(starX, starY, 3, 3);
        }
    }

    @Override
    public void onKeyPressed(KeyCode code) {
        // No key handling needed for menu
    }

    @Override
    public void onKeyReleased(KeyCode code) {
        // No key handling needed for menu
    }

    @Override
    public void onMouseClicked(double x, double y) {
        for (Entity entity : entityManager.getEntities()) {
            if (entity.cButton != null && entity.cButton.containsPoint(x, y)) {
                if (entity.cButton.onClick != null) {
                    entity.cButton.onClick.run();
                }
            }
        }
    }

    @Override
    public void onMouseMoved(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    @Override
    public SceneType getNextScene() {
        return nextScene;
    }

    @Override
    public void resetNextScene() {
        nextScene = null;
    }
}

