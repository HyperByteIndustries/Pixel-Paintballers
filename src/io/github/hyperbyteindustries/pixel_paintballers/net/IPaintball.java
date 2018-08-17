package io.github.hyperbyteindustries.pixel_paintballers.net;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.HeadsUpDisplay;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Paintball;

/**
 * Represents a paintball shot on a multiplayer server.
 * When constructed, this class is responsible for the management of the paintball in a
 * multiplayer system.
 * @author Ramone Graham
 *
 */
public class IPaintball extends Paintball {

	private Server server;
	
	/**
	 * Creates a new paintball.
	 * @param x - The x coordinate of the paintball.
	 * @param y - The y coordinate of the paintball.
	 * @param id - The ID tag of the paintball.
	 * @param shooter - The game object that shot the paintball.
	 * @param server - An instance of the Server class, used to remove the paintball after a
	 * collision.
	 */
	public IPaintball(float x, float y, ID id, GameObject shooter, Server server) {
		super(x, y, id, null, null, shooter);
		
		this.server = server;
	}
	
	// See tick() in GameObject.
	public void tick() {
		x += velX;
		y += velY;
		
		if (id == ID.PAINTBALL) {
			if (x <= 0 || x >= Game.XBOUND-9) server.removeObject(this);
			if (y <= 0 || y >= Game.YBOUND-9) server.removeObject(this);
		} else if (id == ID.BOUNCYPAINTBALL) {
			if (x <= 0 || x >= Game.XBOUND-9) velX *= -1;
			if (y <= 0 || y >= Game.YBOUND-9) velY *= -1;
		} else if (id == ID.HOMINGPAINTBALL) {
			float diffX = x-(Game.player.getX()+12), diffY = y-(Game.player.getY()+
					12), distance = (float) Math.sqrt((x-Game.player.getX())*(x-
							Game.player.getX()) + (y-Game.player.getY())*(y-
									Game.player.getY()));
			
			velX = (float) (((-1.0/distance)*diffX)*7);
			velY = (float) (((-1.0/distance)*diffY)*7);
		}
		
		collision();
	}

	/**
	 * Checks to see if the paintball has collided with another game object.
	 */
	private void collision() {
		for (int i = 0; i < server.getOnlineObjects().size(); i++) {
			GameObject tempObject = server.getOnlineObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) {
				if (shooter != tempObject) {
					if (getBounds().intersects(tempObject.getBounds())) {
						server.removeObject(this);
					}
				}
			} else if (tempObject.getID() == ID.ENEMY || tempObject.getID() ==
					ID.MOVINGENEMY || tempObject.getID() == ID.BOUNCYENEMY ||
					tempObject.getID() == ID.HOMINGENEMY) {
				if (shooter.getID() == ID.PLAYER) {
					if (getBounds().intersects(tempObject.getBounds())) {
						if (tempObject.getID() == ID.ENEMY)
							HeadsUpDisplay.score += 1;
						else if (tempObject.getID() == ID.MOVINGENEMY)
							HeadsUpDisplay.score += 2;
						else if (tempObject.getID() == ID.BOUNCYENEMY)
							HeadsUpDisplay.score += 3;
						else if (tempObject.getID() == ID.HOMINGENEMY)
							HeadsUpDisplay.score += 4;
						server.removeObject(this);
						server.removeObject(tempObject);
					}
				} else {
					if (shooter.getID() == tempObject.getID()) {
						if (shooter != tempObject) {
							if (getBounds().intersects(tempObject.getBounds())) {
								server.removeObject(this);
							}
						}
					} else {
						if (getBounds().intersects(tempObject.getBounds())) {
							server.removeObject(this);
						}
					}
				}
			} else if (tempObject.getID() == ID.PAINTBALL || tempObject.getID() ==
					ID.BOUNCYPAINTBALL || tempObject.getID() == ID.HOMINGENEMY) {
				if (tempObject != this) {
					if (getBounds().intersects(tempObject.getBounds())) {
						server.removeObject(tempObject);
						server.removeObject(this);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the shooter of this paintball.
	 * @return The game object that shot this paintball.
	 */
	public GameObject getShooter() {
		return shooter;
	}
}
