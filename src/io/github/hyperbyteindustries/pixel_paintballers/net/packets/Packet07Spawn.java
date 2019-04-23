package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;

/**
 * Represents the enemy spawn packet of the game's multiplayer system.
 * When constructed, this class is responsible for communicating data about a new wave of enemies
 * that have spawned.
 * @author Ramone Graham
 *
 */
public class Packet07Spawn extends Packet {

	private int enemyCount;
	private ID[] ids;
	private float[] xcoords, ycoords;
	private int[] enemyNumbers;
	private long[] attackTimers, shootTimers;
	private String[] targets;
	
	/**
	 * Creates a new packet to be sent to the clients.
	 * @param x - The X coordinates of the enemies.
	 * @param y - The Y coordinates of the enemies.
	 * @param id - The identification tags of the enemies.
	 * @param enemyNumber - The enemy numbers of the enemies.
	 * @param attackTime - The attack timers for the moving enemies.
	 * @param shootTime - The shoot timers for the enemies.
	 * @param target - The usernames of the enemies' targets.
	 */
	public Packet07Spawn(int enemyCount, ID[] ids, float[] xcoords, float[] ycoords, int[] enemyNumbers, long[] attackTimers,
			long[] shootTimers, String[] targets) {
		super("07");
		
		this.enemyCount = enemyCount;
		this.ids = ids;
		this.xcoords = xcoords;
		this.ycoords = ycoords;
		this.enemyNumbers = enemyNumbers;
		this.attackTimers = attackTimers;
		this.shootTimers = shootTimers;
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
		attackTimers = new long[enemyCount];
		shootTimers = new long[enemyCount];
		targets = new String[enemyCount];
		
		if (enemyCount == 1) {
			xcoords[0] = Float.parseFloat(dataArray[1]);
			ycoords[0] = Float.parseFloat(dataArray[2]);
			ids[0] = ID.valueOf(dataArray[3]);
			enemyNumbers[0] = Integer.parseInt(dataArray[4]);
			attackTimers[0] = Long.parseLong(dataArray[5]);
			shootTimers[0] = Long.parseLong(dataArray[6]);
			targets[0] = dataArray[7];
		} else {
			for (int i = 0; i < dataArray[1].split(";").length; i++)
				xcoords[i] = Float.parseFloat(dataArray[1].split(";")[i]);
			
			for (int i = 0; i < dataArray[2].split(";").length; i++)
				ycoords[i] = Float.parseFloat(dataArray[2].split(";")[i]);
			
			for (int i = 0; i < dataArray[3].split(";").length; i++)
				ids[i] = ID.valueOf(dataArray[3].split(";")[i]);
			
			for (int i = 0; i < dataArray[4].split(";").length; i++)
				enemyNumbers[i] = Integer.parseInt(dataArray[4].split(";")[i]);
			
			for (int i = 0; i < dataArray[5].split(";").length; i++)
				attackTimers[i] = Long.parseLong(dataArray[5].split(";")[i]);
			
			for (int i = 0; i < dataArray[6].split(";").length; i++)
				shootTimers[i] = Long.parseLong(dataArray[6].split(";")[i]);
			
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
		
		for (int i = 0; i < attackTimers.length; i++) {
			if (i < attackTimers.length-1) packetData = packetData.concat(attackTimers[i] + ";");
			else packetData = packetData.concat(attackTimers[i] + ",");
		}
		
		for (int i = 0; i < shootTimers.length; i++) {
			if (i < shootTimers.length-1) packetData = packetData.concat(shootTimers[i] + ";");
			else packetData = packetData.concat(shootTimers[i] + ",");
		}
		
		for (int i = 0; i < targets.length; i++) {
			if (i < targets.length-1) packetData = packetData.concat(targets[i] + ";");
			else packetData = packetData.concat(targets[i]);
		}
		
		return packetData.getBytes();
	}
	
	/**
	 * Returns the amount of enemies that have spawned.
	 * @return The amount of enemies that have spawned.
	 */
	public int getEnemyCount() {
		return enemyCount;
	}
	
	/**
	 * Returns the X coordinate of an enemy.
	 * @param The index corresponding to a certain enemy.
	 * @return The enemy's X coordinate.
	 */
	public float getX(int index) {
		return xcoords[index];
	}
	
	/**
	 * Returns the Y coordinate of an enemy.
	 * @param The index corresponding to a certain enemy.
	 * @return The enemy's Y coordinate.
	 */
	public float getY(int index) {
		return ycoords[index];
	}
	
	/**
	 * Returns the ID tag of an enemy.
	 * @param The index corresponding to a certain enemy.
	 * @return The enemy's ID tag.
	 */
	public ID getID(int index) {
		return ids[index];
	}
	
	/**
	 * Returns the enemy number of an enemy.
	 * @param The index corresponding to a certain enemy.
	 * @return The enemy's enemy number.
	 */
	public int getEnemyNumber(int index) {
		return enemyNumbers[index];
	}
	
	/**
	 * Returns the attack timer for a moving enemy.
	 * @param The index corresponding to a certain enemy.
	 * @return The enemy's attack timer.
	 */
	public long getAttackTimer(int index) {
		return attackTimers[index];
	}
	
	/**
	 * Returns the shoot timer for an enemy.
	 * @param The index corresponding to a certain enemy.
	 * @return The enemy's shoot timer.
	 */
	public long getShootTimer(int index) {
		return shootTimers[index];
	}
	
	/**
	 * Returns the username of the enemy's target.
	 * @param The index corresponding to a certain enemy.
	 * @return The username of the enemy's target.
	 */
	public String getTarget(int index) {
		return targets[index];
	}
}
