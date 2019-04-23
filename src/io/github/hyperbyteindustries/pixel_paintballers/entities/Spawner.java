package io.github.hyperbyteindustries.pixel_paintballers.entities;

import java.util.Random;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;

/**
 * Represents the spawning system of the game.
 * When constructed, this class is responsible for the spawning of enemy AI.
 * @author Ramone Graham
 *
 */
public class Spawner {

	private Game game;
	private Handler handler;
	
	private Random random = new Random();
	
	public static int level = 0;
	
	/**
	 * Creates a new instance of the spawner.
	 * @param game - An instance of the Game class, used to spawn enemies.
	 * @param handler - An instance of the Handler class, used to spawn enemies.
	 */
	public Spawner(Game game, Handler handler) {
		this.game = game;
		this.handler = handler;
	}
	
	/**
	 * Updates the logic of the spawner.
	 */
	public void tick() {
		int enemies = 0;
		
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity entity = handler.getEntities().get(i);
			
			if (entity.getID() == ID.ENEMY || entity.getID() == ID.MOVINGENEMY ||
					entity.getID() == ID.BOUNCYENEMY || entity.getID() == ID.HOMINGENEMY ||
					entity.getID() == ID.IENEMY || entity.getID() == ID.IMOVINGENEMY ||
					entity.getID() == ID.IBOUNCYENEMY || entity.getID() == ID.IHOMINGENEMY)
				enemies++;
		}
		
		if (enemies == 0) {
			level++;
			
			if (level == 1) spawnEnemy();
			else if (level == 2) for (int i = 0; i < 2; i++) spawnEnemy();
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
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				
				spawnBouncyEnemy();
			} else if (level == 12) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
			} else if (level == 13) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 3; i++) spawnBouncyEnemy();
			} else if (level == 14) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 4; i++) spawnBouncyEnemy();
			} else if (level == 15) {
				for (int i = 0; i < 3; i++) spawnEnemy();
				for (int i = 0; i < 2; i++) spawnMovingEnemy();
				for (int i = 0; i < 5; i++) spawnBouncyEnemy();
			} else if (level == 16) {
				for (int i = 0; i < 4; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				
				spawnHomingEnemy();
			} else if (level == 17) {
				for (int i = 0; i < 4; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				for (int i = 0; i < 2; i++) spawnHomingEnemy();
			} else if (level == 18) {
				for (int i = 0; i < 4; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				for (int i = 0; i < 3; i++) spawnHomingEnemy();
			} else if (level == 19) {
				for (int i = 0; i < 4; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
				for (int i = 0; i < 4; i++) spawnHomingEnemy();
			} else if (level == 20) {
				for (int i = 0; i < 4; i++) spawnEnemy();
				for (int i = 0; i < 3; i++) spawnMovingEnemy();
				for (int i = 0; i < 2; i++) spawnBouncyEnemy();
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
		}
	}
	
	/**
	 * Spawns a basic enemy.
	 */
	private void spawnEnemy() {
		handler.addEntity(new Enemy(ID.ENEMY, random.nextInt(Game.WIDTH-25),
				random.nextInt(Game.HEIGHT-25), game, handler));
	}
	
	/**
	 * Spawns an enemy that will move towards the player.
	 */
	private void spawnMovingEnemy() {
		handler.addEntity(new Enemy(ID.MOVINGENEMY, random.nextInt(Game.WIDTH-25),
				random.nextInt(Game.HEIGHT-25), game, handler));
	}
	
	/**
	 * Spawns an enemy that will fire bouncy paintballs.
	 */
	private void spawnBouncyEnemy() {
		handler.addEntity(new Enemy(ID.BOUNCYENEMY, random.nextInt(Game.WIDTH-25),
				random.nextInt(Game.HEIGHT-25), game, handler));
	}
	
	/**
	 * Spawns an enemy that will fire homing paintballs.
	 */
	private void spawnHomingEnemy() {
		handler.addEntity(new Enemy(ID.HOMINGENEMY, random.nextInt(Game.WIDTH-25),
				random.nextInt(Game.HEIGHT-25), game, handler));
	}
}
