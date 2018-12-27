package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the target change packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about an enemy who has
 * changed it's target.
 * @author Ramone Graham
 *
 */
public class Packet09TargetChange extends Packet {

	private int enemyNumber;
	private String target;
	
	/**
	 * Creates a new packet to be sent to clients.
	 * @param enemyNumber - The enemy number of the enemy.
	 * @param target - The username of the new target player.
	 */
	public Packet09TargetChange(int enemyNumber, String target) {
		super("09");
		
		this.enemyNumber = enemyNumber;
		this.target = target;
	}
	
	/**
	 * Creates a new packet that has been sent to a client.
	 * @param data - The packet data sent.
	 */
	public Packet09TargetChange(byte[] data) {
		super("09");
		
		String[] dataArray = readData(data).split(",");
		
		enemyNumber = Integer.parseInt(dataArray[0]);
		target = dataArray[1];
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + enemyNumber + "," + target).getBytes();
	}
	
	/**
	 * Returns the enemy number of the enemy.
	 * @return The enemy number of the enemy.
	 */
	public int getEnemyNumber() {
		return enemyNumber;
	}
	
	/**
	 * Returns the username of the target player.
	 * @return The username of the target player.
	 */
	public String getTarget() {
		return target;
	}
}
