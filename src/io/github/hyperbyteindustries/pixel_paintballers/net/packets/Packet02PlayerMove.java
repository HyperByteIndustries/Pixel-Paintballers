package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the player movement packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a player who is moving
 * or stationary.
 * @author Ramone Graham
 *
 */
public class Packet02PlayerMove extends Packet {

	private String username;
	private boolean moving;
	private float varX, varY;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player moving.
	 * @param moving - Whether the player is moving or stationary.
	 * @param varX - The X axis velocity of the player if moving, else the X coordinate.
	 * @param varY - The Y axis velocity of the player if moving, else the Y coordinate.
	 */
	public Packet02PlayerMove(String username, boolean moving, float varX, float varY) {
		super("02");
		
		this.username = username;
		this.moving = moving;
		this.varX = varX;
		this.varY = varY;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet02PlayerMove(byte[] data) {
		super ("02");
		
		String[] dataArray = readData(data).split(",");
		
		username = dataArray[0];
		moving = Integer.parseInt(dataArray[1]) == 1;
		varX = Float.parseFloat(dataArray[2]);
		varY = Float.parseFloat(dataArray[3]);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username + "," + (moving?1:0) + "," + varX + "," + varY).getBytes();
	}
	
	/**
	 * Returns the username of the player moving.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns whether the player is moving.
	 * @return Whether the player is moving.
	 */
	public boolean isPlayerMoving() {
		return moving;
	}

	/**
	 * Returns the variable determining the player's location on the X axis.
	 * @return The X axis velocity of the player if moving, else the X coordinate.
	 */
	public float getVarX() {
		return varX;
	}

	/**
	 * Returns the variable determining the player's location on the Y axis.
	 * @return The Y axis velocity of the player if moving, else the Y coordinate.
	 */
	public float getVarY() {
		return varY;
	}
}
