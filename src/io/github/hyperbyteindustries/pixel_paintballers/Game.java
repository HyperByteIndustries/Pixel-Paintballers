package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.BLACK;
import static java.awt.Color.GRAY;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Cursor.CROSSHAIR_CURSOR;
import static java.awt.Cursor.DEFAULT_CURSOR;
import static java.awt.Font.TRUETYPE_FONT;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Date;

import io.github.hyperbyteindustries.pixel_paintballers.entities.Enemy;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Player;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Spawner;
import io.github.hyperbyteindustries.pixel_paintballers.managers.AudioManager;
import io.github.hyperbyteindustries.pixel_paintballers.managers.DataManager;
import io.github.hyperbyteindustries.pixel_paintballers.net.Client;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;
import io.github.hyperbyteindustries.pixel_paintballers.ui.HeadsUpDisplay;
import io.github.hyperbyteindustries.pixel_paintballers.ui.KeyInput;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu.State;

/**
 * Represents the core of the game.
 * When the game is initialised, this class is responsible for the management of main functions
 * across the game, such as the tick and render functions.
 * @author Ramone Graham
 * 
 */
public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -7703785121704902521L;
	
	public static final int WIDTH = 800, HEIGHT = WIDTH / 4 * 3;
	public static final String TITLE = "Pixel Paintballers", INFO_PREFIX = "[Main INFO]: ",
			WARN_PREFIX = "[Main WARN]: ", ERROR_PREFIX = "[Main ERROR]: ";
	
	/**
	 * Represents the game modes of the game.
	 * When utilised, this enum is responsible for defining the game's current game mode.
	 * @author Ramone Graham
	 *
	 */
	public enum Mode {
		SINGLEPLAYER(), MULTIPLAYER();
	}
	
	/**
	 * Represents the difficulty of the game.
	 * When utilised, this enum is responsible for defining the game's current difficulty.
	 * @author Ramone Graham
	 *
	 */
	public enum Difficulty {
		EASY(), NORMAL(), HARD(), EXTREME();
	}
	
	public static Mode gameMode = Mode.SINGLEPLAYER;
	public static Difficulty gameDifficulty = null;

	public static float musicVolume = 1, sfxVolume = 1;
	public static boolean paused = false;
	
	public static Player player;

	private boolean running = false;
	private Thread thread;
	
	private Handler handler;
	private Spawner spawner;
	private HeadsUpDisplay headsUpDisplay;
	private Menu menu;
	private KeyInput keyInput;
	
	private long pauseTimer;
	
	public Window window;
	
	public Client client;
	public Server server;

	/**
	 * Creates a new instance of the game.
	 */
	public Game() {
		player = new Player(WIDTH/2-16, HEIGHT/2-16, this, "Player", RED, WHITE, GRAY);
		
		handler = new Handler();
		spawner = new Spawner(this, handler);
		headsUpDisplay = new HeadsUpDisplay(handler);
		menu = new Menu(this, handler);
		keyInput = new KeyInput(handler);
		
		window = new Window(this);

		addMouseListener(menu);
		addKeyListener(keyInput);
		
		AudioManager.init();
		DataManager.init();
		
		menu.clickable = true;
	}
	
	/**
	 * Starts execution of the game.
	 */
	public synchronized void start() {
		System.out.println(new Date() + " " + INFO_PREFIX + "Starting Pixel Paintballers...");
		
		running = true;
		thread = new Thread(this, TITLE + " [MAIN]");
		client = new Client(this, handler, "localhost");
		
		thread.start();
		
		client.start();
		
		System.out.println(new Date() + " " + INFO_PREFIX + "Startup complete!");
	}
	
	/**
	 * Stops execution of the game.
	 */
	public synchronized void stop() {
		System.out.println(new Date() + " " + INFO_PREFIX + "Stopping Pixel Paintballers...");

		client.stop();
		
		running = false;
		
		try {
			thread.join();
		} catch (InterruptedException exception) {
			System.err.print(new Date() + " " + ERROR_PREFIX + "An exception occured whilst "
					+ "stopping the game - ");
			exception.printStackTrace();
		}
		
		System.out.println(new Date() + " " + INFO_PREFIX + "Shutdown complete!");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	/**
	 * {@inheritDoc}
	 */
	public void run() {
		requestFocus();
		
		long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
		double amountOfTicks = 60D, nanosecondsPerTick = 1000000000 / amountOfTicks, delta = 0;
		int ticksPerSecond = 0, framesPerSecond = 0;
		
		while (running) {
			long now = System.nanoTime();
			delta += (now-lastTime) / nanosecondsPerTick;
			lastTime = now;
			
			while (delta >= 1) {
				tick();
				
				delta--;
				ticksPerSecond++;
			}
			
			if (running) render();
			
			framesPerSecond++;
			
			if (System.currentTimeMillis()-timer >= 1000) {
				timer = System.currentTimeMillis();
				
				System.out.println(new Date() + " " + INFO_PREFIX + ticksPerSecond + " TPS, " +
						framesPerSecond + " FPS");
				
				framesPerSecond = 0;
				ticksPerSecond = 0;
				
				DataManager.timePlayed[0] += 1;
				
				if (DataManager.timePlayed[0] == 60) {
					DataManager.timePlayed[1] += 1;
					DataManager.timePlayed[0] = 0;
				}
				
				if (DataManager.timePlayed[1] == 60) {
					DataManager.timePlayed[2] += 1;
					DataManager.timePlayed[1] = 0;
				}
			}
		}
		
		stop();
	}
	
	/**
	 * Updates the logic of the game.
	 */
	private void tick() {
		switch (gameMode) {
		case SINGLEPLAYER:
			if (!paused) handler.tick();
			
			break;

		case MULTIPLAYER:
			handler.tick();
			
			break;
		}
		
		menu.tick();
		
		if (Menu.menuState == State.GAME) {
			switch (gameMode) {
			case SINGLEPLAYER:
				if (!paused) {
					headsUpDisplay.tick();
					spawner.tick();
					
					if (getCursor().getType() == DEFAULT_CURSOR)
						setCursor(new Cursor(CROSSHAIR_CURSOR));
				} else {
					for (int i = 0; i < handler.getEntities().size(); i++) {
						Entity entity = handler.getEntities().get(i);
						
						if (entity.getID() == ID.ENEMY || entity.getID() == ID.MOVINGENEMY ||
								entity.getID() == ID.BOUNCYENEMY || entity.getID() ==
								ID.HOMINGENEMY) {
							Enemy enemy = (Enemy) entity;
							
							enemy.attackTimer += (System.currentTimeMillis()-pauseTimer);
							enemy.shootTimer += (System.currentTimeMillis()-pauseTimer);
						}
					}
					
					if (getCursor().getType() == CROSSHAIR_CURSOR)
						setCursor(new Cursor(DEFAULT_CURSOR));
				}
				
				pauseTimer = System.currentTimeMillis();
				
				break;

			case MULTIPLAYER:
				if (!(server == null)) server.tick();
				
				headsUpDisplay.tick();
				
				if (!Game.player.spectator) {
					if (!paused) {
						if (getCursor().getType() == DEFAULT_CURSOR)
							setCursor(new Cursor(CROSSHAIR_CURSOR));
					} else {
						if (getCursor().getType() == CROSSHAIR_CURSOR)
							setCursor(new Cursor(DEFAULT_CURSOR));
					}
				}
				
				break;
			}
		} else {
			if (getCursor().getType() == CROSSHAIR_CURSOR) setCursor(new Cursor(DEFAULT_CURSOR));
		}
	}
	
	/**
	 * Updates the visuals of the game.
	 */
	private void render() {
		BufferStrategy strategy = getBufferStrategy();
		
		if (strategy == null) {
			createBufferStrategy(3);
			
			return;
		}
		
		Graphics2D graphics2D = (Graphics2D) strategy.getDrawGraphics();
		
		graphics2D.setColor(BLACK);
		graphics2D.fillRect(0, 0, WIDTH, HEIGHT);
		
		switch (gameMode) {
		case SINGLEPLAYER:
			if (!paused) handler.render(graphics2D);
			
			break;

		case MULTIPLAYER:
			handler.render(graphics2D);
			
			break;
		}
		
		menu.render(graphics2D);
		
		if (Menu.menuState == State.GAME) {
			switch (gameMode) {
			case SINGLEPLAYER:
				if (!paused) headsUpDisplay.render(graphics2D);
				
				break;
				
			case MULTIPLAYER:
				if (!player.spectator) headsUpDisplay.render(graphics2D);
				
				break;
			}
		}
		
		graphics2D.dispose();
		strategy.show();
	}
	
	/**
	 * Clamps a variable to a given maximum and minimum.
	 * @param variable - The float to clamp.
	 * @param minimum - The minimum value.
	 * @param maximum - The maximum value.
	 * @return The variable in the clamped parameters.
	 */
	public static float clamp(float variable, float minimum, float maximum) {
		if (variable <= minimum) return minimum;
		else if (variable >= maximum) return maximum;
		else return variable;
	}
	
	/**
	 * Clamps a variable to a given maximum and minimum.
	 * @param variable - The integer to clamp.
	 * @param minimum - The minimum value.
	 * @param maximum - The maximum value.
	 * @return The variable in the clamped parameters.
	 */
	public static int clamp(int variable, int minimum, int maximum) {
		if (variable <= minimum) return minimum;
		else if (variable >= maximum) return maximum;
		else return variable;
	}

	/**
	 * Called by the JVM, this method executes the game.
	 * @param args - Additional arguments included to start the game.
	 */
	public static void main(String[] args) {
		try {
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			
			environment.registerFont(Font.createFont(TRUETYPE_FONT,
					Game.class.getResourceAsStream("/pixelex.ttf")));
		} catch (FontFormatException | IOException exception) {
			System.err.print(new Date() + " " + ERROR_PREFIX + "An exception occured whilst "
					+ "registering the font - ");
			exception.printStackTrace();
		}
		
		new Game();
	}
}
