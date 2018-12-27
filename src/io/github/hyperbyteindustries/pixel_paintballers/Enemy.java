package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.BLACK;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;
import io.github.hyperbyteindustries.pixel_paintballers.net.IEnemy;

/**
 * Represents the enemy AI of the game.
 * When constructed, this class is responsible for the management of the enemy.
 * @author Ramone Graham
 *
 */
public class Enemy extends GameObject {

	protected Game game;
	protected Handler handler;
	
	protected Player target;
	
	protected Random random = new Random();
	
	protected int shootTime;
	
	public long attackTimer, shootTimer;
	
	/**
	 * Creates a new enemy.
	 * @param x - The X coordinate of the enemy.
	 * @param y - The Y coordinate of the enemy.
	 * @param id - The identification tag of the enemy.
	 * @param game - An instance of the Game class, used to shoot paintballs.
	 * @param handler - An instance of the Handler class, used to shoot paintballs.
	 */
	public Enemy(float x, float y, ID id, Game game, Handler handler) {
		super(x, y, id);
		
		this.game = game;
		this.handler = handler;
		
		LinkedList<Player> targets = new LinkedList<Player>();
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (getBounds().intersects(tempObject.getBounds())) {
				if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
						tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
						ID.HOMINGENEMY || tempObject.getID() == ID.IENEMY ||
						tempObject.getID() == ID.IMOVINGENEMY || tempObject.getID() ==
						ID.IBOUNCYENEMY || tempObject.getID() == ID.IHOMINGENEMY ||
						tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER) {
					if (id == ID.ENEMY || id == ID.MOVINGENEMY || id == ID.BOUNCYENEMY ||
							id == ID.HOMINGENEMY) respawn();
					else if (id == ID.IENEMY || id == ID.IMOVINGENEMY || id == ID.IBOUNCYENEMY ||
							id == ID.IHOMINGENEMY) {
						IEnemy enemy = (IEnemy) this;
						
						if (!(enemy.getServer() == null)) respawn();
					} else
						throw new IllegalArgumentException("ID does not correspond to an enemy"
								+ "type.");
				}
			}
			
			if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER)
				targets.add((Player) tempObject);
		}
		
		target = targets.get(random.nextInt(targets.size()));
		
		if (Game.gameDifficulty == Difficulty.EASY) shootTime = 7;
		else if (Game.gameDifficulty == Difficulty.NORMAL) shootTime = 5;
		else if (Game.gameDifficulty == Difficulty.HARD) shootTime = 3;
		else if (Game.gameDifficulty == Difficulty.EXTREME) shootTime = 2;
		
		attackTimer = System.currentTimeMillis();
		shootTimer = System.currentTimeMillis();
	}

	/**
	 * Spawns the enemy at a different location if it's current location imposes a unfair
	 * advantage against players.
	 */
	private void respawn() {
		x = random.nextInt(Game.WIDTH-25);
		y = random.nextInt(Game.HEIGHT-25);
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (getBounds().intersects(tempObject.getBounds())) {
				if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
						tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
						ID.HOMINGENEMY || tempObject.getID() == ID.IENEMY ||
						tempObject.getID() == ID.IMOVINGENEMY || tempObject.getID() ==
						ID.IBOUNCYENEMY || tempObject.getID() == ID.IHOMINGENEMY ||
						tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER)
					respawn();
			}
		}
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 24, 24);
	}

	// See tick() in GameObject.
	public void tick() {
		x += velX;
		y += velY;
		
		if (id == ID.MOVINGENEMY) {
			float diffX = x - target.getX() - 4, diffY = y - target.getY() - 4,
					distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
			
			velX = (float) ((-1.0/distance)*diffX);
			velY = (float) ((-1.0/distance)*diffY);
			
			if (getBounds().intersects(target.getBounds())) {
				if (System.currentTimeMillis() - attackTimer >= 1500) {
					attackTimer = System.currentTimeMillis();
					
					target.health -= 2;
				}
			}
		}
		
		if (System.currentTimeMillis()-shootTimer >= shootTime*1000) {
			shootTimer = System.currentTimeMillis();
			
			Paintball paintball = null;
			
			if (id == ID.ENEMY || id == ID.MOVINGENEMY)
				paintball = new Paintball(x+8, y+8, ID.PAINTBALL, game, handler, this);
			else if (id == ID.BOUNCYENEMY)
				paintball = new Paintball(x+8, y+8, ID.BOUNCYPAINTBALL, game, handler, this);
			else if (id == ID.HOMINGENEMY)
				paintball = new Paintball(x+8, y+8, ID.HOMINGPAINTBALL, game, handler, this);
			
			handler.addObject(paintball);
			
			float diffX = paintball.getX() - target.getX() - 12,
					diffY = paintball.getY() - target.getY() - 12,
					distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
			
			if (distance >= 50) {
				paintball.setVelX((float) ((-1.0/distance)*diffX)*7);
				paintball.setVelY((float) ((-1.0/distance)*diffY)*7);
			} else if (distance >= 40) {
				paintball.setVelX((float) (((-1.0/distance)*diffX)*6.5));
				paintball.setVelY((float) (((-1.0/distance)*diffY)*6.5));
			} else if (distance >= 30) {
				paintball.setVelX((float) ((-1.0/distance)*diffX)*6);
				paintball.setVelY((float) ((-1.0/distance)*diffY)*6);
			} else if (distance >= 20) {
				paintball.setVelX((float) (((-1.0/distance)*diffX)*5.5));
				paintball.setVelY((float) (((-1.0/distance)*diffY)*5.5));
			} else if (distance >= 10) {
				paintball.setVelX((float) ((-1.0/distance)*diffX)*5);
				paintball.setVelY((float) ((-1.0/distance)*diffY)*5);
			} else {
				paintball.setVelX((float) (((-1.0/distance)*diffX)*4.5));
				paintball.setVelY((float) (((-1.0/distance)*diffY)*4.5));
			}
			
			AudioManager.getSound("Shot").play(1.0f, 0.10f);
		}
	}

	// See render(Graphics2D graphics2D) in GameObject.
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
		graphics2D.drawString(String.valueOf(shootTimeText), x+8, y+15);
	}
	
	/**
	 * Sets the enemy's target to the given player.
	 * @param target - The new player for the enemy to target.
	 */
	public void setTarget(Player target) {
		this.target = target;
	}
	
	/**
	 * Returns the enemy's target.
	 * @return The player the enemy is targeting.
	 */
	public Player getTarget() {
		return target;
	}
}
