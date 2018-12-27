package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the player shot packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a player who has shot
 * a paintball.
 * @author Ramone Graham
 *
 */
public class Packet03PlayerShot extends Packet {

	private String username;
	private float x, y, velX, velY;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player shooting.
	 * @param X - The source X coordinate of the paintball.
	 * @param Y - The source Y coordinate of the paintball.
	 * @param velX - The X axis velocity of the paintball.
	 * @param velY - The Y axis velocity of the paintball.
	 */
	public Packet03PlayerShot(String username, float x, float y, float velX, float velY) {
		super("03");
		
		this.username = username;
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet03PlayerShot(byte[] data) {
		super ("03");
		
		String[] dataArray = readData(data).split(",");
		
		username = dataArray[0];
		x = Float.parseFloat(dataArray[1]);
		y = Float.parseFloat(dataArray[2]);
		velX = Float.parseFloat(dataArray[3]);
		velY = Float.parseFloat(dataArray[4]);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username + "," + x + "," + y + "," + velX + "," + velY).getBytes();
	}
	
	/**
	 * Gets the username of the player shooting.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the source X coordinate of the paintball.
	 * @return The source X coordinate of the paintball.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Returns the source X coordinate of the paintball.
	 * @return The source Y coordinate of the paintball.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the X axis velocity of the paintball.
	 * @return The X axis velocity of the paintball.
	 */
	public float getVelX() {
		return velX;
	}

	/**
	 * Returns the Y axis velocity of the paintball.
	 * @return The Y axis velocity of the paintball.
	 */
	public float getVelY() {
		return velY;
	}
}
