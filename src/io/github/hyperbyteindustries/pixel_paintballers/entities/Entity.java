package io.github.hyperbyteindustries.pixel_paintballers.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents the "skeleton" of all entities in the game.
 * When an entity class is created (E.g. {@link Trail}), this abstract class is responsible for
 * providing methods and variables that help to manage the entity.
 * @author Ramone Graham
 *
 */
public abstract class Entity {

	/**
	 * Represents an entity's identification tag in the game.
	 * When a entity class is created, (E.g. {@link Player}), this enumeration is responsible for
	 * providing an ID tag to the entity in order for it to carry out specific instructions.
	 * @author Ramone Graham
	 *
	 */
	public enum ID {

		PLAYER(), IPLAYER(), ENEMY(), MOVINGENEMY(), BOUNCYENEMY(), HOMINGENEMY(), IENEMY(),
		IMOVINGENEMY(), IBOUNCYENEMY(), IHOMINGENEMY(), PAINTBALL(), BOUNCYPAINTBALL(),
		HOMINGPAINTBALL(), TRAIL();
	}

	protected ID id;
	protected float x, y;
	
	/**
	 * Creates a new entity.
	 * @param id - The identification tag of the entity.
	 * @param x - The X coordinate of the entity.
	 * @param y - The Y coordinate of the entity.
	 */
	public Entity(ID id, float x, float y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Updates the logic of the game entity.
	 */
	public abstract void tick();
	
	/**
	 * Updates the visuals of the entity.
	 * @param graphics2d - The graphics context used to update the visuals of the entity.
	 */
	public abstract void render(Graphics2D graphics2D);
	
	/**
	 * Gets the boundaries of the entity.
	 * @return The boundaries of the entity.
	 */
	public abstract Rectangle getBounds();

	/**
	 * Sets the identification tag of the entity.
	 * @param id - The ID to set.
	 */
	public void setID(ID id) {
		this.id = id;
	}

	/**
	 * Sets the X coordinate of the entity.
	 * @param x - The X coordinate to set.
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Sets the Y coordinate of the entity.
	 * @param y - The Y coordinate to set.
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Gets the identification tag of the entity.
	 * @return The identification tag of the entity.
	 */
	public ID getID() {
		return id;
	}

	/**
	 * Gets the X coordinate of the entity.
	 * @return The X coordinate of the entity.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate of the entity.
	 * @return The Y coordinate of the entity.
	 */
	public float getY() {
		return y;
	}
}
