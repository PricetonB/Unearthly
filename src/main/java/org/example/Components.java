package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Components for the Entity Component System.
 * Each component holds data for a specific aspect of an entity.
 */
public class Components {

    /**
     * Transform component - holds position, velocity, and angle
     */
    public static class CTransform {
        public double positionX;
        public double positionY;
        public double velocityX;
        public double velocityY;
        public double angle;

        public CTransform(double posX, double posY, double velX, double velY, double angle) {
            this.positionX = posX;
            this.positionY = posY;
            this.velocityX = velX;
            this.velocityY = velY;
            this.angle = angle;
        }
    }

    /**
     * Shape component - holds a circle shape for rendering
     */
    public static class CShape {
        public Circle shape;

        public CShape(double radius, Color fillColor, Color outlineColor, double thickness) {
            this.shape = new Circle(radius);
            this.shape.setFill(fillColor);
            this.shape.setStroke(outlineColor);
            this.shape.setStrokeWidth(thickness);
        }
    }

    /**
     * Rectangle component - holds a rectangle shape for rendering
     */
    public static class CRectangle {
        public Rectangle rectangle;

        public CRectangle(double width, double height, Color fillColor, Color outlineColor, double thickness) {
            this.rectangle = new Rectangle(width, height);
            this.rectangle.setFill(fillColor);
            this.rectangle.setStroke(outlineColor);
            this.rectangle.setStrokeWidth(thickness);
        }
    }

    /**
     * Collision component - holds collision radius
     */
    public static class CCollision {
        public double radius;

        public CCollision(double radius) {
            this.radius = radius;
        }
    }

    /**
     * Input component - holds input state for player control
     */
    public static class CInput {
        public boolean up;
        public boolean down;
        public boolean left;
        public boolean right;
        public double mouseX;
        public double mouseY;

        public CInput() {
            this.up = false;
            this.down = false;
            this.left = false;
            this.right = false;
            this.mouseX = 0;
            this.mouseY = 0;
        }
    }

    /**
     * LifeSpan component - for entities with limited lifetime
     */
    public static class CLifeSpan {
        public int totalLife;
        public int remainingLife;

        public CLifeSpan(int life) {
            this.totalLife = life;
            this.remainingLife = life;
        }
    }

    /**
     * Score component - holds score value
     */
    public static class CScore {
        public int score;

        public CScore(int score) {
            this.score = score;
        }
    }

    /**
     * Button component - for clickable UI elements
     */
    public static class CButton {
        public double left;
        public double right;
        public double top;
        public double bottom;
        public String text;
        public boolean isHovered;
        public Runnable onClick;

        public CButton(double x, double y, double width, double height, String text, Runnable onClick) {
            this.left = x - width / 2;
            this.right = x + width / 2;
            this.top = y - height / 2;
            this.bottom = y + height / 2;
            this.text = text;
            this.isHovered = false;
            this.onClick = onClick;
        }

        public boolean containsPoint(double x, double y) {
            return x >= left && x <= right && y >= top && y <= bottom;
        }
    }

    /**
     * Text component - for displaying text on screen
     */
    public static class CText {
        public String text;
        public double fontSize;
        public javafx.scene.paint.Color color;

        public CText(String text, double fontSize, javafx.scene.paint.Color color) {
            this.text = text;
            this.fontSize = fontSize;
            this.color = color;
        }
    }

    /**
     * Health component - for entities with health points
     */
    public static class CHealth {
        public int maxHealth;
        public int currentHealth;

        public CHealth(int maxHealth) {
            this.maxHealth = maxHealth;
            this.currentHealth = maxHealth;
        }

        public boolean isDead() {
            return currentHealth <= 0;
        }

        public void takeDamage(int damage) {
            currentHealth = Math.max(0, currentHealth - damage);
        }
    }

    /**
     * Enemy component - marks an entity as an enemy with shooting capability
     */
    public static class CEnemy {
        public int shootCooldown;
        public int currentCooldown;
        public double bulletSpeed;

        public CEnemy(int shootCooldown, double bulletSpeed) {
            this.shootCooldown = shootCooldown;
            this.currentCooldown = 0;
            this.bulletSpeed = bulletSpeed;
        }

        public boolean canShoot() {
            return currentCooldown <= 0;
        }

        public void resetCooldown() {
            currentCooldown = shootCooldown;
        }

        public void updateCooldown() {
            if (currentCooldown > 0) {
                currentCooldown--;
            }
        }
    }

    /**
     * Bullet component - marks an entity as a bullet with owner info
     */
    public static class CBullet {
        public boolean isPlayerBullet;

        public CBullet(boolean isPlayerBullet) {
            this.isPlayerBullet = isPlayerBullet;
        }
    }
}

