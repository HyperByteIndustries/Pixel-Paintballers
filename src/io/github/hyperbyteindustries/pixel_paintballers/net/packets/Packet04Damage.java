package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the damage packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a player who has taken
 * damage from a paintball.
 * @author Ramone Graham
 *
 */
public class Packet04Damage extends Packet {

	private String username;
	private int damageTaken;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param username - The username of the player taking damage.
	 * @param damageTaken - The amount of damage taken.
	 */
	public Packet04Damage(String username, int damageTaken) {
		super("04");
		
		this.username = username;
		this.damageTaken = damageTaken;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet04Damage(byte[] data) {
		super("04");
		
		String[] dataArray = readData(data).split(",");
		
		username = dataArray[0];
		damageTaken = Integer.parseInt(dataArray[1]);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username + "," + damageTaken).getBytes();
	}
	
	/**
	 * Returns the username of the player taking damage.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the amount of damage taken by a player.
	 * @return The damage taken.
	 */
	public int getDamageTaken() {
		return damageTaken;
	}
}
