package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Represents the heads up display of the game.
 * When constructed, this class is responsible for the display of health, score, and
 * level.
 * @author Ramone Graham
 *
 */
public class HeadsUpDisplay {

	private Color healthColour;
	
	public static int maxHealth = 100, health = 100, score = 0, level = 0;
	
	/**
	 * Updates the logic of the display.
	 */
	public void tick() {
		health = Game.clamp(health, 0, maxHealth);
		int healthpercentage = (int) (((double) health/(double) maxHealth)*100);
		
		healthColour = new Color(75, healthpercentage*2, 0);
	}
	
	/**
	 * Updates the visuals of the display.
	 * @param graphics2d - The graphics used to update the visuals.
	 */
	public void render(Graphics2D graphics2d) {
		graphics2d.setColor(GRAY);
		graphics2d.fillRect(5, 5, maxHealth, 25);
		graphics2d.setColor(healthColour);
		graphics2d.fillRect(5, 5, health, 25);
		graphics2d.setColor(WHITE);
		graphics2d.drawRect(5, 5, maxHealth, 25);
		graphics2d.setFont(new Font("Pixel EX", Font.PLAIN, 10));
		graphics2d.drawString("Health: " + health, 15, 22);
		graphics2d.drawString("Score: " + score, 5, 45);
		graphics2d.drawString("Level: " + level, 5, 60);
	}
}
