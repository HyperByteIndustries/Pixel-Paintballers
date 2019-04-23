package io.github.hyperbyteindustries.pixel_paintballers.ui;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_W;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.managers.AudioManager;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu.State;

/**
 * Represents the key input handler of the game.
 * When constructed, this class is responsible for the management of certain key events triggered
 * by the user.
 * @author Ramone Graham
 *
 */
public class KeyInput extends KeyAdapter {

	/**
	 * Represents the keybind system of the game.
	 * When constructed, this class is responsible for the management of a control's keybind.
	 * @author Ramone Graham
	 *
	 */
	public class Keybind {

		private boolean pressed = false;
		
		public int keyCode;
		public boolean edit = false;
		
		/**
		 * Creates a new keybind.
		 * @param keyCode - the ASCII key code of the key to use for the keybind.
		 */
		public Keybind(int keyCode) {
			this.keyCode = keyCode;
		}
	}

	public static Keybind up, down, left, right, reload;
	
	/**
	 * Creates a new instance of this class
	 * @param handler - An instance of the handler class, used to control player movement.
	 */
	public KeyInput(Handler handler) {
		up = new Keybind(VK_W);
		down = new Keybind(VK_S);
		left = new Keybind(VK_A);
		right = new Keybind(VK_D);
		reload = new Keybind(VK_R);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyTyped(java.awt.event.KeyEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyTyped(KeyEvent event) {
		if (Menu.menuState == State.CUSTOMISATION && Menu.edit) {
			String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
			
			for (int i = 0; i < chars.length(); i++) {
				char c = chars.charAt(i);
				
				if (event.getKeyChar() == c) {
					if (Game.player.getUsername().length() < 17)
						Game.player.setUsername(Game.player.getUsername() + c);
					else AudioManager.getSound("Denied").play();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		
		if (Menu.menuState == State.GAME) {
			if (!(Game.paused)) {
				if (keyCode == VK_ESCAPE) Game.paused = true;
				else {
					if (keyCode == up.keyCode) {
						up.pressed = true;
						Game.player.setVelY(-2);
					} else if (keyCode == down.keyCode) {
						down.pressed = true;
						Game.player.setVelY(2);
					} else if (keyCode == left.keyCode) {
						left.pressed = true;
						Game.player.setVelX(-2);
					} else if (keyCode == right.keyCode) {
						right.pressed = true;
						Game.player.setVelX(2);
					} else if (keyCode == reload.keyCode) {
						if (HeadsUpDisplay.ammo == 0 && HeadsUpDisplay.reloadTime == 120)
							HeadsUpDisplay.reloading = true;
					}
				}
			} else {
				if (keyCode == VK_ESCAPE) Game.paused = false;
			}
		} else if (Menu.menuState == State.OPTIONS) {
			if (up.edit) {
				up.keyCode = keyCode;
				up.edit = false;
			} else if (down.edit) {
				down.keyCode = keyCode;
				down.edit = false;
			} else if (left.edit) {
				left.keyCode = keyCode;
				left.edit = false;
			} else if (right.edit) {
				right.keyCode = keyCode;
				right.edit = false;
			} else if (reload.edit) {
				reload.keyCode = keyCode;
				reload.edit = false;
			}
		} if (Menu.menuState == State.CUSTOMISATION) {
			if (Menu.edit) {
				if (keyCode == VK_ENTER) Menu.edit = false;
				else if (keyCode == VK_BACK_SPACE) {
					if (!(Game.player.getUsername().length() == 0))
						Game.player.setUsername(Game.player.getUsername().substring(0,
								Game.player.getUsername().length()-1));
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void keyReleased(KeyEvent event) {
		if (Menu.menuState == State.GAME) {
			int keyCode = event.getKeyCode();
			
			if (keyCode == up.keyCode) up.pressed = false;
			if (keyCode == down.keyCode) down.pressed = false;
			if (keyCode == left.keyCode) left.pressed = false;
			if (keyCode == right.keyCode) right.pressed = false;
			
			if (!up.pressed && !down.pressed) Game.player.setVelY(0);
			if (!left.pressed && !right.pressed) Game.player.setVelX(0);
		}
	}
}
