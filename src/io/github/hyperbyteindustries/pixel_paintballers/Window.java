package io.github.hyperbyteindustries.pixel_paintballers;

import static io.github.hyperbyteindustries.pixel_paintballers.Game.ERROR_PREFIX;
import static io.github.hyperbyteindustries.pixel_paintballers.Game.TITLE;
import static java.awt.BorderLayout.CENTER;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import io.github.hyperbyteindustries.pixel_paintballers.managers.DataManager;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu.State;

/**
 * Represents the window/program frame of the game.
 * When constructed, this class is responsible for creating the game's window and handling any
 * changes to its state.
 * @author Ramone Graham
 *
 */
public class Window extends JFrame implements WindowListener {

	private static final long serialVersionUID = 5982497984337248345L;
	
	private Game game;
	
	public boolean fullscreen = false;
	public List<DisplayMode> fullscreenModes = new ArrayList<DisplayMode>();
	public DisplayMode fullscreenMode;

	/**
	 * Creates a new window.
	 * @param game - An instance of the Game class, used to display the visuals.
	 * @param title - The name of the window.
	 */
	public Window(Game game) {
		super(TITLE);
		
		this.game = game;
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		for (int i = 0; i < environment.getDefaultScreenDevice().getDisplayModes().length; i++) {
			DisplayMode mode = environment.getDefaultScreenDevice().getDisplayModes()[i];
			
			if (mode.getWidth() == 800 && mode.getHeight() == 600) fullscreenModes.add(mode);
		}
		
		fullscreenMode = fullscreenModes.get(0);
		
		Dimension dimension = new Dimension(Game.WIDTH, Game.HEIGHT);
		
		game.setMaximumSize(dimension);
		game.setMinimumSize(dimension);
		game.setPreferredSize(dimension);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
		
		add(game, CENTER);
		pack();
		
		addWindowListener(this);
		setLocationRelativeTo(null);
		
		try {
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/Game Icon.png")));
		} catch (IOException exception) {
			System.err.print(new Date() + " " + ERROR_PREFIX +
					"An exception occured whilst setting the game icon - ");
			exception.printStackTrace();
		}
		
		setVisible(true);
		
		game.start();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowActivated(WindowEvent event) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowClosed(WindowEvent event) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowClosing(WindowEvent event) {
		if (Menu.menuState == State.GAME && Game.gameMode == Game.Mode.MULTIPLAYER) {
			Packet01Disconnect packet = new Packet01Disconnect(Game.player.getUsername());
			packet.writeData(game.client);
		}
		
		DataManager.saveData();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowDeactivated(WindowEvent event) {
		if (Menu.menuState == State.GAME) Game.paused = true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowDeiconified(WindowEvent event) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowIconified(WindowEvent event) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void windowOpened(WindowEvent event) {
		
	}
	
	public void toggleFullscreen() {
		dispose();
		
		setUndecorated(!fullscreen);
		pack();
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		if (fullscreen) {
			environment.getDefaultScreenDevice().setFullScreenWindow(null);
			
			fullscreen = false;
		} else {
			environment.getDefaultScreenDevice().setFullScreenWindow(this);
			environment.getDefaultScreenDevice().setDisplayMode(fullscreenMode);
			
			fullscreen = true;
		}
		
		setVisible(true);
	}
}
