package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.BorderLayout.CENTER;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import io.github.hyperbyteindustries.pixel_paintballers.Game.State;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;

/**
 * Represents the game window.
 * When constructed, this class is responsible for creating the game's window and handling any
 * changes to its state.
 * @author Ramone Graham
 *
 */
public class Window extends Canvas implements WindowListener {

	private static final long serialVersionUID = 5982497984337248345L;
	
	private Game game;

	/**
	 * Creates a new window.
	 * @param game - An instance of the Game class, used to display the visuals.
	 * @param title - The name of the window.
	 */
	public Window(Game game, String title) {
		this.game = game;
		
		Dimension dimension = new Dimension(Game.WIDTH, Game.HEIGHT);
		JFrame frame = new JFrame(title);
		
		game.setMaximumSize(dimension);
		game.setMinimumSize(dimension);
		game.setPreferredSize(dimension);
		
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		
		frame.add(game, CENTER);
		frame.pack();
		
		frame.addWindowListener(this);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(new ImageIcon("res/Game Icon.png").getImage());
		frame.setVisible(true);
		
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
		if (Game.gameState == State.GAME && Game.gameMode == Game.Mode.MULTIPLAYER) {
			Packet01Disconnect packet = new Packet01Disconnect(Game.player.getUsername());
			packet.writeData(game.client);
		}
		
		DataManager.saveData();
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
