package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents the main player of the game.
 * When constructed, this class is responsible for the management of the player.
 * @author Ramone Graham
 *
 */
public class Player extends GameObject {
	
	private String username;
	
	private Color fillColour, outlineColour, usernameColour;

	/**
	 * Creates a new player.
	 * @param x - The x coordinate of the player.
	 * @param y - The y coordinate of the player.
	 * @param id - The identification tag of the player.
	 * @param username - The username of the player.
	 * @param fillColour - The fill colour of the player.
	 * @param outlineColour - The outline colour of the player.
	 * @param usernameColour - The username colour of the player.
	 */
	public Player(float x, float y, ID id, String username, Color fillColour, 
			Color outlineColour, Color usernameColour) {
		super(x, y, id);
		this.username = username;
		this.fillColour = fillColour;
		this.outlineColour = outlineColour;
		this.usernameColour = usernameColour;
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 32, 32);
	}

	// See tick() in GameObject.
	public void tick() {
		x += velX;
		y += velY;
		
		x = Game.clamp(x, 0, Game.XBOUND-33);
		y = Game.clamp(y, 0, Game.YBOUND-33);
	}

	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		graphics2d.setColor(fillColour);
		graphics2d.fill(getBounds());
		graphics2d.setColor(outlineColour);
		graphics2d.draw(getBounds());
		
		if (!(username == null)) {
			graphics2d.setColor(usernameColour);
			graphics2d.setFont(new Font("Pixel EX", Font.PLAIN, 14));
			graphics2d.drawString(username, x-((username.length()-1)/2*9), y);
		}
	}
	
	/**
	 * Sets the username of the player.
	 * @param username - The username to be set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Sets the fill colour of the player.
	 * @param fillColour - The colour to be set.
	 */
	public void setFillColour(Color fillColour) {
		this.fillColour = fillColour;
	}
	
	/**
	 * Sets the outline colour of the player.
	 * @param outlineColour - The colour to be set.
	 */
	public void setOutlineColour(Color outlineColour) {
		this.outlineColour = outlineColour;
	}
	
	/**
	 * Sets the username colour of the player.
	 * @param usernameColour - The colour to be set.
	 */
	public void setUsernameColour(Color usernameColour) {
		this.usernameColour = usernameColour;
	}
	
	/**
	 * Returns the current username of the player.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the current fill colour of the player.
	 * @return The fill colour of the player.
	 */
	public Color getFillColour() {
		return fillColour;
	}
	
	/**
	 * Returns the current outline colour of the player.
	 * @return The outline colour of the player.
	 */
	public Color getOutlineColour() {
		return outlineColour;
	}
	
	/**
	 * Returns the current username colour of the player.
	 * @return The username colour of the player.
	 */
	public Color getUsernameColour() {
		return usernameColour;
	}
}
