package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.util.LinkedList;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.net.IEnemy;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet07Spawn;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet06LevelUp;

/**
 * Represents the spawning system of the game's multiplayer system.
 * When constructed, this class is responsible for the spawning of enemy AI.
 * @author Ramone Graham
 *
 */
public class ServerSpawner {

	private Handler handler;
	private Server server;
	
	private Random random = new Random();
	
	private boolean gameStarted = false;
	
	public int level = 0;
	
	/**
	 * Creates a new instance of the spawner.
	 * @param handler - An instance of the Handler class, used to spawn enemies.
	 * @param server - An instance of the Server class, used to send packets.
	 */
	public ServerSpawner(Handler handler, Server server) {
		this.handler = handler;
		this.server = server;
	}
	
	/**
	 * Updates the logic of the spawner.
	 */
	public void tick() {
		int players = 0, enemies = 0;
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) players++;
			else if (tempObject.getID() == ID.IENEMY || tempObject.getID() == ID.IMOVINGENEMY ||
					tempObject.getID() == ID.IBOUNCYENEMY || tempObject.getID() ==
					ID.IHOMINGENEMY) enemies++;
		}
		
		if (players > 0 && enemies == 0) {
			if (level == 0 || gameStarted) {
				level++;
				
				Packet06LevelUp levelUpPacket = new Packet06LevelUp(level);
				levelUpPacket.writeData(server);
			}
			
			if (level == 1) {
				if (players >= 2) {
					spawnEnemy();
					
					gameStarted = true;
				}
			} else if (level == 2) for (int i = 0; i < 2; i++) spawnEnemy();
			else if (level == 3) for (int i = 0; i < 3; i++) spawnEnemy();
			else if (level == 4) for (int i = 0; i < 4; i++) spawnEnemy();
			else if (level == 5) for (int i = 0; i < 5; i++) spawnEnemy();
			else if (level == 6) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				spawnMovingEnemy();
			} else if (level == 7) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
			} else if (level == 8) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
			} else if (level == 9) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 4; i++) spawnMovingEnemy();
			} else if (level == 10) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 5; i++) spawnMovingEnemy();
			}  else if (level == 11) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				spawnBouncyEnemy();
			} else if (level == 12) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
			} else if (level == 13) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 3; i++) spawnBouncyEnemy();
			} else if (level == 14) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 4; i++) spawnBouncyEnemy();
			} else if (level == 15) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 4; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
			} else if (level == 16) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				spawnHomingEnemy();
			} else if (level == 17) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				for (int i = 0; i < 2; i++) spawnHomingEnemy();
			} else if (level == 18) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				for (int i = 0; i < 3; i++) spawnHomingEnemy();
			} else if (level == 19) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 3; i++) spawnBouncyEnemy();
				for (int i = 0; i < 4; i++) spawnHomingEnemy();
			} else if (level == 20) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 4; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 21) {
				for (int i = 0; i < 5; i++) spawnEnemy();
				for (int i = 0; i < 5; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 22) {
				for (int i = 0; i < 6; i++) spawnEnemy();
				for (int i = 0; i < 5; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 23) {
				for (int i = 0; i < 7; i++) spawnEnemy();
				for (int i = 0; i < 6; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 24) {
				for (int i = 0; i < 8; i++) spawnEnemy();
				for (int i = 0; i < 7; i++) spawnMovingEnemy();
				for (int i = 0; i < 6; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 25) {
				for (int i = 0; i < 9; i++) spawnEnemy();
				for (int i = 0; i < 8; i++) spawnMovingEnemy();
				for (int i = 0; i < 7; i++) spawnBouncyEnemy();
				for (int i = 0; i < 6; i++) spawnHomingEnemy();
			} else {
				for (int i = 0; i < 10; i++) spawnEnemy();
				for (int i = 0; i < 9; i++) spawnMovingEnemy();
				for (int i = 0; i < 8; i++) spawnBouncyEnemy();
				for (int i = 0; i < 7; i++) spawnHomingEnemy();
			}
			
			LinkedList<IEnemy> enemyList = new LinkedList<IEnemy>();
			
			for (int i = 0; i < handler.getObjects().size(); i++) {
				GameObject tempObject = handler.getObjects().get(i);
				
				if (tempObject.getID() == ID.IENEMY || tempObject.getID() == ID.IMOVINGENEMY ||
						tempObject.getID() == ID.IBOUNCYENEMY || tempObject.getID() ==
						ID.IHOMINGENEMY) {
					enemyList.add((IEnemy) tempObject);
				}
			}
			
			if (enemyList.size() >= 1) {
				float[] xCoords = new float[enemyList.size()],
						yCoords = new float[enemyList.size()];
				ID[] ids = new ID[enemyList.size()];
				int[] enemyNumbers = new int[enemyList.size()];
				long[] attackTimers = new long[enemyList.size()],
						shootTimers = new long[enemyList.size()];
				String[] targets = new String[enemyList.size()];
				
				for (int i = 0; i < enemyList.size(); i++) {
					IEnemy enemy = enemyList.get(i);
					
					xCoords[i] = enemy.getX();
					yCoords[i] = enemy.getY();
					ids[i] = enemy.getID();
					enemyNumbers[i] = enemy.getEnemyNumber();
					attackTimers[i] = enemy.attackTimer;
					shootTimers[i] = enemy.shootTimer;
					targets[i] = enemy.getTarget().getUsername();
				}
				
				Packet07Spawn spawnPacket = new Packet07Spawn(enemyList.size(), xCoords, yCoords,
						ids, enemyNumbers, attackTimers, shootTimers, targets);
				spawnPacket.writeData(server);
			}
		}
	}
	
	/**
	 * Spawns a basic enemy.
	 */
	private void spawnEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.WIDTH-25), random.nextInt(Game.HEIGHT-25),
				ID.IENEMY, handler, server, generateEnemyNumber());
		
		handler.addObject(enemy);
	}

	/**
	 * Spawns an enemy that will move towards the player.
	 */
	private void spawnMovingEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.WIDTH-25), random.nextInt(Game.HEIGHT-25),
				ID.IMOVINGENEMY, handler, server, generateEnemyNumber());
		
		handler.addObject(enemy);
	}
	
	/**
	 * Spawns an enemy that will fire bouncy paintballs.
	 */
	private void spawnBouncyEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.WIDTH-25), random.nextInt(Game.HEIGHT-25),
				ID.IBOUNCYENEMY, handler, server, generateEnemyNumber());
		
		handler.addObject(enemy);
	}
	
	/**
	 * Spawns an enemy that will fire homing paintballs.
	 */
	private void spawnHomingEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.WIDTH-25), random.nextInt(Game.HEIGHT-25),
				ID.IHOMINGENEMY, handler, server, generateEnemyNumber());
		
		handler.addObject(enemy);
	}
	
	/**
	 * Gives online enemies a wave-unique number that corresponds to them.
	 * @return A randomly generated enemy number.
	 */
	private int generateEnemyNumber() {
		int enemyNumber = random.nextInt();
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IENEMY || tempObject.getID() == ID.IMOVINGENEMY ||
					tempObject.getID() == ID.IBOUNCYENEMY || tempObject.getID() ==
					ID.IHOMINGENEMY) {
				IEnemy enemy = (IEnemy) tempObject;
				
				if (enemy.getEnemyNumber() == enemyNumber) enemyNumber = generateEnemyNumber();
			}
		}
		
		return enemyNumber;
	}
}
