package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import io.github.hyperbyteindustries.pixel_paintballers.net.Client;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;

/**
 * Represents the damage packet of the multiplayer system.
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
	 * @param username - The username of the player is taking damage.
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
		super ("04");
		
		String[] dataArray = readData(data).split(",");
		
		username = dataArray[0];
		damageTaken = Integer.parseInt(dataArray[1]);
	}

	// See writeData(Client client) in Packet.
	public void writeData(Client client) {
		client.sendData(getData());
	}

	// See writeData(Server server) in Packet.
	public void writeData(Server server) {
		server.sendDataToAll(getData());
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + username + "," + damageTaken).getBytes();
	}
	
	/**
	 * Gets the username of the player is taking damage.
	 * @return The username of the player.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the amount of damage taken by a player.
	 * @return The damage taken.
	 */
	public int getDamageTaken() {
		return damageTaken;
	}
}
