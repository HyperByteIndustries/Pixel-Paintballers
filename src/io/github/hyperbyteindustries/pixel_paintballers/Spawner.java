package io.github.hyperbyteindustries.pixel_paintballers;

import java.util.LinkedList;
import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.net.IEnemy;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet07Spawn;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet06LevelUp;

/**
 * Represents the spawning system of the game. When constructed, this class is
 * responsible for the spawning of enemy AI.
 * @author Ramone Graham
 *
 */
public class Spawner {

	private Game game;
	private Handler handler;
	private Server server;
	
	private Random random;
	
	public int level = 0;
	
	/**
	 * Creates a new instance of the spawner.
	 * @param game - An instance of the Game class, used to spawn enemies.
	 * @param handler - An instance of the Handler class, used to spawn enemies.
	 */
	public Spawner(Game game, Handler handler, Server server) {
		this.game = game;
		this.handler = handler;
		this.server = server;
		random = new Random();
	}
	
	/**
	 * Updates the logic of the spawner.
	 */
	public void tick() {
		int players = 0, enemies = 0;
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) players++;
			else if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
					tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
					ID.HOMINGENEMY) enemies++;
		}
		
		if (players >= 2 && enemies == 0) {
			if (level == 0)  spawnEnemy();
			else if (level == 1) for (int i = 0; i < 2; i++) spawnEnemy();
			else if (level == 2) for (int i = 0; i < 3; i++) spawnEnemy();
			else if (level == 3) for (int i = 0; i < 4; i++) spawnEnemy();
			else if (level == 4) for (int i = 0; i < 5; i++) spawnEnemy();
			else if (level == 5) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				spawnMovingEnemy();
			} else if (level == 6) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
			} else if (level == 7) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
			} else if (level == 8) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 4; i++) spawnMovingEnemy();
			} else if (level == 9) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 5; i++) spawnMovingEnemy();
			}  else if (level == 10) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				spawnBouncyEnemy();
			} else if (level == 11) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
			} else if (level == 12) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 3; i++) spawnBouncyEnemy();
			} else if (level == 13) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 4; i++) spawnBouncyEnemy();
			} else if (level == 14) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 4; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
			} else if (level == 15) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				spawnHomingEnemy();
			} else if (level == 16) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				for (int i = 0; i < 2; i++) spawnHomingEnemy();
			} else if (level == 17) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 3; i++) spawnBouncyEnemy();
				for (int i = 0; i < 3; i++) spawnHomingEnemy();
			} else if (level == 18) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 3; i++) spawnBouncyEnemy();
				for (int i = 0; i < 4; i++) spawnHomingEnemy();
			} else if (level == 19) {
				for (int i = 0; i < 2; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 4; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 20) {
				for (int i = 0; i < 5; i++) spawnEnemy();
				for (int i = 0; i < 5; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 21) {
				for (int i = 0; i < 6; i++) spawnEnemy();
				for (int i = 0; i < 5; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 22) {
				for (int i = 0; i < 7; i++) spawnEnemy();
				for (int i = 0; i < 6; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 23) {
				for (int i = 0; i < 8; i++) spawnEnemy();
				for (int i = 0; i < 7; i++) spawnMovingEnemy();
				for (int i = 0; i < 6; i++) spawnBouncyEnemy();
				for (int i = 0; i < 5; i++) spawnHomingEnemy();
			} else if (level == 24) {
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
				
				if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
						tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
						ID.HOMINGENEMY) {
					enemyList.add((IEnemy) tempObject);
				}
			}
			
			float[] x = new float[enemyList.size()], y = new float[enemyList.size()];
			ID[] id = new ID[enemyList.size()];
			int[] enemyNumber = new int[enemyList.size()], attackTime =
					new int[enemyList.size()], shootTime = new int[enemyList.size()];
			String[] target = new String[enemyList.size()];
			
			for (int i = 0; i < enemyList.size(); i++) {
				IEnemy enemy = enemyList.get(i);
				
				x[i] = enemy.getX();
				y[i] = enemy.getY();
				id[i] = enemy.getID();
				enemyNumber[i] = enemy.getEnemyNumber();
				attackTime[i] = enemy.attackTime;
				shootTime[i] = enemy.shootTime;
				target[i] = enemy.getTarget().getUsername();
			}
			
			level++;
			
			Packet06LevelUp levelUpPacket = new Packet06LevelUp(level);
			levelUpPacket.writeData(server);
			
			Packet07Spawn spawnPacket = new Packet07Spawn(enemyList.size(), x, y, id,
					enemyNumber, attackTime, shootTime, target);
			spawnPacket.writeData(server);
		}
	}
	
	/**
	 * Spawns a basic enemy.
	 */
	private void spawnEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.XBOUND-25), random.nextInt(Game.YBOUND-25),
				ID.ENEMY, game, handler, server, generateEnemyID());
		
		handler.addObject(enemy);
	}

	/**
	 * Spawns an enemy that will move towards the player.
	 */
	private void spawnMovingEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.XBOUND-25), random.nextInt(Game.YBOUND-25),
				ID.MOVINGENEMY, game, handler, server, generateEnemyID());
		
		handler.addObject(enemy);
	}
	
	/**
	 * Spawns an enemy that will fire bouncy paintballs.
	 */
	private void spawnBouncyEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.XBOUND-25), random.nextInt(Game.YBOUND-25),
				ID.BOUNCYENEMY, game, handler, server, generateEnemyID());
		
		handler.addObject(enemy);
	}
	
	/**
	 * Spawns an enemy that will fire homing paintballs.
	 */
	private void spawnHomingEnemy() {
		IEnemy enemy = new IEnemy(random.nextInt(Game.XBOUND-25), random.nextInt(Game.YBOUND-25),
				ID.HOMINGENEMY, game, handler, server, generateEnemyID());
		
		handler.addObject(enemy);
	}
	
	private int generateEnemyID() {
		int enemyID = random.nextInt();
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
					tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
					ID.HOMINGENEMY) {
				IEnemy enemy = (IEnemy) tempObject;
				
				if (enemy.getEnemyNumber() == enemyID) enemyID = generateEnemyID();
			}
		}
		
		return enemyID;
	}
}
