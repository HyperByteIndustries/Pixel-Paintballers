package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import java.awt.Color;

/**
 * Represents the connection packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a new player who has
 * joined the server, or a player who is repsawning after a death.
 * @author Ramone Graham
 *
 */
public class Packet00Connect extends Packet {

	private String username;
	private float x, y;
	private Color fillColour, outlineColour, usernameColour;
	private int health;
	private boolean alreadyConnected, spectator;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player.
	 * @param x - The X coordinate of the player.
	 * @param y - The Y coordinate of the player.
	 * @param fillColour - The fill colour of the player.
	 * @param outlineColour - The outline colour of the player.
	 * @param usernameColour - The username colour of the player.
	 * @param health - The health of the player.
	 * @param alreadyConnected - Determines whether a player has connected already or not.
	 * @param spectator - Determines whether a player is a spectator or not.
	 */
	public Packet00Connect(String username, float x, float y, Color fillColour,
			Color outlineColour, Color usernameColour, int health, boolean alreadyConnected,
			boolean spectator) {
		super("00");
		
		this.username = username;
		this.x = x;
		this.y = y;
		this.fillColour = fillColour;
		this.outlineColour = outlineColour;
		this.usernameColour = usernameColour;
		this.health = health;
		this.alreadyConnected = alreadyConnected;
		this.spectator = spectator;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet00Connect(byte[] data) {
		super ("00");
		
		String[] dataArray = readData(data).split(",");
		
		username = dataArray[0];
		x = Float.parseFloat(dataArray[1]);
		y = Float.parseFloat(dataArray[2]);
		fillColour = new Color(Integer.parseInt(dataArray[3]));
		outlineColour = new Color(Integer.parseInt(dataArray[4]));
		usernameColour = new Color(Integer.parseInt(dataArray[5]));
		health = Integer.parseInt(dataArray[6]);
		alreadyConnected = Integer.parseInt(dataArray[7]) == 1;
		spectator = Integer.parseInt(dataArray[8]) == 1;
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username + "," + x + "," + y + "," + fillColour.getRGB() + "," +
				outlineColour.getRGB() + "," + usernameColour.getRGB() + "," + health + "," +
				(alreadyConnected?1:0) + "," + (spectator?1:0)).getBytes();
	}
	
	/**
	 * Returns the username of the player connecting to the server.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the X coordinate of the player.
	 * @return The X coordinate of the player.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Returns the Y coordinate of the player.
	 * @return The Y coordinate of the player.
	 */
	public float getY() {
		return y;
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
	
	/**
	 * Returns the health of the connecting player.
	 * @return The player's health.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Checks to see if a player has connected to the server.
	 * @return <code>true</code> if a player has connected, otherwise <code>false</code>.
	 */
	public boolean isAlreadyConnected() {
		return alreadyConnected;
	}
	
	/**
	 * Returns whether a player is a spectator or not
	 * @return <code>true</code> if a player is a spectator, otherwise <code>false</code>.
	 */
	public boolean isSpectator() {
		return spectator;
	}
}
