package io.github.hyperbyteindustries.pixel_paintballers;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import io.github.hyperbyteindustries.pixel_paintballers.Game.State;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;

/**
 * Represents the game window.
 * When constructed, this class is responsible for creating the game's window.
 * @author Ramone Graham
 *
 */
public class Window extends Canvas implements WindowListener {

	private static final long serialVersionUID = 5982497984337248345L;
	
	private Game game;

	/**
	 * Creates a new window.
	 * @param title - The name of the window.
	 * @param width - The width of the window.
	 * @param height - The height of the window.
	 * @param game - An instance of the Game class, used to display the visuals and send
	 * disconnect packets if the game closes.
	 */
	public Window(String title, int width, int height, Game game) {
		this.game = game;
		
		JFrame frame = new JFrame(title);
		Dimension dimension = new Dimension(width, height);
		
		frame.setMaximumSize(dimension);
		frame.setMinimumSize(dimension);
		frame.setPreferredSize(dimension);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
		frame.addWindowListener(this);
		
		game.start();
	}

	// Evoked when a window gains focus.
	public void windowActivated(WindowEvent e) {
		
	}

	// Evoked when a window is closed.
	public void windowClosed(WindowEvent e) {
		
	}

	// Evoked when a window is closing.
	public void windowClosing(WindowEvent e) {
		Packet01Disconnect packet = new Packet01Disconnect(Game.player.getUsername());
		packet.writeData(game.client);
	}

	// Evoked when a window has lost focus.
	public void windowDeactivated(WindowEvent e) {
		if (Game.gameState == State.GAME) Game.paused = true;
	}

	// Evoked when a window is opened from its icon.
	public void windowDeiconified(WindowEvent e) {
		
	}

	// Evoked when a window is minimised.
	public void windowIconified(WindowEvent e) {
		
	}

	// Evoked when a window is opened.
	public void windowOpened(WindowEvent e) {
		
	}
}
