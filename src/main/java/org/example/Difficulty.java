package org.example;

/**
 * Enum representing game difficulty levels
 */
public enum Difficulty {
    EASY(3, 120, 2.0),      // 3 enemies, spawn every 120 frames, slow speed
    MEDIUM(5, 90, 3.0),     // 5 enemies, spawn every 90 frames, medium speed
    HARD(8, 60, 4.0);       // 8 enemies, spawn every 60 frames, fast speed

    private final int maxEnemies;
    private final int spawnInterval;
    private final double enemySpeed;

    Difficulty(int maxEnemies, int spawnInterval, double enemySpeed) {
        this.maxEnemies = maxEnemies;
        this.spawnInterval = spawnInterval;
        this.enemySpeed = enemySpeed;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public double getEnemySpeed() {
        return enemySpeed;
    }
}

