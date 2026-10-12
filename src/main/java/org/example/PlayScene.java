package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.Components.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * PlayScene - the main gameplay scene with player movement, shooting, enemies, and scoring.
 * Contains all the system functions (sMovement, sRender, sUserInput, sCollision, etc.)
 */
public class PlayScene implements GameScene {
    private final EntityManager entityManager;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final TileMap tileMap;
    private Entity player;
    private final Set<KeyCode> pressedKeys;
    private final Random random;

    // Player configuration
    private static final double PLAYER_SPEED = 5.0;
    private static final double PLAYER_RADIUS = 25.0;
    private static final Color PLAYER_OUTLINE_COLOR = Color.WHITE;
    private static final double PLAYER_OUTLINE_THICKNESS = 3.0;
    private static final int PLAYER_MAX_HEALTH = 3;

    // Bullet configuration
    private static final double BULLET_SPEED = 10.0;
    private static final double BULLET_RADIUS = 5.0;
    private static final int BULLET_LIFESPAN = 120;
    private static final int PLAYER_SHOOT_COOLDOWN = 15;

    // Enemy configuration
    private static final double ENEMY_RADIUS = 20.0;
    private static final int ENEMY_SHOOT_COOLDOWN = 120;
    private static final double ENEMY_BULLET_SPEED = 4.0;

    // Window dimensions
    private final double windowWidth;
    private final double windowHeight;

    private SceneType nextScene;
    private boolean isRunning;
    private long frameCount;
    private int score;
    private int playerShootCooldown;
    private double mouseX;
    private double mouseY;

    // Difficulty settings
    private Difficulty difficulty;

    public PlayScene(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entityManager = new EntityManager();
        this.tileMap = GameMap.create();
        this.pressedKeys = new HashSet<>();
        this.random = new Random();
        this.windowWidth = canvas.getWidth();
        this.windowHeight = canvas.getHeight();
        this.nextScene = null;
        this.isRunning = true;
        this.frameCount = 0;
        this.score = 0;
        this.playerShootCooldown = 0;
        this.difficulty = GameSettings.getInstance().getDifficulty();

        init();
    }

    /**
     * Initialize the scene
     */
    private void init() {
        spawnPlayer();
    }

    /**
     * Spawn the player entity in the center of the screen
     */
    private void spawnPlayer() {
        player = entityManager.addEntity("Player");

        double centerX = windowWidth / 2.0;
        double centerY = windowHeight / 2.0;

        Color playerColor = GameSettings.getInstance().getPlayerColor();

        player.cTransform = new CTransform(centerX, centerY, 0, 0, 0);
        player.cShape = new CShape(PLAYER_RADIUS, playerColor, PLAYER_OUTLINE_COLOR, PLAYER_OUTLINE_THICKNESS);
        player.cCollision = new CCollision(PLAYER_RADIUS);
        player.cInput = new CInput();
        player.cHealth = new CHealth(PLAYER_MAX_HEALTH);
    }

    /**
     * Spawn an enemy at a random edge position
     */
    private void spawnEnemy() {
        // Count current enemies
        long enemyCount = entityManager.getEntities().stream()
            .filter(e -> "Enemy".equals(e.getTag()) && e.isActive())
            .count();

        if (enemyCount >= difficulty.getMaxEnemies()) {
            return;
        }

        Entity enemy = entityManager.addEntity("Enemy");

        // Spawn at random edge
        double x, y;
        int edge = random.nextInt(4);
        switch (edge) {
            case 0: // Top
                x = random.nextDouble() * windowWidth;
                y = -ENEMY_RADIUS;
                break;
            case 1: // Right
                x = windowWidth + ENEMY_RADIUS;
                y = random.nextDouble() * windowHeight;
                break;
            case 2: // Bottom
                x = random.nextDouble() * windowWidth;
                y = windowHeight + ENEMY_RADIUS;
                break;
            default: // Left
                x = -ENEMY_RADIUS;
                y = random.nextDouble() * windowHeight;
                break;
        }

        // Random color for enemy
        Color enemyColor = Color.hsb(random.nextDouble() * 360, 0.7, 0.8);

        enemy.cTransform = new CTransform(x, y, 0, 0, 0);
        enemy.cShape = new CShape(ENEMY_RADIUS, enemyColor, Color.WHITE, 2);
        enemy.cCollision = new CCollision(ENEMY_RADIUS);
        enemy.cEnemy = new CEnemy(ENEMY_SHOOT_COOLDOWN + random.nextInt(60), ENEMY_BULLET_SPEED);
    }

