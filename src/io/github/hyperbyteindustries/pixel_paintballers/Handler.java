package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Graphics2D;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;

/**
 * Represents the game object handler of the game.
 * When constructed, this class is responsible for the management of the tick and render
 * functions of all game objects.
 * @author Ramone Graham
 *
 */
public class Handler {

	LinkedList<GameObject> objects = new LinkedList<GameObject>();
	
	/**
	 * Updates the logic of all game objects.
	 */
	public void tick() {
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			
			tempObject.tick();
		}
	}
	
	/**
	 * Updates the visuals of all game objects.
	 * @param graphics2d - The graphics used to update the visuals.
	 */
	public void render(Graphics2D graphics2d) {
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			
			tempObject.render(graphics2d);
		}
	}
	
	/**
	 * Adds a game object to the objects list.
	 * @param object - The object to be added to the list.
	 */
	public void addObject(GameObject object) {
		objects.add(object);
	}
	
	/**
	 * Removes a game object from the objects list.
	 * @param object - The object to be removed from the list.
	 */
	public void removeObject(GameObject object) {
		objects.remove(object);
	}
	
	/**
	 * Initialises the player and heads up display to start a game.
	 */
	public void startGame() {
		Game.player.setX((Game.XBOUND/2)-16);
		Game.player.setY((Game.YBOUND/2)-16);
		
		addObject(Game.player);
		
		HeadsUpDisplay.maxHealth = 100;
		HeadsUpDisplay.health = 100;
		HeadsUpDisplay.score = 0;
		HeadsUpDisplay.level = 0;
		HeadsUpDisplay.shots = 0;
		HeadsUpDisplay.kills = 0;
		
		if (Game.gameDifficulty == Difficulty.EASY) HeadsUpDisplay.ammo = -1;
		else if (Game.gameDifficulty == Difficulty.NORMAL) HeadsUpDisplay.ammo = 30;
		else if (Game.gameDifficulty == Difficulty.HARD) HeadsUpDisplay.ammo = 20;
		else if (Game.gameDifficulty == Difficulty.EXTREME) HeadsUpDisplay.ammo = 10;
	}
}
