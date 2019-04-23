package io.github.hyperbyteindustries.pixel_paintballers.entities;

import java.awt.Graphics2D;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.ui.HeadsUpDisplay;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu.State;

/**
 * Represents the entity handler of the game.
 * When constructed, this class is responsible for the management of the tick and render
 * functions of all entities.
 * @author Ramone Graham
 *
 */
public class Handler {

	private LinkedList<Entity> entities = new LinkedList<Entity>();
	
	/**
	 * Updates the logic of all entities.
	 */
	public void tick() {
		for (int i = 0; i < getEntities().size(); i++) {
			Entity entity = getEntities().get(i);
			
			entity.tick();
		}
	}
	
	/**
	 * Updates the visuals of all entities.
	 * @param graphics2d - The graphics context used to update the visuals of the entities.
	 */
	public void render(Graphics2D graphics2D) {
		for (int i = 0; i < getEntities().size(); i++) {
			Entity entity = getEntities().get(i);
			
			entity.render(graphics2D);
		}
	}
	
	/**
	 * Adds a entity to the <code>entities</code> list.
	 * @param entity - The entity to be added to the list.
	 */
	public void addEntity(Entity entity) {
		getEntities().add(entity);
	}
	
	/**
	 * Removes a entity from the <code>entities</code> list.
	 * @param entity - The entity to be removed from the list.
	 */
	public void removeEntity(Entity entity) {
		getEntities().remove(entity);
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
		
		Menu.menuState = State.GAME;
		
		switch (Game.gameMode) {
		case SINGLEPLAYER:
			addEntity(Game.player);
			
			break;
		case MULTIPLAYER:
			if (!Game.player.spectator) addEntity(Game.player);
			
			break;
		}
	}
	
	/**
	 * Gets the entity list.
	 * @return The entity list.
	 */
	public synchronized LinkedList<Entity> getEntities() {
		return entities;
	}
}