    /**
     * Spawn a bullet from the player toward the mouse position
     */
    private void spawnPlayerBullet() {
        if (playerShootCooldown > 0 || player == null || !player.isActive()) return;

        Entity bullet = entityManager.addEntity("Bullet");

        double dx = mouseX - player.cTransform.positionX;
        double dy = mouseY - player.cTransform.positionY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return;

        double velX = (dx / length) * BULLET_SPEED;
        double velY = (dy / length) * BULLET_SPEED;

        bullet.cTransform = new CTransform(
            player.cTransform.positionX,
            player.cTransform.positionY,
            velX, velY, 0
        );
        bullet.cShape = new CShape(BULLET_RADIUS, Color.YELLOW, Color.WHITE, 1);
        bullet.cCollision = new CCollision(BULLET_RADIUS);
        bullet.cLifeSpan = new CLifeSpan(BULLET_LIFESPAN);
        bullet.cBullet = new CBullet(true);

        playerShootCooldown = PLAYER_SHOOT_COOLDOWN;
    }

    /**
     * Spawn a bullet from an enemy toward the player
     */
    private void spawnEnemyBullet(Entity enemy) {
        if (player == null || !player.isActive()) return;

        Entity bullet = entityManager.addEntity("EnemyBullet");

        double dx = player.cTransform.positionX - enemy.cTransform.positionX;
        double dy = player.cTransform.positionY - enemy.cTransform.positionY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return;

        double velX = (dx / length) * enemy.cEnemy.bulletSpeed;
        double velY = (dy / length) * enemy.cEnemy.bulletSpeed;

        bullet.cTransform = new CTransform(
            enemy.cTransform.positionX,
            enemy.cTransform.positionY,
            velX, velY, 0
        );
        bullet.cShape = new CShape(BULLET_RADIUS, Color.RED, Color.DARKRED, 1);
        bullet.cCollision = new CCollision(BULLET_RADIUS);
        bullet.cLifeSpan = new CLifeSpan(BULLET_LIFESPAN * 2);
        bullet.cBullet = new CBullet(false);
    }

    /**
     * Main update function - called every frame
     */
    @Override
    public void update() {
        if (!isRunning) return;

        frameCount++;
        entityManager.update();

        sUserInput();
        sMovement();
        sEnemyAI();
        sSpawner();
        sCollision();
        sLifeSpan();
        sBoundary();
        sRender();

        // Update cooldowns
        if (playerShootCooldown > 0) playerShootCooldown--;

        // Check game over
        if (player != null && player.cHealth != null && player.cHealth.isDead()) {
            GameSettings.getInstance().setLastScore(score);
            nextScene = SceneType.GAME_OVER;
        }
    }

