package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 * Represents the game object handler of the game.
 * When constructed, this class is responsible for the management of the tick and
 * render functions of all game objects.
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
			
			tempObject.tick();
		}
	}
	
	/**
	 * Updates the visuals of all game objects.
	 * @param graphics2d - The graphics used to update the visuals.
	 */
	public void render(Graphics2D graphics2d) {
		for (int i = 0; i < getObjects().size(); i++) {
			GameObject tempObject = getObjects().get(i);
			
			tempObject.render(graphics2d);
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
	 * Initialises the player and heads up display to start a game.
	 */
	public void startGame() {
		Game.player.setX((Game.XBOUND/2)-16);
		Game.player.setY((Game.YBOUND/2)-16);
		
		addObject(Game.player);
		
		Game.player.maxHealth = 100;
		Game.player.health = 100;
		Game.player.score = 0;
	}
	
	/**
	 * Returns the list of objects, optimised to prevent concurrent modification exceptions.
	 * @return The list of objects.
	 */
	public synchronized LinkedList<GameObject> getObjects() {
		return objects;
	}
}
