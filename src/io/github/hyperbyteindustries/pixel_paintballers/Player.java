package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.RED;
import static java.awt.Color.BLUE;
import static java.awt.Color.WHITE;

import java.awt.Color;

import static java.awt.Color.GRAY;

import java.awt.Font;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet02PlayerMove;

/**
 * Represents the main player of the game.
 * When constructed, this class is responsible for the management of the player.
 * @author Ramone Graham
 *
 */
public class Player extends GameObject {

	private Game game;
	
	private float prevVelX = 0, prevVelY = 0;
	private boolean stationaryMovePacketSent = true;
	
	protected String username;
	
	public int health = 100, maxHealth = 100, score = 0;
	public Color healthColour;
	
	/**
	 * Creates a new player.
	 * @param x - The x coordinate of the player.
	 * @param y - The y coordinate of the player.
	 * @param id - The ID tag of the player.
	 * @param game - An instance of the Game class, used to send move packets.
	 * @param username - The username of the player.
	 */
	public Player(float x, float y, ID id, Game game, String username) {
		super(x, y, id);
		
		this.game = game;
		this.username = username;
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 32, 32);
	}

	// See tick() in GameObject.
	public void tick() {
		float prevX = x, prevY = y;
		
		x += velX;
		y += velY;
		
		x = Game.clamp(x, 0, Game.XBOUND-33);
		y = Game.clamp(y, 0, Game.YBOUND-33);
		health = Game.clamp(health, 0, maxHealth);
		
		if (id == ID.IPLAYER) {
			int healthpercentage = (int) (((double) health/(double) maxHealth)*100);
			
			healthColour = new Color(75, healthpercentage*2, 0);
		}
		
		if (!(game == null)) {
			Packet02PlayerMove packet = null;
			
			if (x == prevX && y == prevY && !(stationaryMovePacketSent)) {
				packet = new Packet02PlayerMove(username, false, x, y);
				
				stationaryMovePacketSent = true;
			} else if (x != prevX || y != prevY) {
				if (velX != prevVelX || velY != prevVelY) {
					packet = new Packet02PlayerMove(username, true, velX, velY);
					
					prevVelX = velX;
					prevVelY = velY;
				}
				
				stationaryMovePacketSent = false;
			}
			
			if (!(packet == null)) packet.writeData(game.client);
		}
	}

	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		if (id == ID.PLAYER) graphics2d.setColor(RED);
		else if (id == ID.IPLAYER) graphics2d.setColor(BLUE);
		
		graphics2d.fill(getBounds());
		graphics2d.setColor(WHITE);
		graphics2d.draw(getBounds());
		
		if (!(username == null)) {
			graphics2d.setColor(GRAY);
			graphics2d.setFont(new Font("Pixel EX", Font.PLAIN, 14));
			
			graphics2d.drawString(username, (float) (x-((username.length()-1)/2*9.5)), y-5);
			
			if (id == ID.IPLAYER) {
				graphics2d.setColor(healthColour);
				
				if (health == 100) {
					graphics2d.drawString("Health: " + health,
							(x-((("Health: " + health).length()-1)/2*8)), y-20);
				} else if (health > 9) {
					graphics2d.drawString("Health: " + health,
							(float) (x-((("Health: " + health).length()-1)/2*9.5)), y-20);
				} else {
					graphics2d.drawString("Health: " + health,
							(x-((("Health: " + health).length()-1)/2*9)), y-20);
				}
			}
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
	 * Returns the current username of the player.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
}
