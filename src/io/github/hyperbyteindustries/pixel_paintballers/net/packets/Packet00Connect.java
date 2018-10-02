package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the connection packet of the multiplayer system.
 * When constructed, this class is responsible for communicating data about a new player who has
 * joined the server.
 * @author Ramone Graham
 *
 */
public class Packet00Connect extends Packet {

	private String username;
	private float x, y;
	private int health;
	private boolean alreadyConnected;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player connecting to the server.
	 * @param x - The x coordinate of the player.
	 * @param y - The y coordinate of the player.
	 * @param health - The health of the player.
	 * @param alreadyConnected - The status of a player in relation to the server.
	 */
	public Packet00Connect(String username, float x, float y, int health,
			boolean alreadyConnected) {
		super("00");
		
		this.username = username;
		this.x = x;
		this.y = y;
		this.health = health;
		this.alreadyConnected = alreadyConnected;
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
		health = Integer.parseInt(dataArray[3]);
		alreadyConnected = Integer.parseInt(dataArray[4]) == 1;
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username + "," + x + "," + y + "," + health + "," +
				(alreadyConnected?1:0)).getBytes();
	}
	
	/**
	 * Gets the username of the player connecting to the server.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets the x coordinate of the connecting player.
	 * @return The x coordinate of the player.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the y coordinate of the connecting player.
	 * @return The y coordinate of the player.
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Gets the health of the connecting player.
	 * @return The player's health.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Checks to see if a player has connected to the server.
	 * @return - True if a player has connected, otherwise false.
	 */
	public boolean isAlreadyConnected() {
		return alreadyConnected;
	}
}
