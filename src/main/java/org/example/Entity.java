package org.example;

import org.example.Components.*;

/**
 * Entity class - represents a game object with components.
 * Similar to EntityCls in the C++ implementation.
 */
public class Entity {
    private final String tag;
    private final long id;
    private boolean isActive;

    // Components - each entity can have any combination of components
    public CTransform cTransform;
    public CShape cShape;
    public CRectangle cRectangle;
    public CCollision cCollision;
    public CInput cInput;
    public CLifeSpan cLifeSpan;
    public CScore cScore;
    public CButton cButton;
    public CText cText;
    public CHealth cHealth;
    public CEnemy cEnemy;
    public CBullet cBullet;

    /**
     * Package-private constructor - entities should be created through EntityManager
     */
    Entity(String tag, long id) {
        this.tag = tag;
        this.id = id;
        this.isActive = true;
    }

    /**
     * Mark this entity for removal
     */
    public void destroy() {
        this.isActive = false;
    }

    /**
     * Get the entity's tag (type identifier)
     */
    public String getTag() {
        return tag;
    }

    /**
     * Check if the entity is still active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Get the entity's unique ID
     */
    public long getId() {
        return id;
    }
}

