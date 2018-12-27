package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Graphics2D;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Game.State;

/**
 * Represents the game object handler of the game.
 * When constructed, this class is responsible for the management of the tick and render
 * functions of all game objects.
 * @author Ramone Graham
 *
 */
public class Handler {

	private LinkedList<GameObject> objects = new LinkedList<GameObject>();
	
	/**
	 * Updates the logic of all game objects.
	 */
	public void tick() {
		for (int i = 0; i < getObjects().size(); i++) {
			GameObject tempObject = getObjects().get(i);
			
			if (!(tempObject == null)) tempObject.tick();
		}
	}
	
	/**
	 * Updates the visuals of all game objects.
	 * @param graphics2d - The graphics used to update the visuals of the objects.
	 */
	public void render(Graphics2D graphics2D) {
		for (int i = 0; i < getObjects().size(); i++) {
			GameObject tempObject = getObjects().get(i);
			
			if (!(tempObject == null)) tempObject.render(graphics2D);
		}
	}
	
	/**
	 * Adds a game object to the objects list.
	 * @param object - The object to be added to the list.
	 */
	public void addObject(GameObject object) {
		getObjects().add(object);
	}
	
	/**
	 * Removes a game object from the objects list.
	 * @param object - The object to be removed from the list.
	 */
	public void removeObject(GameObject object) {
		getObjects().remove(object);
	}
	
	/**
	 * Resets the game's systems to start a new game.
	 */
	public void startGame() {
		Game.player.setVelX(0);
		Game.player.setVelY(0);
		Game.player.setX((Game.WIDTH/2)-16);
		Game.player.setY((Game.HEIGHT/2)-16);
		
		Game.player.maxHealth = 100;
		Game.player.health = 100;
		Game.player.score = 0;
		
		Spawner.level = 0;
		
		HeadsUpDisplay.shots = 0;
		HeadsUpDisplay.kills = 0;
		
		switch (Game.gameMode) {
		case SINGLEPLAYER:
			switch (Game.gameDifficulty) {
			case EASY:
				HeadsUpDisplay.ammo = -1;
				
				break;
			case NORMAL:
				HeadsUpDisplay.ammo = 30;
				
				break;
			case HARD:
				HeadsUpDisplay.ammo = 20;
				
				break;
			case EXTREME:
				HeadsUpDisplay.ammo = 10;
				
				break;
			}
			
			break;
		case MULTIPLAYER:
			HeadsUpDisplay.ammo = -1;
			
			break;
		}
		
		Game.gameState = State.GAME;
		
		switch (Game.gameMode) {
		case SINGLEPLAYER:
			addObject(Game.player);
			
			break;
		case MULTIPLAYER:
			if (!Game.player.spectator) addObject(Game.player);
			
			break;
		}
	}
	
	/**
	 * Returns the list of objects, optimised to prevent concurrent modification exceptions.
	 * @return The list of objects.
	 */
	public synchronized LinkedList<GameObject> getObjects() {
		return objects;
	}
}
