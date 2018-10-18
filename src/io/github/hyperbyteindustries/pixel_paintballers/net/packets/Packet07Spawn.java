package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import io.github.hyperbyteindustries.pixel_paintballers.ID;

/**
 * Represents the enemy spawn packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a new wave of enemies
 * that have spawned.
 * @author Ramone Graham
 *
 */
public class Packet07Spawn extends Packet {

	private int enemyCount;
	
	private float[] xcoords, ycoords;
	private ID[] ids;
	private int[] enemyNumbers, attackTimes, shootTimes;
	private String[] targets;
	
	/**
	 * Creates a new packet to be sent to the clients.
	 * @param x - The x coordinates of the enemies.
	 * @param y - The y coordinates of the enemies.
	 * @param id - The ID tags of the enemies.
	 * @param enemyNumber - The enemy numbers of the enemies.
	 * @param attackTime - The attack timers for the moving enemies.
	 * @param shootTime - The shoot timers for the enemies.
	 * @param target - The usernames of the enemies' targets.
	 */
	public Packet07Spawn(int enemyCount, float[] xcoords, float[] ycoords, ID[] ids,
			int[] enemyNumbers, int[] attackTimes, int[] shootTimes, String[] targets) {
		super("07");
		
		this.enemyCount = enemyCount;
		this.xcoords = xcoords;
		this.ycoords = ycoords;
		this.ids = ids;
		this.enemyNumbers = enemyNumbers;
		this.attackTimes = attackTimes;
		this.shootTimes = shootTimes;
		this.targets = targets;
	}
	
	/**
	 * Creates a new packet that has been sent to a client.
	 * @param data - The packet data sent.
	 */
	public Packet07Spawn(byte[] data) {
		super("07");
		
		String[] dataArray = readData(data).split(",");
		
		enemyCount = Integer.parseInt(dataArray[0]);
		xcoords = new float[enemyCount];
		ycoords = new float[enemyCount];
		ids = new ID[enemyCount];
		enemyNumbers = new int[enemyCount];
		attackTimes = new int[enemyCount];
		shootTimes = new int[enemyCount];
		targets = new String[enemyCount];
		
		if (enemyCount == 1) {
			xcoords[0] = Float.parseFloat(dataArray[1]);
			ycoords[0] = Float.parseFloat(dataArray[2]);
			ids[0] = ID.valueOf(dataArray[3]);
			enemyNumbers[0] = Integer.parseInt(dataArray[4]);
			attackTimes[0] = Integer.parseInt(dataArray[5]);
			shootTimes[0] = Integer.parseInt(dataArray[6]);
			targets[0] = dataArray[7];
		} else {
			for (int i = 0; i < dataArray[1].split(";").length; i++) {
				xcoords[i] = Float.parseFloat(dataArray[1].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[2].split(";").length; i++) {
				ycoords[i] = Float.parseFloat(dataArray[2].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[3].split(";").length; i++) {
				ids[i] = ID.valueOf(dataArray[3].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[4].split(";").length; i++) {
				enemyNumbers[i] = Integer.parseInt(dataArray[4].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[5].split(";").length; i++) {
				attackTimes[i] = Integer.parseInt(dataArray[5].split(";")[i]);
			}
			
			for (int i = 0; i < dataArray[6].split(";").length; i++) {
				shootTimes[i] = Integer.parseInt(dataArray[6].split(";")[i]);
			}
			
			targets = dataArray[7].split(";");
		}
	}

	// See getData() in Packet.
	public byte[] getData() {
		String packetData = packetID + enemyCount + ",";
		
		for (int i = 0; i < xcoords.length; i++) {
			if (i < xcoords.length-1) packetData = packetData.concat(xcoords[i] + ";");
			else packetData = packetData.concat(xcoords[i] + ",");
		}
		
		for (int i = 0; i < ycoords.length; i++) {
			if (i < ycoords.length-1) packetData = packetData.concat(ycoords[i] + ";");
			else packetData = packetData.concat(ycoords[i] + ",");
		}
		
		for (int i = 0; i < ids.length; i++) {
			if (i < ids.length-1) packetData = packetData.concat(ids[i] + ";");
			else packetData = packetData.concat(ids[i] + ",");
		}
		
		for (int i = 0; i < enemyNumbers.length; i++) {
			if (i < xcoords.length-1) packetData = packetData.concat(enemyNumbers[i] + ";");
			else packetData = packetData.concat(enemyNumbers[i] + ",");
		}
		
		for (int i = 0; i < attackTimes.length; i++) {
			if (i < attackTimes.length-1) packetData = packetData.concat(attackTimes[i] + ";");
			else packetData = packetData.concat(attackTimes[i] + ",");
		}
		
		for (int i = 0; i < shootTimes.length; i++) {
			if (i < shootTimes.length-1) packetData = packetData.concat(shootTimes[i] + ";");
			else packetData = packetData.concat(shootTimes[i] + ",");
		}
		
		for (int i = 0; i < targets.length; i++) {
			if (i < targets.length-1) packetData = packetData.concat(targets[i] + ";");
			else packetData = packetData.concat(targets[i]);
		}
		
		return packetData.getBytes();
	}
	
	/**
	 * Gets the amount of enemies that have spawned.
	 * @return The amount of enemies that have spawned.
	 */
	public int getEnemyCount() {
		return enemyCount;
	}
	
	/**
	 * Gets the x coordinate of an enemy.
	 * @return The enemy's current x coordinate.
	 */
	public float getX(int index) {
		return xcoords[index];
	}
	
	/**
	 * Gets the y coordinate of an enemy.
	 * @return The enemy's current y coordinate.
	 */
	public float getY(int index) {
		return ycoords[index];
	}
	
	/**
	 * Gets the ID tag of an enemy.
	 * @return The enemy's current ID tag.
	 */
	public ID getID(int index) {
		return ids[index];
	}
	
	/**
	 * Gets the enemy number of an enemy.
	 * @return The enemy's current enemy number.
	 */
	public int getEnemyNumber(int index) {
		return enemyNumbers[index];
	}
	
	/**
	 * Gets the attack timer for a moving enemy.
	 * @return The enemy's current attack timer.
	 */
	public int getAttackTime(int index) {
		return attackTimes[index];
	}
	
	/**
	 * Gets the shoot timer for an enemy.
	 * @return The enemy's current shoot timer.
	 */
	public int getShootTime(int index) {
		return shootTimes[index];
	}
	
	/**
	 * Gets the username of the enemy's target.
	 * @return The current username of the enemy's target.
	 */
	public String getTarget(int index) {
		return targets[index];
	}
}
