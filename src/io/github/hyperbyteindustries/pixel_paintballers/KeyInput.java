package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.event.KeyEvent.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import io.github.hyperbyteindustries.pixel_paintballers.Game.State;

/**
 * Represents the key input handler of the game.
 * When constructed, this class is responsible for the management of certain key events triggered
 * by the user.
 * @author Ramone Graham
 *
 */
public class KeyInput extends KeyAdapter {

	private Handler handler;
	
	private boolean[] keyDown = new boolean[4];
	
	/**
	 * Creates a new instance of this class
	 * @param handler - An instance of the handler class, used to control player movement.
	 */
	public KeyInput(Handler handler) {
		this.handler = handler;
		
		keyDown[0] = false;
		keyDown[1] = false;
		keyDown[2] = false;
		keyDown[3] = false;
	}
	
	// Invoked when a key is pressed.
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		
		if (Game.gameState == State.GAME) {
			if (!(Game.paused)) {
				if (key == VK_ESCAPE) Game.paused = true;
				else {
					for (int i = 0; i < handler.getObjects().size(); i++) {
						GameObject tempObject = handler.getObjects().get(i);
						
						if (tempObject.getID() == ID.PLAYER) {
							if (key == VK_W) {
								tempObject.setVelY(-5);
								keyDown[0] = true;
							}
							
							if (key == VK_A) {
								tempObject.setVelX(-5);
								keyDown[1] = true;
							}
							
							if (key == VK_S) {
								tempObject.setVelY(5);
								keyDown[2] = true;
							}
							
							if (key == VK_D) {
								tempObject.setVelX(5);
								keyDown[3] = true;
							}
						}
					}
					
					if (key == VK_R) {
						if (HeadsUpDisplay.ammo == 0 && HeadsUpDisplay.reloadTime == 120)
							HeadsUpDisplay.reloading = true;
					}
				}
			} else {
				if (key == VK_ESCAPE) Game.paused = false;
			}
		} else if (Game.gameState == State.CUSTOMISATION) {
			if (Menu.editText) {
				if (key == VK_ENTER) Menu.editText = false;
				else if (key == VK_BACK_SPACE) {
					if (!(Game.player.getUsername().length() == 0))
						Game.player.setUsername(Game.player.getUsername().substring(0,
								Game.player.getUsername().length()-1));
				} else if (key == VK_MINUS) {
					if (event.isShiftDown()) {
						if (Game.player.getUsername().length() < 17)
							Game.player.setUsername(Game.player.getUsername().concat("_"));
						else AudioManager.getSound("Denied").play();
					}
				} else {
					String chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					
					for (int i = 0; i < chars.length(); i++) {
						String c = chars.substring(i, i+1);
						
						if (KeyEvent.getKeyText(key).equalsIgnoreCase(c)) {
							if (Game.player.getUsername().length() < 17)
								Game.player.setUsername(Game.player.getUsername().concat(c));
							else AudioManager.getSound("Denied").play();
						}
					}
				}
			}
		}
	}
	
	// Invoked when a key is released.
	public void keyReleased(KeyEvent event) {
		if (Game.gameState == State.GAME) {
			int key = event.getKeyCode();
			
			for (int i = 0; i < handler.getObjects().size(); i++) {
				GameObject tempObject = handler.getObjects().get(i);
				
				if (tempObject.getID() == ID.PLAYER) {
					if (key == VK_W) keyDown[0] = false;
					if (key == VK_A) keyDown[1] = false;
					if (key == VK_S) keyDown[2] = false;
					if (key == VK_D) keyDown[3] = false;
					
					if (!keyDown[0] && !keyDown[2]) tempObject.setVelY(0);
					if (!keyDown[1] && !keyDown[3]) tempObject.setVelX(0);
				}
			}
		}
	}
}
