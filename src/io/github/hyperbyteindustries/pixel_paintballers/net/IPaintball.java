package io.github.hyperbyteindustries.pixel_paintballers.net;

import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Paintball;

/**
 * Represents a paintball in the game's multiplayer system.
 * When constructed, this class is responsible for the online management of the paintball.
 * @author Ramone Graham
 *
 */
public class IPaintball extends Paintball {

	/**
	 * Creates a new paintball.
	 * @param id - The identification tag of the paintball.
	 * @param x - The X coordinate of the paintball.
	 * @param y - The Y coordinate of the paintball.
	 * @param handler - An instance of the Handler class, used to manage collisions with other
	 * entities, and to remove the paintball after said collision.
	 * @param shooter - The game object that shot the paintball.
	 */
	public IPaintball(ID id, float x, float y, Handler handler, Entity shooter) {
		super(id, x, y, null, handler, shooter);
	}
}
