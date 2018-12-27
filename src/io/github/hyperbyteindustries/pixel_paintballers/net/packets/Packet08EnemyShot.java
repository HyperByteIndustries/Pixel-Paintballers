package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import io.github.hyperbyteindustries.pixel_paintballers.ID;

/**
 * Represents the enemy shot packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about an enemy who has shot
 * a paintball.
 * @author Ramone Graham
 *
 */
public class Packet08EnemyShot extends Packet {

	private int enemyNumber;
	private String target;
	private ID id;
	private float x, y, velX, velY;
	
	/**
	 * Creates a new packet to be sent to clients.
	 * @param enemyNumber - The enemy number of the enemy shooting.
	 * @param target - The username of the targeted player.
	 * @param id - The ID tag of the paintball.
	 * @param x - The source X coordinate of the paintball.
	 * @param y - The source Y coordinate of the paintball.
	 * @param velX - The x-axis velocity of the paintball.
	 * @param velY - The y-axis velocity of the paintball.
	 */
	public Packet08EnemyShot(int enemyNumber, String target, ID id, float x, float y, float velX,
			float velY) {
		super("08");
		
		this.enemyNumber = enemyNumber;
		this.target = target;
		this.id = id;
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
	}
	
	/**
	 * Creates a new packet that has been sent to a client.
	 * @param data - The packet data sent.
	 */
	public Packet08EnemyShot(byte[] data) {
		super("08");
		
		String[] dataArray = readData(data).split(",");
		
		enemyNumber = Integer.parseInt(dataArray[0]);
		target = dataArray[1];
		id = ID.valueOf(dataArray[2]);
		x = Float.parseFloat(dataArray[3]);
		y = Float.parseFloat(dataArray[4]);
		velX = Float.parseFloat(dataArray[5]);
		velY = Float.parseFloat(dataArray[6]);
	}

	// See getData() in Packet.
	public byte[] getData() {
		return (packetID + enemyNumber + "," + target + "," + id.name() + "," + x + "," + y +
				"," + velX + "," + velY).getBytes();
	}
	
	/**
	 * Returns the enemy number of the enemy.
	 * @return The current enemy number.
	 */
	public int getEnemyNumber() {
		return enemyNumber;
	}
	
	/**
	 * Returns the username of the targeted player.
	 * @return The username of the player.
	 */
	public String getTarget() {
		return target;
	}
	
	/**
	 * Returns the ID tag of the paintball.
	 * @return The current ID tag of the paintball.
	 */
	public ID getID() {
		return id;
	}
	
	/**
	 * Returns the source X coordinate of the paintball.
	 * @return The source X coordinate of the paintball.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Returns the source Y coordinate of the paintball.
	 * @return The source Y coordinate of the paintball.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the X axis velocity of the paintball.
	 * @return The X axis velocity of the paintball.
	 */
	public float getVelX() {
		return velX;
	}

	/**
	 * Returns the Y axis velocity of the paintball.
	 * @return The Y axis velocity of the paintball.
	 */
	public float getVelY() {
		return velY;
	}
}
