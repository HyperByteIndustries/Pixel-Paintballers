package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.net.IEnemy;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet04Damage;

/**
 * Represents the main weapon of the game.
 * When constructed, this class is responsible for the management of the paintball.
 * @author Ramone Graham
 *
 */
public class Paintball extends GameObject {

	private Game game;
	private Handler handler;
	
	private GameObject shooter;
	
	private Color colour;
	
	/**
	 * Creates a new paintball.
	 * @param x - The x coordinate of the paintball.
	 * @param y - The y coordinate of the paintball.
	 * @param id - The ID tag of the paintball.
	 * @param game - An instance of the Game class, used to send damage packets to the server.
	 * @param handler - An instance of the Handler class, used to manage collisions with other
	 * objects.
	 * @param shooter - The game object that shot the paintball.
	 */
	public Paintball(float x, float y, ID id, Game game, Handler handler, GameObject shooter) {
		super(x, y, id);
		
		this.game = game;
		this.handler = handler;
		this.shooter = shooter;
		
		Random random = new Random();
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
			Player target = ((Enemy) shooter).getTarget();
			
			float diffX = x-(target.getX()+12), diffY = y-(target.getY()+12),
					distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
							(y-target.getY())*(y-target.getY()));
			
			velX = (float) (((-1.0/distance)*diffX)*5);
			velY = (float) (((-1.0/distance)*diffY)*5);
		}
		
		if (!(game == null))
			handler.addObject(new Trail(x, y, ID.TRAIL, handler, colour, getBounds().width,
					getBounds().height, 0.075f));
		
		collision();
	}

	/**
	 * Checks to see if the paintball has collided with another game object.
	 */
	private void collision() {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getBounds().intersects(getBounds())) {
				if (tempObject.getID() == ID.PLAYER) {
					if (!(shooter.getID() == ID.PLAYER)) {
						if (!(game == null)) {
							Game.player.health -= 1;
							
							Packet04Damage packet = new Packet04Damage(Game.player.getUsername(),
									1);
							packet.writeData(game.client);
						}
						
						handler.removeObject(this);
					}
				} else if (tempObject.getID() == ID.IPLAYER) {
					if (!(shooter.equals(tempObject))) {
						handler.removeObject(this);
					}
				} else if (tempObject.getID() == ID.ENEMY || tempObject.getID() ==
						ID.MOVINGENEMY || tempObject.getID() == ID.BOUNCYENEMY ||
						tempObject.getID() == ID.HOMINGENEMY) {
					Enemy enemy = (Enemy) tempObject;
					
					if (shooter.getID() == ID.PLAYER || shooter.getID() == ID.IPLAYER) {
						Player player = (Player) shooter;
						
						if (enemy.getID() == ID.ENEMY) player.score += 1;
						else if (enemy.getID() == ID.MOVINGENEMY) player.score += 2;
						else if (enemy.getID() == ID.BOUNCYENEMY) player.score += 3;
						else if (enemy.getID() == ID.HOMINGENEMY) player.score += 4;
						
						handler.removeObject(enemy);
						handler.removeObject(this);
					} else if (shooter.getID() == enemy.getID()) {
						if (!(shooter.equals(enemy))) {
							handler.removeObject(this);
						}
					} else handler.removeObject(this);
				} else if (tempObject.getID() == ID.IENEMY || tempObject.getID() ==
						ID.IMOVINGENEMY || tempObject.getID() == ID.IBOUNCYENEMY ||
						tempObject.getID() == ID.IHOMINGENEMY) {
					IEnemy enemy = (IEnemy) tempObject;
					
					if (shooter.getID() == ID.PLAYER || shooter.getID() == ID.IPLAYER) {
						Player player = (Player) shooter;
						
						if (enemy.getID() == ID.IENEMY) player.score += 1;
						else if (enemy.getID() == ID.IMOVINGENEMY) player.score += 2;
						else if (enemy.getID() == ID.IBOUNCYENEMY) player.score += 3;
						else if (enemy.getID() == ID.IHOMINGENEMY) player.score += 4;
						
						handler.removeObject(enemy);
						handler.removeObject(this);
					} else if (shooter.getID() == enemy.getID()) {
						if (!(shooter.equals(enemy))) {
							handler.removeObject(this);
						}
					} else handler.removeObject(this);
				} else if (tempObject.getID() == ID.PAINTBALL || tempObject.getID() ==
						ID.BOUNCYPAINTBALL || tempObject.getID() == ID.HOMINGPAINTBALL) {
					Paintball paintball = (Paintball) tempObject;
					
					if (!(this.equals(paintball))) {
						handler.removeObject(paintball);
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

	/**
	 * Gets the game object that shot the paintball.
	 * @return The object that shot the paintball.
	 */
	public GameObject getShooter() {
		return shooter;
	}
}
