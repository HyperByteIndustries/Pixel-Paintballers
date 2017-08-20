package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.GREEN;
import static java.awt.Color.CYAN;
import static java.awt.Color.YELLOW;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.GRAY;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;

/**
 * Represents the enemy AI of the game.
 * When constructed, this class is responsible for the management of the enemy.
 * @author Ramone Graham
 *
 */
public class Enemy extends GameObject {

	private Handler handler;
	
	private Random random;
	
	private int attack = 0;
	private int shootTime = 0 /*420 = 7 secs*/;
	
	/**
	 * Creates a new enemy.
	 * @param x - The x coordinate of the enemy.
	 * @param y - The y coordinate of the enemy.
	 * @param id - The ID tag of the enemy.
	 * @param handler - An instance of the Handler class, used to shoot paintballs.
	 */
	public Enemy(float x, float y, ID id, Handler handler) {
		super(x, y, id);
		
		this.handler = handler;
		random = new Random();
		
		for (int i = 0; i < handler.objects.size(); i++) {
			GameObject tempObject = handler.objects.get(i);
			
			if (tempObject.getID() == ID.ENEMY || tempObject.getID() ==
					ID.MOVINGENEMY || tempObject.getID() == ID.BOUNCYENEMY ||
					tempObject.getID() == ID.HOMINGENEMY || tempObject.getID() ==
					ID.PLAYER) {
				if (getBounds().intersects(tempObject.getBounds())) respawn();
			}
		}
		
		if (Game.gameDifficulty == Difficulty.EASY) shootTime = 420;
		else if (Game.gameDifficulty == Difficulty.NORMAL) shootTime = 300;
		else if (Game.gameDifficulty == Difficulty.HARD) shootTime = 180;
		else if (Game.gameDifficulty == Difficulty.EXTREME) shootTime = 120;
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
			float diffX = x-(Game.player.getX()+4), diffY = y-(Game.player.getY()+
					4), distance = (float) Math.sqrt((x-Game.player.getX())*(x-
							Game.player.getX()) + (y-Game.player.getY())*(y-
									Game.player.getY()));
			
			velX = (float) ((-1.0/distance)*diffX);
			velY = (float) ((-1.0/distance)*diffY);
			
			if (getBounds().intersects(Game.player.getBounds())) {
				if (attack == 1) {
					HeadsUpDisplay.health -= 2;
					attack++;
				} else if (attack == 100) attack = 1;
				else attack++;
			}
		}
		
		shootTime--;
		
		if (shootTime == 0) {
			if (Game.gameDifficulty == Difficulty.EASY) shootTime = 420;
			else if (Game.gameDifficulty == Difficulty.NORMAL) shootTime = 300;
			else if (Game.gameDifficulty == Difficulty.HARD) shootTime = 180;
			else if (Game.gameDifficulty == Difficulty.EXTREME) shootTime = 120;
			
			Paintball paintball = null;
			
			if (id == ID.ENEMY || id == ID.MOVINGENEMY) paintball =
					new Paintball(x+8, y+8, ID.PAINTBALL, handler, this);
			else if (id == ID.BOUNCYENEMY) paintball = new Paintball(x+8, y+8,
					ID.BOUNCYPAINTBALL, handler, this);
			else if (id == ID.HOMINGENEMY) paintball = new Paintball(x+8, y+8,
					ID.HOMINGPAINTBALL, handler, this);
			
			handler.addObject(paintball);
			
			float diffX = x-Game.player.getX(), diffY = y-Game.player.getY(),
					distance = (float) Math.sqrt((x-Game.player.getX())*(x-
							Game.player.getX()) + (y-Game.player.getY())*(y-
									Game.player.getY()));
			
			paintball.setVelX((float) (((-1.0/distance)*diffX)*7));
			paintball.setVelY((float) (((-1.0/distance)*diffY)*7));
		}
	}

	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		Font font = new Font("Pixel EX", Font.PLAIN, 10);
		int shootTime = (int) ((double) ((double) this.shootTime/(double) 420)*10);
		String shootTimePrompt = "Shooting in " + shootTime;
		
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
		graphics2d.setColor(GRAY);
		graphics2d.setFont(font);
		graphics2d.drawString(shootTimePrompt, x-((shootTimePrompt.length()-1)/2*
				5), y);
	}

	private void respawn() {
		x = random.nextInt(Game.XBOUND-25);
		y = random.nextInt(Game.YBOUND-25);
		
		for (int i = 0; i < handler.objects.size(); i++) {
			GameObject tempObject = handler.objects.get(i);
			
			if (tempObject.getID() == ID.ENEMY || tempObject.getID() ==
					ID.MOVINGENEMY || tempObject.getID() == ID.BOUNCYENEMY ||
					tempObject.getID() == ID.HOMINGENEMY || tempObject.getID() ==
					ID.PLAYER) {
				if (getBounds().intersects(tempObject.getBounds())) respawn();
				else return;
			}
		}
	}
}
