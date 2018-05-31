package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Represents the main weapon of the game.
 * When constructed, this class is responsible for the management of the paintball.
 * @author Ramone Graham
 *
 */
public class Paintball extends GameObject {

	private Handler handler;
	private GameObject shooter;
	
	private Random random;
	private Color colour;
	
	/**
	 * Creates a new paintball.
	 * @param x - The x coordinate of the paintball.
	 * @param y - The y coordinate of the paintball.
	 * @param id - The ID tag of the paintball.
	 * @param handler - An instance of the Handler class, used to remove the
	 * paintball after a collision.
	 * @param shooter - The game object that shot the paintball.
	 */
	public Paintball(float x, float y, ID id, Handler handler, GameObject shooter) {
		super(x, y, id);
		
		this.handler = handler;
		this.shooter = shooter;
		random = new Random();
		colour = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 8, 8);
	}

	// See tick() in GameObject.
	public void tick() {
		x += velX;
		y += velY;
		
		if (id == ID.PAINTBALL) {
			if (x <= 0 || x >= Game.XBOUND-9) handler.removeObject(this);
			if (y <= 0 || y >= Game.YBOUND-9) handler.removeObject(this);
		} else if (id == ID.BOUNCYPAINTBALL) {
			if (x <= 0 || x >= Game.XBOUND-9) velX *= -1;
			if (y <= 0 || y >= Game.YBOUND-9) velY *= -1;
		} else if (id == ID.HOMINGPAINTBALL) {
			float diffX = x-(Game.player.getX()+12), diffY = y-(Game.player.getY()+12),
					distance = (float) Math.sqrt((x-Game.player.getX())*(x-Game.player.getX()) +
							(y-Game.player.getY())*(y-Game.player.getY()));
			
			velX = (float) (((-1.0/distance)*diffX)*5);
			velY = (float) (((-1.0/distance)*diffY)*5);
		}
		
		handler.addObject(new Trail(x, y, ID.TRAIL, handler, colour, getBounds().width,
				getBounds().height, 0.075f));
		
		collision();
	}

	/**
	 * Checks to see if the paintball has collided with another game object.
	 */
	private void collision() {
		for (int i = 0; i < handler.objects.size(); i++) {
			GameObject tempObject = handler.objects.get(i);
			
			if (tempObject.getID() == ID.PLAYER) {
				if (shooter.getID() != ID.PLAYER) {
					if (getBounds().intersects(tempObject.getBounds())) {
						HeadsUpDisplay.health -= 1;
						handler.removeObject(this);
					}
				}
			} else if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
					tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
					ID.HOMINGENEMY) {
				if (shooter.getID() == ID.PLAYER) {
					if (getBounds().intersects(tempObject.getBounds())) {
						if (tempObject.getID() == ID.ENEMY) HeadsUpDisplay.score += 1;
						else if (tempObject.getID() == ID.MOVINGENEMY)
							HeadsUpDisplay.score += 2;
						else if (tempObject.getID() == ID.BOUNCYENEMY)
							HeadsUpDisplay.score += 3;
						else if (tempObject.getID() == ID.HOMINGENEMY)
							HeadsUpDisplay.score += 4;
						handler.removeObject(this);
						handler.removeObject(tempObject);
						HeadsUpDisplay.kills++;
					}
				} else {
					if (shooter.getID() == tempObject.getID()) {
						if (shooter != tempObject) {
							if (getBounds().intersects(tempObject.getBounds())) {
								handler.removeObject(this);
							}
						}
					} else {
						if (getBounds().intersects(tempObject.getBounds())) {
							handler.removeObject(this);
						}
					}
				}
			} else if (tempObject.getID() == ID.PAINTBALL || tempObject.getID() ==
					ID.BOUNCYPAINTBALL || tempObject.getID() == ID.HOMINGENEMY) {
				if (tempObject != this) {
					if (getBounds().intersects(tempObject.getBounds())) {
						handler.removeObject(tempObject);
						handler.removeObject(this);
					}
				}
			}
		}
	}

	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		graphics2d.setColor(colour);
		graphics2d.fill(getBounds());
	}
}
