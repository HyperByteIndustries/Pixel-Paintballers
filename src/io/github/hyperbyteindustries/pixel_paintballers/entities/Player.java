package io.github.hyperbyteindustries.pixel_paintballers.entities;

import static java.awt.Color.GREEN;
import static java.awt.Color.RED;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.Game.Mode;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet02PlayerMove;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet04Damage;

/**
 * Represents the main player of the game.
 * When constructed, this class is responsible for the management of the player.
 * @author Ramone Graham
 *
 */
public class Player extends Mob {

	private Game game;
	
	private String username;
	private Color fill, outline, name, healthBar = GREEN;
	
	private float prevVelX = 0, prevVelY = 0;
	private boolean stationaryMovePacketSent = true;
	
	public int health = 100, maxHealth = 100, prevHealth = health, score = 0;
	
	public boolean spectator = false;

	/**
	 * Creates a new player.
	 * @param x - The X coordinate of the player.
	 * @param y - The Y coordinate of the player.
	 * @param game - An instance of the Game class, used to send packets.
	 * @param username - The username of the player.
	 * @param fill - The fill colour of the player.
	 * @param outline - The outline colour of the player.
	 * @param username - The username colour of the player.
	 */
	public Player(float x, float y, Game game, String username, Color fill, Color outline,
			Color name) {
		super(ID.PLAYER, x, y, 2.5f);
		
		this.game = game;
		this.username = username;
		this.fill = fill;
		this.outline = outline;
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#tick()
	 */
	/**
	 * {@inheritDoc}
	 */
	public void tick() {
		float prevX = x, prevY = y;
		
		x += velX * speed;
		y += velY * speed;
		
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
				Packet04Damage damagePacket = new Packet04Damage(username, prevHealth-health);
				damagePacket.writeData(game.client);
			}
		}
		
		prevHealth = health;
		
		if (health > 0) {
			double healthdecimal = (double) health / (double) maxHealth;
			
			healthBar = new Color((int) ((1-healthdecimal)*255), (int) (healthdecimal*255), 0);
		} else healthBar = RED;
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#render(java.awt.Graphics2D)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void render(Graphics2D graphics2D) {
		graphics2D.setColor(fill);
		graphics2D.fill(getBounds());
		graphics2D.setColor(outline);
		graphics2D.draw(getBounds());
		graphics2D.setColor(name);
		graphics2D.setFont(new Font("Pixel EX", Font.PLAIN, 14));
		
		FontMetrics metrics = graphics2D.getFontMetrics();
		Rectangle2D stringBounds = metrics.getStringBounds(username, graphics2D);
		
		graphics2D.drawString(username,
				(float) (getBounds().getCenterX()-stringBounds.getWidth()/2), y-5);
		
		if (id == ID.IPLAYER) {
			graphics2D.setColor(healthBar);
			
			String healthText = "Health: " + health;
			stringBounds = metrics.getStringBounds(healthText, graphics2D);
			
			graphics2D.drawString(healthText,
					(float) (getBounds().getCenterX()-stringBounds.getWidth()/2), y-20);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.Entity#getBounds()
	 */
	/**
	 * {@inheritDoc}
	 */
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 32, 32);
	}

	/**
	 * Sets the username of the player.
	 * @param username - The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets the fill colour of the player.
	 * @param fill - The fill colour to set.
	 */
	public void setFillColour(Color fill) {
		this.fill = fill;
	}

	/**
	 * Sets the outline colour of the player.
	 * @param outline - The outline colour to set.
	 */
	public void setOutlineColour(Color outline) {
		this.outline = outline;
	}

	/**
	 * Sets the username colour of the player.
	 * @param name - The username colour to set.
	 */
	public void setUsernameColour(Color name) {
		this.name = name;
	}

	/**
	 * Sets the player's health bar colour.
	 * @param healthBar - The health bar colour to set.
	 */
	public void setHealthBarColour(Color healthBar) {
		this.healthBar = healthBar;
	}

	/**
	 * Gets the username of the player.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the fill colour of the player.
	 * @return The fill colour of the player.
	 */
	public Color getFillColour() {
		return fill;
	}

	/**
	 * Gets the outline colour of the player.
	 * @return The outline colour of the player.
	 */
	public Color getOutlineColour() {
		return outline;
	}

	/**
	 * Gets the username colour of the player.
	 * @return The username colour of the player.
	 */
	public Color getUsernameColour() {
		return name;
	}

	/**
	 * Gets the player's health bar colour.
	 * @return The player's health bar colour.
	 */
	public Color getHealthBarColour() {
		return healthBar;
	}
}
