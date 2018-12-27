package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.GRAY;
import static java.awt.Color.BLACK;
import static java.awt.Cursor.DEFAULT_CURSOR;
import static java.awt.Cursor.CROSSHAIR_CURSOR;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;

import io.github.hyperbyteindustries.pixel_paintballers.net.Client;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;

/**
 * Represents the core of the game.
 * When the game is initialised, this class is responsible for the management of main functions
 * across the game, such as the tick and render functions.
 * @author Ramone Graham
 * 
 */
public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -7703785121704902521L;
	
	public static final int WIDTH = 800, HEIGHT = WIDTH / 12 * 9;
	public static final String TITLE = "Pixel Paintballers", MAINPREFIX = "[Main INFO]: ",
			WARNPREFIX = "[Main WARN]: ", ERRORPREFIX = "[Main ERROR]: ";
	
	/**
	 * Represents the menu states of the game.
	 * When utilised, this enum is responsible for defining the game's current menu state.
	 * @author Ramone Graham
	 *
	 */
	public enum State {
		LOGO(), TITLESCREEN(), MAINMENU(), DIFFICULTYSELECT(), GAME(), GAMEOVER(), MULTIPLAYER(),
		SERVERCONNECTION(), CUSTOMISATION(), INFO(), INFO2();
	}
	
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
	
	public static State gameState = State.LOGO;
	public static Mode gameMode = Mode.SINGLEPLAYER;
	public static Difficulty gameDifficulty = null;
	
	public static Player player;

	public static boolean paused = false;
	
	private Thread thread;
	private boolean running = false;
	
	private Handler handler;
	private Menu menu;
	private HeadsUpDisplay headsUpDisplay;
	private KeyInput keyInput;
	private Spawner spawner;
	
	private long pauseTimer;
	
	public Client client;
	public Server server;

	/**
	 * Creates a new instance of the game.
	 */
	public Game() {
		player = new Player(WIDTH/2-16, HEIGHT/2-16, ID.PLAYER, this, "Player", RED, WHITE,
				GRAY);
		
		handler = new Handler();
		menu = new Menu(this, handler);
		headsUpDisplay = new HeadsUpDisplay(handler);
		keyInput = new KeyInput(handler);
		spawner = new Spawner(this, handler);
		
		client = new Client(this, handler, "localhost");

		addMouseListener(menu);
		addKeyListener(keyInput);
		
		new Window(this, TITLE);
		
		AudioManager.init();
		DataManager.init();
		
		menu.clickable = true;
	}
	
	/**
	 * Starts execution of the game.
	 */
	public synchronized void start() {
		thread = new Thread(this, TITLE + " [MAIN]");
		thread.start();
		
		client.start();
		
		running = true;
	}
	
	/**
	 * Stops execution of the game.
	 */
	public synchronized void stop() {
		try {
			thread.join();

			client.stop();
			
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Runs the game loop.
	public void run() {
		requestFocus();
		
		long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
		double amountOfTicks = 60.0, ns = 1000000000 / amountOfTicks, delta = 0;
		int frames = 0;
		
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while (delta >= 1) {
				tick();
				
				delta--;
			}
			
			if (running) render();
			
			frames++;
			
			if (System.currentTimeMillis() - timer >= 1000) {
				timer = System.currentTimeMillis();
				
				if (menu.clickable) System.out.println(MAINPREFIX + frames + " FPS");
				
				frames = 0;
				
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
		
		if (gameState == State.GAME) {
			switch (gameMode) {
			case SINGLEPLAYER:
				if (!paused) {
					headsUpDisplay.tick();
					spawner.tick();
					
					if (getCursor().getType() == DEFAULT_CURSOR)
						setCursor(new Cursor(CROSSHAIR_CURSOR));
				} else {
					for (int i = 0; i < handler.getObjects().size(); i++) {
						GameObject tempObject = handler.getObjects().get(i);
						
						if (tempObject.getID() == ID.ENEMY || tempObject.getID() ==
								ID.MOVINGENEMY || tempObject.getID() == ID.BOUNCYENEMY ||
								tempObject.getID() == ID.HOMINGENEMY) {
							Enemy enemy = (Enemy) tempObject;
							
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
			if (getCursor().getType() == CROSSHAIR_CURSOR)
				setCursor(new Cursor(DEFAULT_CURSOR));
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
		
		if (gameState == State.GAME) {
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

	// Called by the JVM, this method executes the game.
	public static void main(String[] args) {
		try {
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(
					Font.TRUETYPE_FONT, new File("res/pixelex.ttf")));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		new Game();
	}
}
