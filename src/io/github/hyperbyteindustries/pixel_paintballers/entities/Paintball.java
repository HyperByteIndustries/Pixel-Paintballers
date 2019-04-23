package io.github.hyperbyteindustries.pixel_paintballers.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.ui.HeadsUpDisplay;

/**
 * Represents the main weapon of the game.
 * When constructed, this class is responsible for the management of the paintball.
 * @author Ramone Graham
 *
 */
public class Paintball extends Mob {

	private Game game;
	private Handler handler;
	
	private final Entity shooter;
	
	private Color colour;
	
	/**
	 * Creates a new paintball.
	 * @param id - The identification tag of the paintball.
	 * @param x - The X coordinate of the paintball.
	 * @param y - The Y coordinate of the paintball.
	 * @param game - An instance of the Game class, used to create a trail.
	 * @param handler - An instance of the Handler class, used to manage collisions with other
	 * entities, and to remove the paintball after said collision.
	 * @param shooter - The game object that shot the paintball.
	 */
	public Paintball(ID id, float x, float y, Game game, Handler handler, Entity shooter) {
		super(id, x, y, 7);
		
		this.game = game;
		this.handler = handler;
		this.shooter = shooter;
		
		Random random = new Random();
		colour = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
		
		if (id == ID.HOMINGPAINTBALL) speed = 6.25f;
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#tick()
	 */
	/**
	 * {@inheritDoc}
	 */
	public void tick() {
		x += velX * speed;
		y += velY * speed;
		
		if (id == ID.PAINTBALL) {
			if (x <= 0 || x >= Game.WIDTH-9) handler.removeEntity(this);
			if (y <= 0 || y >= Game.HEIGHT-9) handler.removeEntity(this);
		} else if (id == ID.BOUNCYPAINTBALL) {
			if (x <= 0 || x >= Game.WIDTH-9) velX *= -1;
			if (y <= 0 || y >= Game.HEIGHT-9) velY *= -1;
		} else if (id == ID.HOMINGPAINTBALL) {
			Player target = ((Enemy) shooter).getTarget();
			
			float diffX = x - target.getX() - 12, diffY = y - target.getY() - 12,
					distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
			
			velX = -1/distance*diffX;
			velY = -1/distance*diffY;
		}
		
		if (!(game == null)) handler.addEntity(new Trail(handler, getBounds(), colour, 0.075f));
		
		collision();
	}

	/**
	 * Checks to see if the paintball has collided with another game object.
	 */
	private void collision() {
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity entity = handler.getEntities().get(i);
			
			if (entity.getBounds().intersects(getBounds())) {
				if (entity.getID() == ID.PLAYER) {
					if (!(shooter.getID() == ID.PLAYER)) {
						Game.player.health -= 1;
						
						handler.removeEntity(this);
						
						break;
					}
				} else if (entity.getID() == ID.IPLAYER) {
					if (!shooter.equals(entity)) handler.removeEntity(this);
				} else if (entity.getID() == ID.ENEMY || entity.getID() == ID.MOVINGENEMY ||
						entity.getID() == ID.BOUNCYENEMY || entity.getID() == ID.HOMINGENEMY ||
						entity.getID() == ID.IENEMY || entity.getID() == ID.IMOVINGENEMY ||
						entity.getID() == ID.IBOUNCYENEMY || entity.getID() == ID.IHOMINGENEMY) {
					Enemy enemy = (Enemy) entity;
					
					if (shooter.getID() == ID.PLAYER || shooter.getID() == ID.IPLAYER) {
						Player player = (Player) shooter;
						
						if (enemy.getID() == ID.ENEMY || enemy.getID() == ID.IENEMY)
							player.score += 1;
						else if (enemy.getID() == ID.MOVINGENEMY || enemy.getID() ==
								ID.IMOVINGENEMY) player.score += 2;
						else if (enemy.getID() == ID.BOUNCYENEMY || enemy.getID() ==
								ID.BOUNCYENEMY) player.score += 3;
						else if (enemy.getID() == ID.HOMINGENEMY || enemy.getID() ==
								ID.HOMINGENEMY) player.score += 4;
						
						if (player.equals(Game.player)) HeadsUpDisplay.kills++;
						
						handler.removeEntity(enemy);
						handler.removeEntity(this);
						
						break;
					} else if (shooter.getID() == enemy.getID()) {
						if (!shooter.equals(enemy)) handler.removeEntity(this);
					} else handler.removeEntity(this);
				} else if (entity.getID() == ID.PAINTBALL || entity.getID() ==
						ID.BOUNCYPAINTBALL || entity.getID() == ID.HOMINGPAINTBALL) {
					Paintball paintball = (Paintball) entity;
					
					if (!equals(paintball)) {
						handler.removeEntity(paintball);
						handler.removeEntity(this);
						
						break;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#render(java.awt.Graphics2D)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void render(Graphics2D graphics2D) {
		graphics2D.setColor(colour);
		graphics2D.fill(getBounds());
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#getBounds()
	 */
	/**
	 * {@inheritDoc}
	 */
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 8, 8);
	}

	/**
	 * Returns the game object that shot the paintball.
	 * @return The object that shot the paintball.
	 */
	public Entity getShooter() {
		return shooter;
	}
}
