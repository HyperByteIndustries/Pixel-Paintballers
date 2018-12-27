package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Mode;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet02PlayerMove;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet04Damage;

/**
 * Represents the main player of the game.
 * When constructed, this class is responsible for the management of the player.
 * @author Ramone Graham
 *
 */
public class Player extends GameObject {

	private Game game;
	
	private String username;
	private Color fillColour, outlineColour, usernameColour;
	
	private float prevVelX = 0, prevVelY = 0;
	private boolean stationaryMovePacketSent = true;
	
	public int health = 100, maxHealth = 100, prevHealth = health, score = 0;
	public Color healthColour;
	
	public boolean spectator = false;

	/**
	 * Creates a new player.
	 * @param x - The X coordinate of the player.
	 * @param y - The Y coordinate of the player.
	 * @param id - The identification tag of the player.
	 * @param game - An instance of the Game class, used to send packets.
	 * @param username - The username of the player.
	 * @param fillColour - The fill colour of the player.
	 * @param outlineColour - The outline colour of the player.
	 * @param usernameColour - The username colour of the player.
	 */
	public Player(float x, float y, ID id, Game game, String username, Color fillColour,
			Color outlineColour, Color usernameColour) {
		super(x, y, id);
		
		this.game = game;
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
		float prevX = x, prevY = y;
		
		x += velX;
		y += velY;
		
		x = Game.clamp(x, 0, Game.WIDTH-33);
		y = Game.clamp(y, 0, Game.HEIGHT-33);
		health = Game.clamp(health, 0, maxHealth);
		
		if (Game.gameMode == Mode.MULTIPLAYER && !(game == null)) {
			Packet02PlayerMove movePacket = null;
			
			if (x == prevX && y == prevY && !stationaryMovePacketSent) {
				movePacket = new Packet02PlayerMove(username, false, x, y);
				
				stationaryMovePacketSent = true;
			} else if (!(x == prevX) || !(y == prevY)) {
				if (!(velX == prevVelX) || !(velY == prevVelY)) {
					movePacket = new Packet02PlayerMove(username, true, velX, velY);
					
					prevVelX = velX;
					prevVelY = velY;
				}
				
				stationaryMovePacketSent = false;
			}
			
			if (!(movePacket == null)) movePacket.writeData(game.client);
			
			if (health < prevHealth) {
				Packet04Damage damagePacket = new Packet04Damage(Game.player.getUsername(),
						prevHealth-health);
				damagePacket.writeData(game.client);
			}
		}
		
		prevHealth = health;
		
		if (id == ID.IPLAYER) {
			if (health > 0) {
				double healthdecimal = (double) health / (double) maxHealth;
				
				healthColour = new Color((int) ((1-healthdecimal)*255),
						(int) (healthdecimal*255), 0);
			} else healthColour = new Color(255, 0, 0);
		}
	}

	// See render(Graphics2D graphics2D) in GameObject.
	public void render(Graphics2D graphics2D) {
		graphics2D.setColor(fillColour);
		graphics2D.fill(getBounds());
		graphics2D.setColor(outlineColour);
		graphics2D.draw(getBounds());
		
		if (!(username == null)) {
			graphics2D.setColor(usernameColour);
			graphics2D.setFont(new Font("Pixel EX", Font.PLAIN, 14));
			graphics2D.drawString(username, x-201/17*(username.length()-1)/2+4, y-5);
			
			if (id == ID.IPLAYER) {
				graphics2D.setColor(healthColour);
				
				if (health == 100) {
					graphics2D.drawString("Health: " + health,
							x-(("Health: " + health).length()-1)/2*8, y-20);
				} else if (health > 9) {
					graphics2D.drawString("Health: " + health,
							(float) (x-((("Health: " + health).length()-1)/2*9.5)), y-20);
				} else {
					graphics2D.drawString("Health: " + health,
							x-(("Health: " + health).length()-1)/2*9, y-20);
				}
			}
		}
	}
	
	/**
	 * Sets the username to the given value.
	 * @param username - The new username of the player.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Sets the fill colour to the given value.
	 * @param fillColour - The new fill colour of the player.
	 */
	public void setFillColour(Color fillColour) {
		this.fillColour = fillColour;
	}
	
	/**
	 * Sets the outline colour to the given value.
	 * @param outlineColour - The new outline colour of the player.
	 */
	public void setOutlineColour(Color outlineColour) {
		this.outlineColour = outlineColour;
	}
	
	/**
	 * Sets the username colour to the given value.
	 * @param usernameColour - The new username colour of the player.
	 */
	public void setUsernameColour(Color usernameColour) {
		this.usernameColour = usernameColour;
	}
	
	/**
	 * Returns the username of the player.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the fill colour of the player.
	 * @return The fill colour of the player.
	 */
	public Color getFillColour() {
		return fillColour;
	}
	
	/**
	 * Returns the outline colour of the player.
	 * @return The outline colour of the player.
	 */
	public Color getOutlineColour() {
		return outlineColour;
	}
	
	/**
	 * Returns the username colour of the player.
	 * @return The username colour of the player.
	 */
	public Color getUsernameColour() {
		return usernameColour;
	}
}
