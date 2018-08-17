package io.github.hyperbyteindustries.pixel_paintballers;

import java.util.Random;

/**
 * Represents the spawning system of the game. When constructed, this class is
 * responsible for the spawning of enemy AI.
 * @author Ramone Graham
 *
 */
public class Spawner {

	private Game game;
	private Handler handler;
	
	private Random random;
	
	/**
	 * Creates a new instance of the spawner.
	 * @param game - An instance of the Game class, used to spawn enemies.
	 * @param handler - An instance of the Handler class, used to spawn enemies.
	 */
	public Spawner(Game game, Handler handler) {
		this.handler = handler;
		random = new Random();
	}
	
	/**
	 * Updates the logic of the spawner.
	 */
	public void tick() {
		int enemies = 0;
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.ENEMY || tempObject.getID() == ID.MOVINGENEMY ||
					tempObject.getID() == ID.BOUNCYENEMY || tempObject.getID() ==
					ID.HOMINGENEMY) enemies++;
		}
	}
	
	/**
	 * Spawns a basic enemy.
	 */
	private void spawnEnemy() {
		handler.addObject(new Enemy(random.nextInt(Game.XBOUND-25),
				random.nextInt(Game.YBOUND-25), ID.ENEMY, game, handler));
	}
	
	/**
	 * Spawns an enemy that will move towards the player.
	 */
	private void spawnMovingEnemy() {
		handler.addObject(
				new Enemy(random.nextInt(Game.XBOUND-25),
						random.nextInt(Game.YBOUND-25), ID.MOVINGENEMY, game, handler));
	}
	
	/**
	 * Spawns an enemy that will fire bouncy paintballs.
	 */
	private void spawnBouncyEnemy() {
		handler.addObject(
				new Enemy(random.nextInt(Game.XBOUND-25),
						random.nextInt(Game.YBOUND-25), ID.BOUNCYENEMY, game, handler));
	}
	
	/**
	 * Spawns an enemy that will fire homing paintballs.
	 */
	private void spawnHomingEnemy() {
		handler.addObject(
				new Enemy(random.nextInt(Game.XBOUND-25),
						random.nextInt(Game.YBOUND-25), ID.HOMINGENEMY, game, handler));
	}
}
