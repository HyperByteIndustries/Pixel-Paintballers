package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.GREEN;
import static java.awt.Color.CYAN;
import static java.awt.Color.YELLOW;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.BLACK;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.LinkedList;
import java.util.Random;

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
	
	protected Random random;
	
	public int attackTime = 100;
	public int shootTime = 420;
	
	/**
	 * Creates a new enemy.
	 * @param x - The x coordinate of the enemy.
	 * @param y - The y coordinate of the enemy.
	 * @param id - The ID tag of the enemy.
	 * @param game - An instance of the game class, used to create shoot paintballs.
	 * @param handler - An instance of the Handler class, used to shoot paintballs.
	 */
	public Enemy(float x, float y, ID id, Game game, Handler handler) {
		super(x, y, id);
		
		this.game = game;
		this.handler = handler;
		random = new Random();
		
		LinkedList<Player> targets = new LinkedList<Player>();
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
					tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
					ID.HOMINGENEMY || tempObject.getID() == ID.PLAYER || tempObject.getID() ==
					ID.IPLAYER) {
				if (getBounds().intersects(tempObject.getBounds())) respawn();
				
				if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER) {
					targets.add((Player) tempObject);
				}
			}
		}
		
		target = targets.get(random.nextInt(targets.size()));
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
			float diffX = x-(target.getX()+4), diffY = y-(target.getY()+4),
					distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
							(y-target.getY())*(y-target.getY()));
			
			velX = (float) ((-1.0/distance)*diffX);
			velY = (float) ((-1.0/distance)*diffY);
			
			if (getBounds().intersects(target.getBounds())) {
				if (attackTime == 100) {
					target.health -= 2;
					attackTime--;
				} else if (attackTime == 0) attackTime = 100;
				else attackTime--;
			}
		}
		
		if (shootTime == 0) {
			Paintball paintball = null;
			
			if (id == ID.ENEMY || id == ID.MOVINGENEMY) paintball = new Paintball(x+8, y+8,
					ID.PAINTBALL, game, handler, this);
			else if (id == ID.BOUNCYENEMY) paintball = new Paintball(x+8, y+8,
					ID.BOUNCYPAINTBALL, game, handler, this);
			else if (id == ID.HOMINGENEMY) paintball = new Paintball(x+8, y+8,
					ID.HOMINGPAINTBALL, game, handler, this);
			
			handler.addObject(paintball);
			
			float diffX = x-target.getX(), diffY = y-target.getY(),
					distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
							(y-target.getY())*(y-target.getY()));
			
			paintball.setVelX((float) (((-1.0/distance)*diffX)*7));
			paintball.setVelY((float) (((-1.0/distance)*diffY)*7));
			
			shootTime = 420;
		} else shootTime--;
	}

	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		int shootTime = (int) (((double) ((double) this.shootTime/(double) 600)*10)+1);
		
		if (id == ID.ENEMY) {
			graphics2d.setColor(GREEN);
		} else if (id == ID.MOVINGENEMY) {
			graphics2d.setColor(CYAN);
		} else if (id == ID.BOUNCYENEMY) {
			graphics2d.setColor(YELLOW);
		} else if (id == ID.HOMINGENEMY) {
			graphics2d.setColor(RED);
		}
		
		graphics2d.fill(getBounds());
		graphics2d.setColor(WHITE);
		graphics2d.draw(getBounds());
		graphics2d.setColor(BLACK);
		graphics2d.setFont(new Font("Pixel EX", Font.PLAIN, 10));
		graphics2d.drawString(String.valueOf(shootTime), x+8, y+15);
	}

	private void respawn() {
		x = random.nextInt(Game.XBOUND-25);
		y = random.nextInt(Game.YBOUND-25);
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
					tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
					ID.HOMINGENEMY || tempObject.getID() == ID.PLAYER || tempObject.getID() ==
					ID.IPLAYER) {
				if (getBounds().intersects(tempObject.getBounds())) respawn();
				else return;
			}
		}
	}
}
