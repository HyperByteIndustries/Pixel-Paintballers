package io.github.hyperbyteindustries.pixel_paintballers.entities;

import static java.awt.Color.BLACK;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;
import io.github.hyperbyteindustries.pixel_paintballers.managers.AudioManager;

/**
 * Represents the enemy AI of the game.
 * When constructed, this class is responsible for the management of the enemy.
 * @author Ramone Graham
 *
 */
public class Enemy extends Mob {

	protected Game game;
	protected Handler handler;
	protected Player target;
	
	protected Random random = new Random();
	
	protected int shootTime;
	
	public long attackTimer, shootTimer;
	
	/**
	 * Creates a new enemy.
	 * @param id - The identification tag of the enemy.
	 * @param x - The X coordinate of the enemy.
	 * @param y - The Y coordinate of the enemy.
	 * @param game - An instance of the Game class, used to shoot paintballs.
	 * @param handler - An instance of the Handler class, used to shoot paintballs.
	 */
	public Enemy(ID id, float x, float y, Game game, Handler handler) {
		super(id, x, y, 1);
		
		this.game = game;
		this.handler = handler;
		
		LinkedList<Player> targets = new LinkedList<Player>();
		
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity entity = handler.getEntities().get(i);
			
			if (entity.getID() == ID.PLAYER || entity.getID() == ID.IPLAYER)
				targets.add((Player) entity);
		}
		
		target = targets.get(random.nextInt(targets.size()));
		
		boolean spawnable = false;
		
		while (!spawnable) {
			spawnable = true;
			
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (getBounds().intersects(entity.getBounds())) {
					if (entity.getID() == ID.PLAYER || entity.getID() == ID.IPLAYER ||
							entity.getID() == ID.ENEMY || entity.getID() == ID.MOVINGENEMY ||
							entity.getID() == ID.BOUNCYENEMY || entity.getID() ==
							ID.HOMINGENEMY || entity.getID() == ID.IENEMY || entity.getID() ==
							ID.IMOVINGENEMY || entity.getID() == ID.IBOUNCYENEMY ||
							entity.getID() == ID.IHOMINGENEMY) {
						spawnable = false;
						
						break;
					}
				}
			}
			
			if (!spawnable) {
				this.x = random.nextInt(Game.WIDTH-25);
				this.y = random.nextInt(Game.HEIGHT-25);
			}
		}
		
		if (Game.gameDifficulty == Difficulty.EASY) shootTime = 7;
		else if (Game.gameDifficulty == Difficulty.NORMAL) shootTime = 5;
		else if (Game.gameDifficulty == Difficulty.HARD) shootTime = 3;
		else if (Game.gameDifficulty == Difficulty.EXTREME) shootTime = 2;
		
		long initTime = System.currentTimeMillis();
		
		attackTimer = initTime;
		shootTimer = initTime;
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
		
		if (id == ID.MOVINGENEMY) {
			float diffX = x - target.getX() - 4, diffY = y - target.getY() - 4,
					distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
			
			velX = -1/distance*diffX;
			velY = -1/distance*diffY;
			
			if (getBounds().intersects(target.getBounds())) {
				if (System.currentTimeMillis()-attackTimer >= 1500) {
					attackTimer = System.currentTimeMillis();
					
					target.health -= 2;
				}
			}
		}
		
		if (System.currentTimeMillis()-shootTimer >= shootTime*1000) {
			shootTimer = System.currentTimeMillis();
			
			Paintball paintball = null;
			
			if (id == ID.ENEMY || id == ID.MOVINGENEMY)
				paintball = new Paintball(ID.PAINTBALL, x+8, y+8, game, handler, this);
			else if (id == ID.BOUNCYENEMY)
				paintball = new Paintball(ID.BOUNCYPAINTBALL, x+8, y+8, game, handler, this);
			else if (id == ID.HOMINGENEMY)
				paintball = new Paintball(ID.HOMINGPAINTBALL, x+8, y+8, game, handler, this);
			
			handler.addEntity(paintball);
			
			float diffX = paintball.getX() - target.getX() - 12,
					diffY = paintball.getY() - target.getY() - 12,
					distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
			
			paintball.setVelX(-1/distance*diffX);
			paintball.setVelY(-1/distance*diffY);
			
			AudioManager.getSound("Shot").play(1, Game.sfxVolume);
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
		int shootTimeText = shootTime - (int) (System.currentTimeMillis()-shootTimer) / 1000;
		
		if (id == ID.ENEMY || id == ID.IENEMY) graphics2D.setColor(GREEN);
		else if (id == ID.MOVINGENEMY || id == ID.IMOVINGENEMY) graphics2D.setColor(CYAN);
		else if (id == ID.BOUNCYENEMY || id == ID.IBOUNCYENEMY) graphics2D.setColor(YELLOW);
		else if (id == ID.HOMINGENEMY || id == ID.IHOMINGENEMY) graphics2D.setColor(RED);
		
		graphics2D.fill(getBounds());
		graphics2D.setColor(WHITE);
		graphics2D.draw(getBounds());
		graphics2D.setColor(BLACK);
		graphics2D.setFont(new Font("Pixel EX", Font.PLAIN, 10));
		
		FontMetrics metrics = graphics2D.getFontMetrics();
		Rectangle2D stringBounds = metrics.getStringBounds(String.valueOf(shootTimeText),
				graphics2D);
		
		graphics2D.drawString(String.valueOf(shootTimeText),
				(int) (getBounds().getCenterX()-stringBounds.getWidth()/2),
				(int) (getBounds().getCenterY()+stringBounds.getHeight()/2));
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#getBounds()
	 */
	/**
	 * {@inheritDoc}
	 */
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 24, 24);
	}

	/**
	 * Sets the player target of the enemy.
	 * @param target - The target to set.
	 */
	public void setTarget(Player target) {
		this.target = target;
	}

	/**
	 * Gets the player target of the enemy.
	 * @return The player target of the enemy.
	 */
	public Player getTarget() {
		return target;
	}
}
