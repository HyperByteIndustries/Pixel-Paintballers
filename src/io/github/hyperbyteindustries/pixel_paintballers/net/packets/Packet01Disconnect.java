package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the disconnection packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a player who has left
 * the server.
 * @author Ramone Graham
 *
 */
public class Packet01Disconnect extends Packet {

	private String username;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player disconnecting from the server.
	 */
	public Packet01Disconnect(String username) {
		super("01");
		
		this.username = username;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet01Disconnect(byte[] data) {
		super ("01");
		
		username = readData(data);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username).getBytes();
	}
	
	/**
	 * Returns the username of the player disconnecting from the server.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}
}
