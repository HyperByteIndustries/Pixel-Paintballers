package io.github.hyperbyteindustries.pixel_paintballers.entities;

/**
 * Represents the "skeleton" of all mobile entities in the game.
 * When an mob class is created (E.g. {@link Player}), this abstract class is responsible for
 * providing methods and variables beyond the {@link Entity} that help to manage the mob.
 * @author Ramone Graham
 *
 */
public abstract class Mob extends Entity {

	protected float speed, velX = 0, velY = 0;
	
	/**
	 * Creates a new mob.
	 * @param id - The identification tag of the mob.
	 * @param x - The X coordinate of the mob.
	 * @param y - The Y coordinate of the mob.
	 * @param speed - The movement speed of the mob.
	 */
	public Mob(ID id, float x, float y, float speed) {
		super(id, x, y);
		
		this.speed = speed;
	}

	/**
	 * Sets the movement speed of the mob.
	 * @param speed - The speed to set.
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Sets the X axis velocity of the mob.
	 * @param velX - The X axis velocity to set.
	 */
	public void setVelX(float velX) {
		this.velX = velX;
	}

	/**
	 * Sets the Y axis velocity of the mob.
	 * @param velY - The Y axis velocity to set.
	 */
	public void setVelY(float velY) {
		this.velY = velY;
	}

	/**
	 * Gets the movement speed of the mob.
	 * @return The movement speed of the mob.
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Gets the X axis velocity of the mob.
	 * @return The X axis velocity of the mob.
	 */
	public float getVelX() {
		return velX;
	}

	/**
	 * Gets the Y axis velocity of the mob.
	 * @return The Y axis velocity of the mob.
	 */
	public float getVelY() {
		return velY;
	}
}
