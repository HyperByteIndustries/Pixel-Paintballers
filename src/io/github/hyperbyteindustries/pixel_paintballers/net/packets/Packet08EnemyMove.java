package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the enemy movement packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about an enemy who has
 * moved.
 * @author Ramone Graham
 *
 */
public class Packet08EnemyMove extends Packet {

	private int enemyNumber;
	private float x, y;

	/**
	 * Creates a new packet to be sent to clients.
	 * @param enemyNumber - The enemy number of the enemy moving.
	 * @param x - The current x coordinate of the enemy.
	 * @param y - The current y coordinate of the enemy.
	 */
	public Packet08EnemyMove(int enemyNumber, float x, float y) {
		super("08");
		
		this.enemyNumber = enemyNumber;
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new packet that has been sent to a client.
	 * @param data - The packet data sent.
	 */
	public Packet08EnemyMove(byte[] data) {
		super ("08");
		
		String[] dataArray = readData(data).split(",");
		
		enemyNumber = Integer.parseInt(dataArray[0]);
		x = Float.parseFloat(dataArray[1]);
		y = Float.parseFloat(dataArray[2]);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + enemyNumber + "," + x + "," + y).getBytes();
	}
	
	/**
	 * Gets the enemy number of the enemy moving.
	 * @return The enemy number of the enemy.
	 */
	public int getEnemyNumber() {
		return enemyNumber;
	}

	/**
	 * Gets the x coordinate of the enemy.
	 * @return The current x coordinate.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the y coordinate of the enemy.
	 * @return The current y coordinate.
	 */
	public float getY() {
		return y;
	}
}
