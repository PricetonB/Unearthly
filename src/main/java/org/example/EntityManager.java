package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EntityManager - manages all entities in the game.
 * Handles creation, removal, and querying of entities.
 * Similar to EntityManagerCls in the C++ implementation.
 */
public class EntityManager {
    private final List<Entity> entities;
    private final Map<String, List<Entity>> entityMap;
    private final List<Entity> toAdd;
    private long totalEntities;

    public EntityManager() {
        this.entities = new ArrayList<>();
        this.entityMap = new HashMap<>();
        this.toAdd = new ArrayList<>();
        this.totalEntities = 0;
    }

    /**
     * Update the entity manager - adds pending entities and removes dead entities
     * Should be called at the beginning of each frame
     */
    public void update() {
        // Add pending entities
        for (Entity entity : toAdd) {
            entities.add(entity);
            entityMap.computeIfAbsent(entity.getTag(), k -> new ArrayList<>()).add(entity);
        }
        toAdd.clear();

        // Remove dead entities from the main list
        removeDeadEntities(entities);

        // Remove dead entities from the tag map
        for (List<Entity> entityList : entityMap.values()) {
            removeDeadEntities(entityList);
        }
    }

    /**
     * Remove inactive entities from a list
     */
    private void removeDeadEntities(List<Entity> entityList) {
        entityList.removeIf(entity -> !entity.isActive());
    }

    /**
     * Add a new entity with the given tag
     * The entity will be added to the game on the next update() call
     */
    public Entity addEntity(String tag) {
        Entity entity = new Entity(tag, totalEntities++);
        toAdd.add(entity);
        return entity;
    }

    /**
     * Get all entities
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Get all entities with a specific tag
     */
    public List<Entity> getEntities(String tag) {
        return entityMap.getOrDefault(tag, new ArrayList<>());
    }

    /**
     * Get the entity map (tag -> list of entities)
     */
    public Map<String, List<Entity>> getEntityMap() {
        return entityMap;
    }
}

