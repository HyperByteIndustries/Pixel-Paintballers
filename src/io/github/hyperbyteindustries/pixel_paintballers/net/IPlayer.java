package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.awt.Color;
import java.net.InetAddress;

import io.github.hyperbyteindustries.pixel_paintballers.entities.Player;

/**
 * Represents an online player in the game's multiplayer system.
 * When constructed, this class is responsible for the online management of the player.
 * @author Ramone Graham
 *
 */
public class IPlayer extends Player {

	private InetAddress ipAddress;
	private int port;
	
	/**
	 * Creates a new player.
	 * @param x - The X coordinate of the player.
	 * @param y - The Y coordinate of the player.
	 * @param id - The identification tag of the player.
	 * @param username - The username of the player.
	 * @param fillColour - The fill colour of the player.
	 * @param outlineColour - The outline colour of the player.
	 * @param usernameColour - The username colour of the player.
	 * @param ipAddress - The IP address of the player's system.
	 * @param port - The port of the player's system.
	 */
	public IPlayer(float x, float y, String username, Color fillColour, Color outlineColour,
			Color usernameColour, InetAddress ipAddress, int port) {
		super(x, y, null, username, fillColour, outlineColour, usernameColour);
		
		id = ID.IPLAYER;
		
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
	 * Sets the player to either a spectator or not.
	 * @param spectator - Whether the player is a spectator or not.
	 */
	public void setSpectator(boolean spectator) {
		this.spectator = spectator;
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

	/**
	 * Gets whether the player is a spectator or not
	 * @return <code>true</code> is the player is a spectator, else <code>false</code>.
	 */
	public boolean isSpectator() {
		return spectator;
	}
}
