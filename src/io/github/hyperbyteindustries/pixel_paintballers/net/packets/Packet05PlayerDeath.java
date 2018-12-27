package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the death packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a player who has died.
 * @author Ramone Graham
 *
 */
public class Packet05PlayerDeath extends Packet {

	private String username;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player who has died.
	 */
	public Packet05PlayerDeath(String username) {
		super("05");
		
		this.username = username;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet05PlayerDeath(byte[] data) {
		super ("05");
		
		username = readData(data);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username).getBytes();
	}
	
	/**
	 * Returns the username of the player who died.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
}
