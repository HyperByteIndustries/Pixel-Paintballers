package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import io.github.hyperbyteindustries.pixel_paintballers.ID;

/**
 * Represents the enemy spawn packet of the multiplayer system.
 * When constructed, this class is responsible for communicating data about a new enemy that has
 * spawned.
 * @author Ramone Graham
 *
 */
public class Packet07Spawn extends Packet {

	private int enemyCount;
	
	private float[] x, y;
	private ID[] id;
	private int[] enemyNumber, attackTime, shootTime;
	private String[] target;
	
	/**
	 * Creates a new packet to be sent to the clients.
	 * @param x - The x coordinate of the enemy.
	 * @param y - The y coordinate of the enemy.
	 * @param id - The ID tag of the enemy.
	 * @param enemyNumber - The enemy number of the enemy.
	 * @param attackTime - The attack timer for the moving enemy.
	 * @param shootTime - The shoot timer for the enemy.
	 * @param target - The username of the enemy's target.
	 */
	public Packet07Spawn(int enemyCount, float[] x, float[] y, ID[] id, int[] enemyNumber,
			int[] attackTime, int[] shootTime, String[] target) {
		super("07");
		
		this.enemyCount = enemyCount;
		this.x = x;
		this.y = y;
		this.id = id;
		this.enemyNumber = enemyNumber;
		this.attackTime = attackTime;
		this.shootTime = shootTime;
		this.target = target;
	}
	
	/**
	 * Creates a new packet that has been sent between a client and a server.
	 * @param data - The packet data sent.
	 */
	public Packet07Spawn(byte[] data) {
		super("07");
		
		String[] dataArray = readData(data).split(",");
		
		enemyCount = Integer.parseInt(dataArray[0]);

		x = new float[enemyCount];
		y = new float[enemyCount];
		id = new ID[enemyCount];
		enemyNumber = new int[enemyCount];
		attackTime = new int[enemyCount];
		shootTime = new int[enemyCount];
		target = new String[enemyCount];
		
		if (enemyCount == 1) {
			x[0] = Float.parseFloat(dataArray[1]);
			y[0] = Float.parseFloat(dataArray[2]);
			id[0] = ID.valueOf(dataArray[3]);
			enemyNumber[0] = Integer.parseInt(dataArray[4]);
			attackTime[0] = Integer.parseInt(dataArray[5]);
			shootTime[0] = Integer.parseInt(dataArray[6]);
			target[0] = dataArray[7];
		} else {
			for (int i = 0; i < dataArray[1].split(";").length; i++) {
				x[i] = Float.parseFloat(dataArray[1].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[2].split(";").length; i++) {
				y[i] = Float.parseFloat(dataArray[2].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[3].split(";").length; i++) {
				id[i] = ID.valueOf(dataArray[3].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[4].split(";").length; i++) {
				enemyNumber[i] = Integer.parseInt(dataArray[4].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[5].split(";").length; i++) {
				attackTime[i] = Integer.parseInt(dataArray[5].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[6].split(";").length; i++) {
				shootTime[i] = Integer.parseInt(dataArray[6].split(";")[i]);
			}
			
			target = dataArray[7].split(";");
		}
	}

	// See getData() in Packet.
	public byte[] getData() {
		String packetData = packetID + enemyCount + ",";
		
		for (int i = 0; i < x.length; i++) {
			if (i < x.length-1) packetData = packetData.concat(x[i] + ";");
			else packetData = packetData.concat(x[i] + ",");
		}
		
		for (int i = 0; i < y.length; i++) {
			if (i < y.length-1) packetData = packetData.concat(y[i] + ";");
			else packetData = packetData.concat(y[i] + ",");
		}
		
		for (int i = 0; i < id.length; i++) {
			if (i < id.length-1) packetData = packetData.concat(id[i] + ";");
			else packetData = packetData.concat(id[i] + ",");
		}
		
		for (int i = 0; i < enemyNumber.length; i++) {
			if (i < x.length-1) packetData = packetData.concat(enemyNumber[i] + ";");
			else packetData = packetData.concat(enemyNumber[i] + ",");
		}
		
		for (int i = 0; i < attackTime.length; i++) {
			if (i < attackTime.length-1) packetData = packetData.concat(attackTime[i] + ";");
			else packetData = packetData.concat(attackTime[i] + ",");
		}
		
		for (int i = 0; i < shootTime.length; i++) {
			if (i < shootTime.length-1) packetData = packetData.concat(shootTime[i] + ";");
			else packetData = packetData.concat(shootTime[i] + ",");
		}
		
		for (int i = 0; i < target.length; i++) {
			if (i < target.length-1) packetData = packetData.concat(target[i] + ";");
			else packetData = packetData.concat(target[i]);
		}
		
		return packetData.getBytes();
	}
	
	public int getEnemyCount() {
		return enemyCount;
	}
	
	/**
	 * Gets the x coordinate of the enemy.
	 * @return The current x coordinate.
	 */
	public float getX(int index) {
		return x[index];
	}
	
	/**
	 * Gets the y coordinate of the enemy.
	 * @return The current y coordinate.
	 */
	public float getY(int index) {
		return y[index];
	}
	
	/**
	 * Gets the ID tag of the enemy.
	 * @return The current ID tag.
	 */
	public ID getID(int index) {
		return id[index];
	}
	
	/**
	 * Gets the enemy number of the enemy.
	 * @return The current enemy number.
	 */
	public int getEnemyNumber(int index) {
		return enemyNumber[index];
	}
	
	/**
	 * Gets the attack timer for the moving enemy.
	 * @return The current attack timer.
	 */
	public int getAttackTime(int index) {
		return attackTime[index];
	}
	
	/**
	 * Gets the shoot timer for the enemy.
	 * @return The current shoot timer.
	 */
	public int getShootTime(int index) {
		return shootTime[index];
	}
	
	/**
	 * Gets the username of the enemy's target.
	 * @return The current username of the enemy's target.
	 */
	public String getTarget(int index) {
		return target[index];
	}
}
