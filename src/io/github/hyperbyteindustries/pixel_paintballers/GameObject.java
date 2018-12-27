package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents the "skeleton" of all game objects in the game.
 * When a game object class is created (E.g. Player), it will extend this class, inheriting its
 * methods and variables. 
 * @author Ramone Graham
 *
 */
public abstract class GameObject {

	protected float x, y, velX, velY;
	protected ID id;
	
	/**
	 * Creates a new game object.
	 * @param x - The X coordinate of the object.
	 * @param y - The Y coordinate of the object.
	 * @param id - The identification tag of the object.
	 */
	public GameObject(float x, float y, ID id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	/**
	 * Gets the boundaries of the game object.
	 * @return The boundaries of the object.
	 */
	public abstract Rectangle getBounds();
	
	/**
	 * Updates the logic of the game object.
	 */
	public abstract void tick();
	
	/**
	 * Updates the visuals of the game object.
	 * @param graphics2d - The graphics used to update the visuals of the object.
	 */
	public abstract void render(Graphics2D graphics2D);
	
	/**
	 * Sets the X coordinate to the given value.
	 * @param x - The X coordinate of the new location.
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Sets the Y coordinate to the given value.
	 * @param y - The Y coordinate of the new location.
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Sets the X axis velocity to the given value.
	 * @param velX - The new velocity across the X axis.
	 */
	public void setVelX(float velX) {
		this.velX = velX;
	}
	
	/**
	 * Sets the Y axis velocity to the given value.
	 * @param velY - The new velocity across the Y axis.
	 */
	public void setVelY(float velY) {
		this.velY = velY;
	}
	
	/**
	 * Sets the ID tag to the given value.
	 * @param id - The new ID tag.
	 */
	public void setID(ID id) {
		this.id = id;
	}
	
	/**
	 * Returns the X coordinate of the game object.
	 * @return The X coordinate of the object.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Returns the Y coordinate of the game object.
	 * @return The Y coordinate of the object.
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Returns the X axis velocity of the game object.
	 * @return The velocity of across the X axis.
	 */
	public float getVelX() {
		return velX;
	}
	
	/**
	 * Returns the Y axis velocity of the game object.
	 * @return The velocity of across the Y axis.
	 */
	public float getVelY() {
		return velY;
	}
	
	/**
	 * Returns the ID tag of the game object.
	 * @return The ID tag of the object.
	 */
	public ID getID() {
		return id;
	}
}
