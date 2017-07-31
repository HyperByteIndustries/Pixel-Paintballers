package io.github.hyperbyteindustries.pixel_paintballers;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Represents the game window.
 * When constructed, this class is responsible for creating the game's window.
 * @author Ramone Graham
 *
 */
public class Window extends Canvas {

	private static final long serialVersionUID = 5982497984337248345L;

	/**
	 * Creates a new window.
	 * @param title - The name of the window.
	 * @param width - The width of the window.
	 * @param height - The height of the window.
	 * @param game - An instance of the Game class, used to display the visuals.
	 */
	public Window(String title, int width, int height, Game game) {
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
		
		game.start();
	}
}
