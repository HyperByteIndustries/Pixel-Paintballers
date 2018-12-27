package io.github.hyperbyteindustries.pixel_paintballers.net;

import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Paintball;

/**
 * Represents a paintball in the game's multiplayer system.
 * When constructed, this class is responsible for the online management of the paintball.
 * @author Ramone Graham
 *
 */
public class IPaintball extends Paintball {

	/**
	 * Creates a new paintball.
	 * @param x - The X coordinate of the paintball.
	 * @param y - The Y coordinate of the paintball.
	 * @param id - The identification tag of the object.
	 * @param handler - An instance of the Handler class, used to manage collisions with other
	 * objects.
	 * @param shooter - The game object that shot the paintball.
	 */
	public IPaintball(float x, float y, ID id, Handler handler, GameObject shooter) {
		super(x, y, id, null, handler, shooter);
	}
}
