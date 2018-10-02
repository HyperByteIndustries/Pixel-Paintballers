package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

/**
 * Represents the enemy movement packet of the multiplayer system.
 * When constructed, this class is responsible for communicating data about an enemy who has
 * moved.
 * @author Ramone Graham
 *
 */
public class Packet10EnemyMove extends Packet {

	private int enemyNumber;
	private float velX, velY;

	/**
	 * Creates a new packet to be sent between a client and a server.
	 * @param enemyNumber - The enemy number of the enemy moving.
	 * @param varX - The velocity of the enemy across the x-axis if moving, else the x coordinate
	 * of the enemy.
	 * @param varY - The velocity of the enemy across the y-axis if moving, else the y coordinate
	 * of the enemy.
	 */
	public Packet10EnemyMove(int enemyNumber, float velX, float velY) {
		super("10");
		
		this.enemyNumber = enemyNumber;
		this.velX = velX;
		this.velY = velY;
	}

	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet10EnemyMove(byte[] data) {
		super ("10");
		
		String[] dataArray = readData(data).split(",");
		
		enemyNumber = Integer.parseInt(dataArray[0]);
		velX = Float.parseFloat(dataArray[1]);
		velY = Float.parseFloat(dataArray[2]);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + enemyNumber + "," + velX + "," + velY).getBytes();
	}
	
	/**
	 * Gets the enemy number of the enemy moving.
	 * @return The enemy number of the enemy.
	 */
	public int getEnemyNumber() {
		return enemyNumber;
	}

	/**
	 * Gets the variable determining the enemy's location on the x-axis.
	 * @return The velocity of the enemy across the x-axis if moving, else the x coordinate of
	 * the enemy.
	 */
	public float getVelX() {
		return velX;
	}

	/**
	 * Gets the variable determining the enemy's location on the y-axis.
	 * @return The velocity of the enemy across the y-axis if moving, else the y coordinate of
	 * the enemy.
	 */
	public float getVelY() {
		return velY;
	}
}
