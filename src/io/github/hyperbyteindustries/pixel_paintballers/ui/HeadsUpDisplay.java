package io.github.hyperbyteindustries.pixel_paintballers.ui;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Spawner;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;

/**
 * Represents the heads-up display of the game.
 * When constructed, this class is responsible for the display of the player's health, score,
 * level and remaining ammo.
 * @author Ramone Graham
 *
 */
public class HeadsUpDisplay {
	
	public static int ammo = 0, reloadTime = 120, shots = 0, kills = 0;
	public static boolean reloading = false;
	
	private Handler handler;
	private float alpha = 1;
	
	/**
	 * Creates a new instance of the HUD.
	 * @param handler - An instance of the Handler class, used to determine the HUD transparency.
	 */
	public HeadsUpDisplay(Handler handler) {
		this.handler = handler;
	}
	
	/**
	 * Updates the logic of the display.
	 */
	public void tick() {
		if (reloading) {
			reloadTime--;
			
			if (reloadTime == 0) {
				reloadTime = 120;
				
				reloadAmmo();
			}
		}
		
		boolean blindspot = false;
		
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity entity = handler.getEntities().get(i);
			
			if (new Rectangle(0, 0, 105, 65).intersects(entity.getBounds())) {
				if (entity.getID() != ID.PAINTBALL && entity.getID() != ID.BOUNCYPAINTBALL &&
						entity.getID() != ID.HOMINGPAINTBALL && entity.getID() != ID.TRAIL) {
					blindspot = true;
					break;
				}
			}
		}
		
		if (blindspot) alpha -= 0.075f;
		else alpha += 0.075f;
		
		alpha = Game.clamp(alpha, 0.25f, 1);
	}

	/**
	 * Reloads the player's paintball ammunition.
	 */
	public static void reloadAmmo() {
		if (Game.gameDifficulty == Difficulty.NORMAL) HeadsUpDisplay.ammo = 15;
		else if (Game.gameDifficulty == Difficulty.HARD) HeadsUpDisplay.ammo = 10;
		else if (Game.gameDifficulty == Difficulty.EXTREME) HeadsUpDisplay.ammo = 5;
		
		reloading = false;
	}
	
	/**
	 * Updates the visuals of the display.
	 * @param graphics2D - The graphics context used to update the visuals.
	 */
	public void render(Graphics2D graphics2D) {
		graphics2D.setComposite(AlphaComposite.getInstance(SRC_OVER, alpha));
		
		graphics2D.setColor(GRAY);
		graphics2D.fillRect(5, 5, Game.player.maxHealth, 25);
		graphics2D.setColor(Game.player.getHealthBarColour());
		graphics2D.fillRect(5, 5, Game.player.health, 25);
		graphics2D.setColor(WHITE);
		graphics2D.drawRect(5, 5, Game.player.maxHealth, 25);
		graphics2D.setFont(new Font("Pixel EX", Font.PLAIN, 10));
		graphics2D.drawString("Health: " + Game.player.health, 15, 22);
		graphics2D.drawString("Score: " + Game.player.score, 5, 45);
		graphics2D.drawString("Level: " + Spawner.level, 5, 55);
		
		if (Game.gameDifficulty != Difficulty.EASY) {
			if (reloading) graphics2D.drawString("Ammo: Reloading...", 5, 65);
			else if (ammo == 0) graphics2D.drawString("Ammo: Out of ammo!", 5, 65);
			else graphics2D.drawString("Ammo: " + ammo, 5, 65);
		}
		
		graphics2D.setComposite(AlphaComposite.getInstance(SRC_OVER, 1));
	}
}
