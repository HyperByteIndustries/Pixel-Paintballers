package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the level up packet in the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a level up.
 * @author Ramone Graham
 *
 */
public class Packet06LevelUp extends Packet {

	private int level;
	
	/**
	 * Creates a new packet to be sent to clients.
	 * @param level - The new level of the game.
	 */
	public Packet06LevelUp(int level) {
		super("06");
		
		this.level = level;
	}
	
	/**
	 * Creates a new packet that has been sent to a client.
	 * @param data - The packet data sent.
	 */
	public Packet06LevelUp(byte[] data) {
		super("06");
		
		level = Integer.parseInt(readData(data));
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + level).getBytes();
	}
	
	/**
	 * Returns the new level of the game.
	 * @return The new level.
	 */
	public int getLevel() {
		return level;
	}
}
