package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.net.InetAddress;

import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Player;

/**
 * Represents a player who has connected to a multiplayer server.
 * When constructed, this class is responsible for the management of the player in a multiplayer
 * system.
 * @author Ramone Graham
 *
 */
public class IPlayer extends Player {

	private InetAddress ipAddress;
	private int port;
	
	/**
	 *Creates a new player.
	 * @param x - The x coordinate of the player.
	 * @param y - The y coordinate of the player.
	 * @param id - The ID tag of the player.
	 * @param username - The username of the player.
	 * @param ipAddress - The IP address of the player's system.
	 * @param port - The port of the player's system.
	 */
	public IPlayer(float x, float y, ID id, String username, InetAddress ipAddress, int port) {
		super(x, y, id, null, username);
		
		this.ipAddress = ipAddress;
		this.port = port;
	}

	/**
	 * Sets the source IP address of the player.
	 * @param ipAddress - The IP address to be set.
	 */
	public void setIPAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Sets the source port of the player.
	 * @param port - The port to be set.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the current source IP address of the player.
	 * @return The player's IP address.
	 */
	public InetAddress getIPAddress() {
		return ipAddress;
	}

	/**
	 * Gets the current source port of the player.
	 * @return The player's port.
	 */
	public int getPort() {
		return port;
	}
}