    /**
     * Enemy AI system - moves enemies toward player and handles shooting
     */
    private void sEnemyAI() {
        if (player == null || !player.isActive()) return;

        for (Entity entity : entityManager.getEntities()) {
            if ("Enemy".equals(entity.getTag()) && entity.cTransform != null && entity.cEnemy != null) {
                // Move toward player
                double dx = player.cTransform.positionX - entity.cTransform.positionX;
                double dy = player.cTransform.positionY - entity.cTransform.positionY;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0) {
                    entity.cTransform.velocityX = (dx / length) * difficulty.getEnemySpeed();
                    entity.cTransform.velocityY = (dy / length) * difficulty.getEnemySpeed();
                }

                // Handle shooting
                entity.cEnemy.updateCooldown();
                if (entity.cEnemy.canShoot()) {
                    spawnEnemyBullet(entity);
                    entity.cEnemy.resetCooldown();
                }
            }
        }
    }

    /**
     * Spawner system - spawns enemies periodically
     */
    private void sSpawner() {
        if (frameCount % difficulty.getSpawnInterval() == 0) {
            spawnEnemy();
        }
    }

    /**
     * Movement system - updates entity positions based on velocity
     */
    private void sMovement() {
        // Update player velocity based on input
        if (player != null && player.cInput != null && player.isActive()) {
            // Reset velocity
            player.cTransform.velocityX = 0;
            player.cTransform.velocityY = 0;

            if (player.cInput.up) {
                player.cTransform.velocityY = -PLAYER_SPEED;
            }
            if (player.cInput.down) {
                player.cTransform.velocityY = PLAYER_SPEED;
            }
            if (player.cInput.left) {
                player.cTransform.velocityX = -PLAYER_SPEED;
            }
            if (player.cInput.right) {
                player.cTransform.velocityX = PLAYER_SPEED;
            }

            // Normalize diagonal movement
            if (player.cTransform.velocityX != 0 && player.cTransform.velocityY != 0) {
                double factor = 1.0 / Math.sqrt(2);
                player.cTransform.velocityX *= factor;
                player.cTransform.velocityY *= factor;
            }
        }

        // Update positions for all entities
        for (Entity entity : entityManager.getEntities()) {
            if (entity.cTransform == null) {
                continue;
            }

            if (entity == player && entity.cCollision != null) {
                // Move each axis separately so the player slides along solid tiles.
                double radius = entity.cCollision.radius;
                double newX = entity.cTransform.positionX + entity.cTransform.velocityX;
                if (!tileMap.collidesCircle(newX, entity.cTransform.positionY, radius)) {
                    entity.cTransform.positionX = newX;
                }
                double newY = entity.cTransform.positionY + entity.cTransform.velocityY;
                if (!tileMap.collidesCircle(entity.cTransform.positionX, newY, radius)) {
                    entity.cTransform.positionY = newY;
                }
                continue;
            }

            entity.cTransform.positionX += entity.cTransform.velocityX;
            entity.cTransform.positionY += entity.cTransform.velocityY;

            if (entity.cBullet != null && entity.cCollision != null
                && tileMap.collidesCircle(entity.cTransform.positionX, entity.cTransform.positionY,
                    entity.cCollision.radius)) {
                entity.destroy();
            }
        }
    }

    /**
     * Collision system - handles collisions between entities
     */
    private void sCollision() {
        for (Entity bullet : entityManager.getEntities()) {
            if (bullet.cBullet == null || !bullet.isActive()) continue;

            if (bullet.cBullet.isPlayerBullet) {
                // Check player bullets hitting enemies
                for (Entity enemy : entityManager.getEntities()) {
                    if (!"Enemy".equals(enemy.getTag()) || !enemy.isActive()) continue;

                    if (checkCollision(bullet, enemy)) {
                        bullet.destroy();
                        enemy.destroy();
                        score += 100;
                        break;
                    }
                }
            } else {
                // Check enemy bullets hitting player
                if (player != null && player.isActive() && checkCollision(bullet, player)) {
                    bullet.destroy();
                    if (player.cHealth != null) {
                        player.cHealth.takeDamage(1);
                    }
                }
            }
        }

        // Check enemies colliding with player
        for (Entity enemy : entityManager.getEntities()) {
            if (!"Enemy".equals(enemy.getTag()) || !enemy.isActive()) continue;

            if (player != null && player.isActive() && checkCollision(enemy, player)) {
                enemy.destroy();
                if (player.cHealth != null) {
                    player.cHealth.takeDamage(1);
                }
            }
        }
    }

    /**
     * Check collision between two entities
     */
    private boolean checkCollision(Entity a, Entity b) {
        if (a.cTransform == null || b.cTransform == null ||
            a.cCollision == null || b.cCollision == null) {
            return false;
        }

        double dx = a.cTransform.positionX - b.cTransform.positionX;
        double dy = a.cTransform.positionY - b.cTransform.positionY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDistance = a.cCollision.radius + b.cCollision.radius;

        return distance < minDistance;
    }

    /**
     * LifeSpan system - handles entities with limited lifetime
     */
    private void sLifeSpan() {
        for (Entity entity : entityManager.getEntities()) {
            if (entity.cLifeSpan != null) {
                entity.cLifeSpan.remainingLife--;
                if (entity.cLifeSpan.remainingLife <= 0) {
                    entity.destroy();
                }
            }
        }
    }

    /**
     * Boundary system - keeps player within the window, removes off-screen bullets
     */
    private void sBoundary() {
        // Keep player in bounds
        if (player != null && player.cTransform != null && player.cCollision != null) {
            double radius = player.cCollision.radius;

            if (player.cTransform.positionX - radius < 0) {
                player.cTransform.positionX = radius;
            }
            if (player.cTransform.positionX + radius > windowWidth) {
                player.cTransform.positionX = windowWidth - radius;
            }
            if (player.cTransform.positionY - radius < 0) {
                player.cTransform.positionY = radius;
            }
            if (player.cTransform.positionY + radius > windowHeight) {
                player.cTransform.positionY = windowHeight - radius;
            }
        }

        // Remove off-screen bullets
        for (Entity entity : entityManager.getEntities()) {
            if (entity.cBullet != null && entity.cTransform != null) {
                double margin = 50;
                if (entity.cTransform.positionX < -margin ||
                    entity.cTransform.positionX > windowWidth + margin ||
                    entity.cTransform.positionY < -margin ||
                    entity.cTransform.positionY > windowHeight + margin) {
                    entity.destroy();
                }
            }
        }
    }

    /**
     * Render system - draws all entities to the canvas
     */
    private void sRender() {
        // Clear the canvas before drawing the layered tile map.
        gc.setFill(Color.rgb(15, 15, 25));
        gc.fillRect(0, 0, windowWidth, windowHeight);
        tileMap.render(gc);

        // Draw all entities
        for (Entity entity : entityManager.getEntities()) {
            if (entity.cTransform != null && entity.cShape != null) {
                double x = entity.cTransform.positionX;
                double y = entity.cTransform.positionY;
                double radius = entity.cShape.shape.getRadius();

                // Draw the shape (circle)
                gc.setFill((Color) entity.cShape.shape.getFill());
                gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

                // Draw the outline
                gc.setStroke((Color) entity.cShape.shape.getStroke());
                gc.setLineWidth(entity.cShape.shape.getStrokeWidth());
                gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
            }
        }

        // Draw UI
        drawUI();
    }

    /**
     * Draw the UI elements (score, health)
     */
    private void drawUI() {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(new Font("Arial Bold", 24));

        // Draw score
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 20, 40);

        // Draw health
        gc.fillText("Health: ", 20, 80);
        if (player != null && player.cHealth != null) {
            for (int i = 0; i < player.cHealth.maxHealth; i++) {
                if (i < player.cHealth.currentHealth) {
                    gc.setFill(Color.RED);
                } else {
                    gc.setFill(Color.DARKGRAY);
                }
                gc.fillOval(120 + i * 30, 60, 20, 20);
            }
        }

        // Draw difficulty
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(new Font("Arial", 16));
        gc.fillText("Difficulty: " + difficulty.name(), 20, 110);

        // Draw instructions
        gc.setFill(Color.rgb(150, 150, 150));
        gc.setFont(new Font("Arial", 14));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("WASD to move, Click to shoot, ESC to quit", windowWidth - 20, 30);
    }

    /**
     * User input system - processes input state from pressed keys
     */
    private void sUserInput() {
        if (player != null && player.cInput != null) {
            player.cInput.up = pressedKeys.contains(KeyCode.W);
            player.cInput.down = pressedKeys.contains(KeyCode.S);
            player.cInput.left = pressedKeys.contains(KeyCode.A);
            player.cInput.right = pressedKeys.contains(KeyCode.D);
        }
    }

    @Override
    public void onKeyPressed(KeyCode code) {
        pressedKeys.add(code);

        if (code == KeyCode.P) {
            isRunning = !isRunning;
        }
    }

    @Override
    public void onKeyReleased(KeyCode code) {
        pressedKeys.remove(code);
    }

    @Override
    public void onMouseClicked(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
        spawnPlayerBullet();
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

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Entity getPlayer() {
        return player;
    }
}
