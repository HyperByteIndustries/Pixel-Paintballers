package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents the "skeleton" of all game objects in the game.
 * When a game object class is created (E.g. Player), it will extend this class,
 * inheriting it's methods and variables. 
 * @author Ramone Graham
 *
 */
public abstract class GameObject {

	protected float x, y, velX, velY;
	protected ID id;
	
	/**
	 * Creates a new game object.
	 * @param x - The x coordinate of the object.
	 * @param y - The y coordinate of the object.
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
	 * @param graphics2d - The graphics used to update the visuals.
	 */
	public abstract void render(Graphics2D graphics2d);
	
	/**
	 * Sets the x coordinate to the given value.
	 * @param x - The value to be set.
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Sets the y coordinate to the given value.
	 * @param y - The value to be set.
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Sets the velocity across the x axis to the given value.
	 * @param velX - The value the velocity is to be set to.
	 */
	public void setVelX(float velX) {
		this.velX = velX;
	}
	
	/**
	 * Sets the velocity across the y axis to the given value.
	 * @param velY - The value the velocity is to be set to.
	 */
	public void setVelY(float velY) {
		this.velY = velY;
	}
	
	/**
	 * Sets the ID tag to the given value.
	 * @param id - The ID to be set.
	 */
	public void setID(ID id) {
		this.id = id;
	}
	
	/**
	 * Gets the current x coordinate of the game object.
	 * @return The current x coordinate of the object.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Gets the current y coordinate of the game object.
	 * @return The current y coordinate of the object.
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Gets the current velocity across the x axis.
	 * @return The current velocity.
	 */
	public float getVelX() {
		return velX;
	}
	
	/**
	 * Gets the current velocity across the y axis.
	 * @return The current velocity.
	 */
	public float getVelY() {
		return velY;
	}
	
	/**
	 * Gets the ID tag of the game object.
	 * @return The ID of the object.
	 */
	public ID getID() {
		return id;
	}
}
