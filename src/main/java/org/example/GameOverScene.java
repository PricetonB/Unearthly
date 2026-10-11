package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.Components.*;

/**
 * GameOverScene - displayed when player loses, shows score and options to retry or return to menu
 */
public class GameOverScene implements GameScene {
    private final EntityManager entityManager;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final double windowWidth;
    private final double windowHeight;
    private SceneType nextScene;
    private double mouseX;
    private double mouseY;

    public GameOverScene(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entityManager = new EntityManager();
        this.windowWidth = canvas.getWidth();
        this.windowHeight = canvas.getHeight();
        this.nextScene = null;

        init();
    }

    private void init() {
        // Create Game Over text
        Entity gameOverText = entityManager.addEntity("Title");
        gameOverText.cTransform = new CTransform(windowWidth / 2, 180, 0, 0, 0);
        gameOverText.cText = new CText("GAME OVER", 80, Color.RED);

        // Create score text
        Entity scoreText = entityManager.addEntity("Score");
        scoreText.cTransform = new CTransform(windowWidth / 2, 280, 0, 0, 0);
        scoreText.cText = new CText("Score: " + GameSettings.getInstance().getLastScore(), 40, Color.WHITE);

        // Create Try Again button
        Entity tryAgainButton = entityManager.addEntity("Button");
        tryAgainButton.cTransform = new CTransform(windowWidth / 2, 420, 0, 0, 0);
        tryAgainButton.cButton = new CButton(windowWidth / 2, 420, 280, 60, "TRY AGAIN", () -> {
            nextScene = SceneType.PLAY;
        });
        tryAgainButton.cRectangle = new CRectangle(280, 60, Color.rgb(50, 80, 50), Color.LIGHTGREEN, 3);

        // Create Main Menu button
        Entity mainMenuButton = entityManager.addEntity("Button");
        mainMenuButton.cTransform = new CTransform(windowWidth / 2, 520, 0, 0, 0);
        mainMenuButton.cButton = new CButton(windowWidth / 2, 520, 280, 60, "MAIN MENU", () -> {
            nextScene = SceneType.MENU;
        });
        mainMenuButton.cRectangle = new CRectangle(280, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);
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
        // Clear background with dark red tint
        gc.setFill(Color.rgb(30, 15, 15));
        gc.fillRect(0, 0, windowWidth, windowHeight);

        // Draw all entities
        for (Entity entity : entityManager.getEntities()) {
            // Draw text
            if (entity.cTransform != null && entity.cText != null && entity.cButton == null) {
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

                Color baseColor = (Color) entity.cRectangle.rectangle.getFill();
                if (entity.cButton.isHovered) {
                    gc.setFill(baseColor.brighter());
                } else {
                    gc.setFill(baseColor);
                }
                gc.fillRect(x - width / 2, y - height / 2, width, height);

                gc.setStroke((Color) entity.cRectangle.rectangle.getStroke());
                gc.setLineWidth(entity.cRectangle.rectangle.getStrokeWidth());
                gc.strokeRect(x - width / 2, y - height / 2, width, height);

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial Bold", 24));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(entity.cButton.text, x, y + 8);
            }
        }
    }

    @Override
    public void onKeyPressed(KeyCode code) {
    }

    @Override
    public void onKeyReleased(KeyCode code) {
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

