package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.BLACK;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.io.File;

/**
 * Represents the core of the game.
 * When the game is initialised, this class is responsible for the management of
 * main functions across the game, such as the tick and render functions.
 * @author Ramone Graham
 * 
 */
public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -7703785121704902521L;
	
	public static final int WIDTH = 800, HEIGHT = WIDTH/12*9, XBOUND = WIDTH-6,
			YBOUND = HEIGHT-29;
	public static final String TITLE = "Pixel Paintballers";
	
	/**
	 * Represents the menu states of the game.
	 * When utilised, this enum is responsible for defining the game's current menu
	 * state.
	 * @author Ramone Graham
	 *
	 */
	public enum State {
		LOGO(), TITLESCREEN(), DIFFICULTYSELECT(), GAME(), GAMEOVER();
	}
	
	/**
	 * Represents the difficulty of the game.
	 * When utilised, this enum is responsible for defining the game's current
	 * difficulty.
	 * @author Ramone Graham
	 *
	 */
	public enum Difficulty {
		EASY(), NORMAL(), HARD(), EXTREME();
	}
	
	public static State gameState = State.LOGO;
	public static Difficulty gameDifficulty = null;

	public static boolean paused = false;
	
	public static Player player = new Player(XBOUND/2-16, YBOUND/2-16, ID.PLAYER);
	
	private Thread thread;
	private boolean running = false;
	
	private Handler handler;
	private Menu menu;
	private KeyInput keyInput;
	private HeadsUpDisplay headsUpDisplay;
	private Spawner spawner;

	/**
	 * Creates a new instance of the game.
	 */
	public Game() {
		handler = new Handler();
		menu = new Menu(handler);
		keyInput = new KeyInput(handler);
		headsUpDisplay = new HeadsUpDisplay();
		spawner = new Spawner(handler);

		addMouseListener(menu);
		addKeyListener(keyInput);
		
		new Window(TITLE, WIDTH, HEIGHT, this);
	}
	
	/**
	 * Starts execution of the game.
	 */
	public synchronized void start() {
		thread = new Thread(this, TITLE);
		thread.start();
		running = true;
	}
	
	/**
	 * Stops execution of the game.
	 */
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Runs the game loop.
	public void run() {
		requestFocus();
		
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
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
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		
		stop();
	}
	
	/**
	 * Updates the logic of the game.
	 */
	private void tick() {
		if (!(paused)) handler.tick();
		
		menu.tick();
		
		if (gameState == State.GAME) {
			if (!(isFocusOwner())) paused = true;
			
			if (!(paused)) {
				headsUpDisplay.tick();
				spawner.tick();
			}
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
		
		Graphics2D graphics2d = (Graphics2D) strategy.getDrawGraphics();
		
		graphics2d.setColor(BLACK);
		graphics2d.fillRect(0, 0, XBOUND, YBOUND);

		if (!(paused)) handler.render(graphics2d);
		
		menu.render(graphics2d);
		
		if (gameState == State.GAME) {
			if (!(paused)) headsUpDisplay.render(graphics2d);
		}
		
		graphics2d.dispose();
		strategy.show();
	}
	
	/**
	 * Mostly used with the float data-type coordinate system, this method clamps a
	 * variable to a given maximum and minimum.
	 * @param variable - The variable to clamp.
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
	 * Mostly used with integers, this method clamps a variable to a given maximum
	 * and minimum.
	 * @param variable - The variable to clamp.
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
		new Game();
		
		try {
			GraphicsEnvironment environment = 
					GraphicsEnvironment.getLocalGraphicsEnvironment();
		
			environment.registerFont(Font.createFont(Font.TRUETYPE_FONT, 
					new File("res/pixelex.ttf")));
			Thread.sleep(5000);
			
			gameState = State.TITLESCREEN;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
