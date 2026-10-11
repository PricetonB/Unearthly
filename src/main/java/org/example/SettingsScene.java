package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.Components.*;

/**
 * SettingsScene - allows player to change player color
 */
public class SettingsScene implements GameScene {
    private final EntityManager entityManager;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final double windowWidth;
    private final double windowHeight;
    private SceneType nextScene;
    private double mouseX;
    private double mouseY;
    private Entity colorPreview;
    private Entity colorNameText;

    public SettingsScene(Canvas canvas) {
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
        title.cTransform = new CTransform(windowWidth / 2, 100, 0, 0, 0);
        title.cText = new CText("SETTINGS", 50, Color.WHITE);

        // Create color label
        Entity colorLabel = entityManager.addEntity("Label");
        colorLabel.cTransform = new CTransform(windowWidth / 2, 220, 0, 0, 0);
        colorLabel.cText = new CText("Player Color:", 30, Color.LIGHTGRAY);

        // Create previous color button
        Entity prevButton = entityManager.addEntity("Button");
        prevButton.cTransform = new CTransform(windowWidth / 2 - 150, 320, 0, 0, 0);
        prevButton.cButton = new CButton(windowWidth / 2 - 150, 320, 80, 60, "<", () -> {
            GameSettings.getInstance().previousColor();
            updateColorPreview();
        });
        prevButton.cRectangle = new CRectangle(80, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);

        // Create color preview circle
        colorPreview = entityManager.addEntity("Preview");
        colorPreview.cTransform = new CTransform(windowWidth / 2, 320, 0, 0, 0);
        colorPreview.cShape = new CShape(40, GameSettings.getInstance().getPlayerColor(), Color.WHITE, 3);

        // Create next color button
        Entity nextButton = entityManager.addEntity("Button");
        nextButton.cTransform = new CTransform(windowWidth / 2 + 150, 320, 0, 0, 0);
        nextButton.cButton = new CButton(windowWidth / 2 + 150, 320, 80, 60, ">", () -> {
            GameSettings.getInstance().nextColor();
            updateColorPreview();
        });
        nextButton.cRectangle = new CRectangle(80, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);

        // Create color name text
        colorNameText = entityManager.addEntity("ColorName");
        colorNameText.cTransform = new CTransform(windowWidth / 2, 400, 0, 0, 0);
        colorNameText.cText = new CText(GameSettings.getInstance().getCurrentColorName(), 24, Color.WHITE);

        // Create Back button
        Entity backButton = entityManager.addEntity("Button");
        backButton.cTransform = new CTransform(windowWidth / 2, 520, 0, 0, 0);
        backButton.cButton = new CButton(windowWidth / 2, 520, 250, 60, "BACK", () -> {
            nextScene = SceneType.MENU;
        });
        backButton.cRectangle = new CRectangle(250, 60, Color.rgb(50, 50, 80), Color.WHITE, 3);
    }

    private void updateColorPreview() {
        if (colorPreview != null && colorPreview.cShape != null) {
            colorPreview.cShape.shape.setFill(GameSettings.getInstance().getPlayerColor());
        }
        if (colorNameText != null && colorNameText.cText != null) {
            colorNameText.cText.text = GameSettings.getInstance().getCurrentColorName();
        }
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
            if (entity.cTransform != null && entity.cText != null && entity.cButton == null) {
                gc.setFill(entity.cText.color);
                gc.setFont(new Font("Arial Bold", entity.cText.fontSize));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(entity.cText.text, entity.cTransform.positionX, entity.cTransform.positionY);
            }

            // Draw shape (color preview)
            if (entity.cTransform != null && entity.cShape != null) {
                double x = entity.cTransform.positionX;
                double y = entity.cTransform.positionY;
                double radius = entity.cShape.shape.getRadius();

                gc.setFill((Color) entity.cShape.shape.getFill());
                gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
                gc.setStroke((Color) entity.cShape.shape.getStroke());
                gc.setLineWidth(entity.cShape.shape.getStrokeWidth());
                gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
            }

            // Draw buttons
            if (entity.cTransform != null && entity.cButton != null && entity.cRectangle != null) {
                double x = entity.cTransform.positionX;
                double y = entity.cTransform.positionY;
                double width = entity.cRectangle.rectangle.getWidth();
                double height = entity.cRectangle.rectangle.getHeight();

                if (entity.cButton.isHovered) {
                    gc.setFill(Color.rgb(80, 80, 120));
                } else {
                    gc.setFill((Color) entity.cRectangle.rectangle.getFill());
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
        if (code == KeyCode.ESCAPE) {
            nextScene = SceneType.MENU;
        }
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

