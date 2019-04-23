package io.github.hyperbyteindustries.pixel_paintballers.ui;

import static io.github.hyperbyteindustries.pixel_paintballers.Game.ERROR_PREFIX;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.PINK;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static java.awt.Font.PLAIN;

import java.awt.Desktop;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Paintball;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Spawner;
import io.github.hyperbyteindustries.pixel_paintballers.managers.AudioManager;
import io.github.hyperbyteindustries.pixel_paintballers.managers.DataManager;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet00Connect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet03PlayerShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet05PlayerDeath;

/**
 * Represents the menu system and mouse input handler of the game.
 * When constructed, this class is responsible for the management of the menu display and
 * certain mouse events triggered by the user.
 * @author Ramone Graham
 *
 */
public class Menu extends MouseAdapter {

	/**
	 * Represents the menu states of the game.
	 * When utilised, this enum is responsible for defining the game's current menu state.
	 * @author Ramone Graham
	 *
	 */
	public enum State {
		LOGO(), TITLE_SCREEN(), MAIN_MENU(), DIFFICULTY_SELECT(), GAME(), GAME_OVER(),
		MULTIPLAYER_MENU(), SERVER_CONNECTION(), OPTIONS(), CUSTOMISATION(), INFO(), INFO_2();
	}
	
	public static State menuState = State.LOGO;

	public static boolean edit = false;
	
	private Game game;
	private Handler handler;
	
	private Image companyLogo, gameLogo;
	private Font titleSelect, menuHeader, menuSelect, menuText;
	
	private int logoY = 0;
	
	private boolean highscore = false;
	
	public boolean clickable = false;
	
	/**
	 * Creates a new instance of this class.
	 * @param game - An instance of the Game class, used to draw pictures and send packets.
	 * @param handler - An instance of the Handler class, used to shoot paintballs and manage
	 * the player.
	 */
	public Menu(Game game, Handler handler) {
		this.game = game;
		this.handler = handler;
		
		try {
			companyLogo = ImageIO.read(getClass().getResourceAsStream("/Company logo.png"));
			gameLogo = ImageIO.read(getClass().getResourceAsStream("/Game logo.png"));
		} catch (IOException exception) {
			System.err.print(new Date() + " " + ERROR_PREFIX +
					"An exception occured whilst registering the logos - ");
			exception.printStackTrace();
		}
		
		titleSelect = new Font("Pixel EX", PLAIN, 32);
		menuHeader = new Font("Pixel EX", BOLD, 40);
		menuSelect = new Font("Pixel EX", PLAIN, 20);
		menuText = new Font("Pixel EX", ITALIC, 15);
	}
	
	/**
	 * Updates the logic of the menu.
	 */
	public void tick() {
		Game.musicVolume = Game.clamp(Game.musicVolume, 0, 1);
		Game.sfxVolume = Game.clamp(Game.sfxVolume, 0, 1);
		
		if (menuState == State.LOGO) {
			if (clickable) {
				logoY -= 10;
				
				logoY = Game.clamp(logoY, -600, 0);
			}
		} else if (menuState == State.TITLE_SCREEN) {
			if (!AudioManager.getMusic("Title").playing())
				AudioManager.getMusic("Title").loop(1, Game.musicVolume);
		} else if (menuState == State.MAIN_MENU) {
			if (!AudioManager.getMusic("Menu 1").playing()) {
				if (AudioManager.getMusic("Menu 2").playing()) {
					AudioManager.getMusic("Menu 2").pause();
					AudioManager.getMusic("Menu 1").setPosition(AudioManager.getMusic("Menu 2").
							getPosition());
				}

				AudioManager.getMusic("Menu 1").loop(1, Game.musicVolume);
			}
		} else if (menuState == State.DIFFICULTY_SELECT || menuState ==
				State.MULTIPLAYER_MENU || menuState == State.OPTIONS || menuState ==
				State.CUSTOMISATION || menuState == State.INFO) {
			if (!AudioManager.getMusic("Menu 2").playing()) {
				if (AudioManager.getMusic("Menu 1").playing()) {
					AudioManager.getMusic("Menu 1").pause();
					AudioManager.getMusic("Menu 2").setPosition(AudioManager.getMusic("Menu 1").
							getPosition());
				} else if (AudioManager.getMusic("Menu 3").playing()) {
					AudioManager.getMusic("Menu 3").pause();
					AudioManager.getMusic("Menu 2").setPosition(AudioManager.getMusic("Menu 3").
							getPosition());
				}

				AudioManager.getMusic("Menu 2").loop(1, Game.musicVolume);
			}
		} else if (menuState == State.GAME) {
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				if (!AudioManager.getMusic("Game 1").playing())
					AudioManager.getMusic("Game 1").loop(1, Game.musicVolume);
				
				break;
			case MULTIPLAYER:
				if (!AudioManager.getMusic("Game 2").playing())
					AudioManager.getMusic("Game 2").loop(1, Game.musicVolume);
				
				break;
			}
			
			if (Game.player.health == 0) {
				if (Game.gameMode == Game.Mode.MULTIPLAYER) {
					Packet05PlayerDeath packet =
							new Packet05PlayerDeath(Game.player.getUsername());
					packet.writeData(game.client);
				}
				
				Game.paused = false;
				Game.player.setX((Game.WIDTH/2)-16);
				Game.player.setY((Game.HEIGHT/2)-16);
				Game.player.setVelX(0);
				Game.player.setVelY(0);
				handler.getEntities().clear();
				
				DataManager.increaseStatistic("Shots fired", HeadsUpDisplay.shots);
				DataManager.increaseStatistic("Total kills", HeadsUpDisplay.kills);
				DataManager.increaseStatistic("Games played", 1);
				
				if (Game.gameMode == Game.Mode.SINGLEPLAYER) {
					if (Game.player.score > DataManager.getStatistic("Highscore (" +
							Game.gameDifficulty.name() + ")")) {
						DataManager.setStatistic("Highscore (" + Game.gameDifficulty.name() +
								")", Game.player.score);
						
						highscore = true;
					} else highscore = false;
				}
				
				DataManager.saveData();
				
				menuState = State.GAME_OVER;
			}
		} else if (menuState == State.SERVER_CONNECTION) {
			if (!AudioManager.getMusic("Menu 3").playing()) {
				if (AudioManager.getMusic("Menu 2").playing()) {
					AudioManager.getMusic("Menu 2").pause();
					AudioManager.getMusic("Menu 3").setPosition(AudioManager.getMusic("Menu 2").
							getPosition());
				}

				AudioManager.getMusic("Menu 3").loop(1, Game.musicVolume);
			}
		}
	}
	
	/**
	 * Updates the visuals for the menu.
	 * @param graphics2D - The graphics context used to update the visuals.
	 */
	public void render(Graphics2D graphics2D) {
		FontMetrics titleSelectMetrics = graphics2D.getFontMetrics(titleSelect);
		FontMetrics menuHeaderMetrics = graphics2D.getFontMetrics(menuHeader);
		FontMetrics menuSelectMetrics = graphics2D.getFontMetrics(menuSelect);
		
		switch (menuState) {
		case LOGO:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			String header = "Software Warning";
			Rectangle2D headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
			
			graphics2D.drawString(header, (int) (400-headerBounds.getWidth()/2), 40);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Pixel Paintballers is a non-profit, open-source project "
					+ "developed", 5, 100);
			graphics2D.drawString("under the direction of Ramone Graham (A.K.A Sweetboy13735) "
					+ "of HyperByte", 5, 115);
			graphics2D.drawString("Industries.", 5, 130);
			
			graphics2D.drawString("The original program (Referenced to as the \"software\") is "
					+ "provided to", 5, 160);
			graphics2D.drawString("any user free-of-charge, and can be downloaded from the "
					+ "project's", 5, 175);
			graphics2D.drawString("website: "
					+ "https://hyperbyteindustries.github.io/Pixel-Paintballers", 5, 190);
			graphics2D.drawRect(98, 189, 636, 1);
			
			graphics2D.drawString("Before continuing, we advise you to ensure that a duplicate "
					+ "of the", 5, 220);
			graphics2D.drawString("software is not running from the same file directory, as "
					+ "this could lead", 5, 235);
			graphics2D.drawString("to corruptions in the game's data file, as well as "
					+ "unintended game", 5, 250);
			graphics2D.drawString("crashes.", 5, 265);
			
			graphics2D.drawString("If you believe that the software has crashed, please send "
					+ "an e-mail", 5, 295);
			graphics2D.drawString("providing details of the crash to ramonegraham@gmail.com "
					+ "with the", 5, 310);
			graphics2D.drawString("Subject name \"PXPA [version] crash\".", 5, 325);
			graphics2D.drawRect(367, 309, 269, 1);
			
			graphics2D.drawString("Please be advised.", 5, 355);
			graphics2D.drawString("- Ramone Graham (Sweetboy13735) - Founder of HBI and Lead "
					+ "Director of", 5, 370);
			graphics2D.drawString("Pixel Paintballers", 15, 385);
			
			graphics2D.setFont(titleSelect);
			graphics2D.setColor(WHITE);
			
			String footer = "Click the screen to continue";
			Rectangle2D footerBounds = titleSelectMetrics.getStringBounds(footer, graphics2D);
			
			graphics2D.drawString("Click the screen to continue",
					(int) (400-footerBounds.getWidth()/2), Game.HEIGHT-5);
			
			graphics2D.drawImage(companyLogo, 0, logoY, Game.WIDTH, Game.HEIGHT, game);
			
			break;
		case TITLE_SCREEN:
			graphics2D.drawImage(gameLogo, 0, 0, Game.WIDTH, Game.HEIGHT, game);
			
			renderButton(graphics2D, Game.WIDTH/2-125/2, Game.HEIGHT/2-75, 125, 50, "Play",
					titleSelect);
			
			renderButton(graphics2D, Game.WIDTH/2-125/2, Game.HEIGHT/2+25, 125, 50, "Quit",
					titleSelect);
			
			break;
		case MAIN_MENU:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			header = "Main menu";
			headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
			
			graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2), 40);
			
			renderButton(graphics2D, 5, 100, 250, 60, "Singleplayer", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Fend off the waves of enemies with your trusty", 260, 115);
			graphics2D.drawString("paintball gun alone!", 260, 130);

			renderButton(graphics2D, 5, 175, 250, 60, "Multiplayer", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("There's nothing better than splatting every-", 260, 190);
			graphics2D.drawString("thing but doing it with friends!", 260, 205);
			
			renderButton(graphics2D, 5, 250, 250, 60, "Customisation", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Be your own person. Change your looks and name", 260, 265);
			graphics2D.drawString("here!", 260, 280);
			
			renderButton(graphics2D, 5, 325, 250, 60, "Options", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Make our game your own. Change any video, sound", 260, 340);
			graphics2D.drawString("and control customisations here!", 260, 355);
			
			renderButton(graphics2D, 5, 400, 250, 60, "Info and credits", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Learn the ropes of combat, or find out who helped", 260,
					415);
			graphics2D.drawString("bring this game to life!", 260, 430);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			break;
		case DIFFICULTY_SELECT:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			header = "Select difficulty";
			headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
			
			graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2), 40);
			
			renderButton(graphics2D, 5, 100, 200, 60, "Easy", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: Infinite (No reloads)", 210, 115);
			graphics2D.drawString("Enemy fire rate: 7 secs", 210, 130);
			graphics2D.drawString("A good start for beginners.", 210, 145);

			renderButton(graphics2D, 5, 175, 200, 60, "Normal", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: 30", 210, 190);
			graphics2D.drawString("Reload ammo: 15", 210, 205);
			graphics2D.drawString("Enemy fire rate: 5 secs", 210, 220);
			graphics2D.drawString("The full basic experience.", 210, 235);

			renderButton(graphics2D, 5, 250, 200, 60, "Hard", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: 20", 210, 265);
			graphics2D.drawString("Reload ammo: 10", 210, 280);
			graphics2D.drawString("Enemy fire rate: 3 secs", 210, 295);
			graphics2D.drawString("Provides a good challenge.", 210, 310);

			renderButton(graphics2D, 5, 325, 200, 60, "Extreme", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: 10", 210, 340);
			graphics2D.drawString("Reload ammo: 5", 210, 355);
			graphics2D.drawString("Enemy fire rate: 2 secs", 210, 370);
			graphics2D.setColor(RED);
			graphics2D.drawString("WARNING: For true professionals only!", 210, 385);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			break;
		case GAME:
			if (Game.paused) {
				graphics2D.setFont(menuHeader);
				graphics2D.setColor(WHITE);

				header = "Paused";
				headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
				
				graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2),
						40);
				
				renderButton(graphics2D, Game.WIDTH/2-175/2, Game.HEIGHT/2-75, 175, 50,
						"Resume", menuSelect);
				
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					renderButton(graphics2D, Game.WIDTH/2-175/2, Game.HEIGHT/2+25, 175, 50,
							"Quit", menuSelect);
					
					break;
				case MULTIPLAYER:
					renderButton(graphics2D, Game.WIDTH/2-175/2, Game.HEIGHT/2+25, 175, 50,
							"Disconnect", menuSelect);
					
					break;
				}
			}
			
			break;
		case GAME_OVER:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				header = "Game over!";
				headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
				
				graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2),
						40);
				
				break;
			case MULTIPLAYER:
				header = "You died!";
				headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
				
				graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2),
						40);
				
				break;
			}
			
			graphics2D.setFont(menuSelect);
			graphics2D.drawString("Game stats", 5, 100);
			
			graphics2D.setFont(menuText);
			
			if (Game.gameMode == Game.Mode.SINGLEPLAYER) {
				if (highscore) {
					graphics2D.setColor(YELLOW);
					graphics2D.drawString("Your final score was: " + Game.player.score +
							"! HIGHSCORE!!!", 5, 125);
					graphics2D.setColor(WHITE);
				} else graphics2D.drawString("Your final score was: " + Game.player.score, 5,
						125);
				
				graphics2D.drawString("Your final level was: " + Spawner.level, 5, 140);
				graphics2D.drawString("Your chosen difficulty: " + Game.gameDifficulty.name(),
						5, 155);
			}
			
			graphics2D.drawString("Paintballs shot in this game: " + HeadsUpDisplay.shots, 5,
					170);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				graphics2D.drawString("Enemies killed: " + HeadsUpDisplay.kills, 5, 185);
				
				break;
			case MULTIPLAYER:
				if (Server.gameMode == Server.Mode.TEAMSURVIVAL)
					graphics2D.drawString("Enemies killed: " + HeadsUpDisplay.kills, 5, 185);
				
				break;
			}
			
			graphics2D.setFont(menuSelect);
			graphics2D.drawString("So, what now?", 5, 210);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				renderButton(graphics2D, 5, 220, 200, 60, "Play again", menuSelect);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Play another game using the same settings.", 210, 235);
				
				break;
			case MULTIPLAYER:
				renderButton(graphics2D, 5, 220, 200, 60, "Rejoin game", menuSelect);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Jump right back into the multiplayer mayhem!", 210, 235);
				
				break;
			}
			
			renderButton(graphics2D, 5, 295, 200, 60, "", menuSelect);
			
			String label;
			Rectangle2D labelBounds;
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				label = "Change";
				labelBounds = menuSelectMetrics.getStringBounds(label, graphics2D);
				
				graphics2D.drawString(label, (int) (105-labelBounds.getWidth()/2),
						(int) (335-labelBounds.getHeight()/2));
				
				label = "difficulty";
				labelBounds = menuSelectMetrics.getStringBounds(label, graphics2D);
				
				graphics2D.drawString(label, (int) (105-labelBounds.getWidth()/2),
						(int) (355-labelBounds.getHeight()/2));
				
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Play another game with a different difficulty.", 210,
						310);
				
				break;
			case MULTIPLAYER:
				label = "Rejoin as";
				labelBounds = menuSelectMetrics.getStringBounds(label, graphics2D);
				
				graphics2D.drawString(label, (int) (105-labelBounds.getWidth()/2),
						(int) (335-labelBounds.getHeight()/2));
				
				label = "spectator";
				labelBounds = menuSelectMetrics.getStringBounds(label, graphics2D);
				
				graphics2D.drawString(label, (int) (105-labelBounds.getWidth()/2),
						(int) (355-labelBounds.getHeight()/2));
				
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Relax and watch the rest of the chaos ensue!", 210, 310);
				
				break;
			}
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				renderButton(graphics2D, 5, 370, 200, 60, "Main menu", menuSelect);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Return to the main menu.", 210, 385);
				
				break;
			case MULTIPLAYER:
				renderButton(graphics2D, 5, 370, 200, 60, "Leave game", menuSelect);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Return to the multiplayer menu.", 210, 385);
				
				break;
			}
			
			break;
		case MULTIPLAYER_MENU:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);

			header = "Multiplayer";
			headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
			
			graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2), 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Create a server", 5, 100);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Select a gamemode", 5, 125);
			
			renderButton(graphics2D, 5, 135, 250, 60, "PVP", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("It's a free-for-all as a maximum of 4 players", 260, 150);
			graphics2D.drawString("battle it out against each other!", 260, 165);
			
			renderButton(graphics2D, 5, 210, 250, 60, "Team Survival", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Band together as a maximum of 4 players battle", 260, 225);
			graphics2D.drawString("the enemy waves. Careful tough, friendly fire is", 260, 240);
			graphics2D.drawString("enabled!", 260, 255);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Connect to a server", 5, 300);
			
			renderButton(graphics2D, 5, 310, 250, 30, "Change address", menuSelect);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Change the target server's IP address", 260, 325);
			graphics2D.drawString("Target server address: " +
					game.client.getTargetIPAddress().getHostAddress(), 260, 340);
			
			renderButton(graphics2D, 5, 355, 250, 60, "Join game", menuSelect);
			
			renderButton(graphics2D, 260, 355, 250, 60, "Spectate game", menuSelect);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			break;
		case SERVER_CONNECTION:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Connecting to server...", 38, 40);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("The game is currently attempting to connect to the server. "
					+ "Once it has", 5, 100);
			graphics2D.drawString("access, you will automatically be brought into the game.", 5,
					115);
			
			graphics2D.drawString("The process shouldn't take long, unless you have a slow "
					+ "connection,", 5, 145);
			graphics2D.drawString("the server's physical location is in a far region, or the "
					+ "server is offline.", 5, 160);
			
			graphics2D.drawString("If a few minutes have passed and you still haven't "
					+ "connected, either try", 5, 190);
			graphics2D.drawString("and reconnect, or contact the player who is running the "
					+ "server to see", 5, 205);
			graphics2D.drawString("if it is online.", 5, 220);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-106, 150, 50, "Retry",
					menuSelect);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			break;
		case CUSTOMISATION:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			header = "Customisation";
			headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
			
			graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2), 40);
			
			renderButton(graphics2D, 5, 150, 250, 50, (edit?"Save username":"Change username"),
					menuSelect);
			
			renderButton(graphics2D, 260, 150, 100, 50, "Reset", menuSelect);
			
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Select fill colour", 5, 230);
			
			graphics2D.setColor(CYAN);
			graphics2D.fillRect(5, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 240, 32, 32);
			
			graphics2D.setColor(BLUE);
			graphics2D.fillRect(40, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(40, 240, 32, 32);
			
			graphics2D.setColor(DARK_GRAY);
			graphics2D.fillRect(75, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(75, 240, 32, 32);
			
			graphics2D.setColor(GRAY);
			graphics2D.fillRect(110, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(110, 240, 32, 32);
			
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(145, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(145, 240, 32, 32);
			
			graphics2D.setColor(LIGHT_GRAY);
			graphics2D.fillRect(180, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(180, 240, 32, 32);
			
			graphics2D.setColor(MAGENTA);
			graphics2D.fillRect(215, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(215, 240, 32, 32);
			
			graphics2D.setColor(ORANGE);
			graphics2D.fillRect(250, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(250, 240, 32, 32);
			
			graphics2D.setColor(PINK);
			graphics2D.fillRect(285, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(285, 240, 32, 32);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(320, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(320, 240, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.fillRect(355, 240, 32, 32);
			graphics2D.drawRect(355, 240, 32, 32);
			
			graphics2D.setColor(YELLOW);
			graphics2D.fillRect(390, 240, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(390, 240, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Select outline colour", 5, 305);
			
			graphics2D.setColor(CYAN);
			graphics2D.fillRect(5, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 315, 32, 32);
			
			graphics2D.setColor(BLUE);
			graphics2D.fillRect(40, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(40, 315, 32, 32);
			
			graphics2D.setColor(DARK_GRAY);
			graphics2D.fillRect(75, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(75, 315, 32, 32);
			
			graphics2D.setColor(GRAY);
			graphics2D.fillRect(110, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(110, 315, 32, 32);
			
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(145, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(145, 315, 32, 32);
			
			graphics2D.setColor(LIGHT_GRAY);
			graphics2D.fillRect(180, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(180, 315, 32, 32);
			
			graphics2D.setColor(MAGENTA);
			graphics2D.fillRect(215, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(215, 315, 32, 32);
			
			graphics2D.setColor(ORANGE);
			graphics2D.fillRect(250, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(250, 315, 32, 32);
			
			graphics2D.setColor(PINK);
			graphics2D.fillRect(285, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(285, 315, 32, 32);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(320, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(320, 315, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.fillRect(355, 315, 32, 32);
			graphics2D.drawRect(355, 315, 32, 32);
			
			graphics2D.setColor(YELLOW);
			graphics2D.fillRect(390, 315, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(390, 315, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Select username colour", 5, 380);
			
			graphics2D.setColor(CYAN);
			graphics2D.fillRect(5, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 390, 32, 32);
			
			graphics2D.setColor(BLUE);
			graphics2D.fillRect(40, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(40, 390, 32, 32);
			
			graphics2D.setColor(DARK_GRAY);
			graphics2D.fillRect(75, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(75, 390, 32, 32);
			
			graphics2D.setColor(GRAY);
			graphics2D.fillRect(110, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(110, 390, 32, 32);
			
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(145, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(145, 390, 32, 32);
			
			graphics2D.setColor(LIGHT_GRAY);
			graphics2D.fillRect(180, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(180, 390, 32, 32);
			
			graphics2D.setColor(MAGENTA);
			graphics2D.fillRect(215, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(215, 390, 32, 32);
			
			graphics2D.setColor(ORANGE);
			graphics2D.fillRect(250, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(250, 390, 32, 32);
			
			graphics2D.setColor(PINK);
			graphics2D.fillRect(285, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(285, 390, 32, 32);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(320, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(320, 390, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.fillRect(355, 390, 32, 32);
			graphics2D.drawRect(355, 390, 32, 32);
			
			graphics2D.setColor(YELLOW);
			graphics2D.fillRect(390, 390, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(390, 390, 32, 32);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			break;
		case OPTIONS:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			header = "Options";
			headerBounds = menuHeaderMetrics.getStringBounds(header, graphics2D);
			
			graphics2D.drawString(header, (int) (Game.WIDTH/2-headerBounds.getWidth()/2), 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.drawString("Video", 5, 100);
			
			renderButton(graphics2D, 5, 110, 250, 30, "Fullscreen: " +
					(game.window.fullscreen?"On":"Off"), menuSelect);
			
			renderButton(graphics2D, 260, 110, 325, 30, "Change display mode", menuSelect);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Current mode:", 590, 125);
			
			DisplayMode mode = game.window.fullscreenMode;
			graphics2D.drawString(mode.getWidth() + " x " + mode.getHeight() + " @ " +
					mode.getRefreshRate() + "Hz", 590, 140);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Sound", 5, 170);

			renderButton(graphics2D, 5, 180, 25, 50, "<-", menuSelect);

			graphics2D.setColor(GRAY);
			graphics2D.fillRect(35, 180, 200, 50);
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(35, 180, (int) (Game.musicVolume*200), 50);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(35, 180, 200, 50);
			graphics2D.setColor(BLUE);
			
			label = "Music";
			labelBounds = menuSelectMetrics.getStringBounds(label, graphics2D);
			
			graphics2D.drawString(label, (int) (135-labelBounds.getWidth()/2+1),
					(int) (205+labelBounds.getHeight()/2));
			
			renderButton(graphics2D, 240, 180, 25, 50, "->", menuSelect);

			renderButton(graphics2D, 275, 180, 25, 50, "<-", menuSelect);

			graphics2D.setColor(GRAY);
			graphics2D.fillRect(305, 180, 200, 50);
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(305, 180, (int) (Game.sfxVolume*200), 50);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(305, 180, 200, 50);
			graphics2D.setColor(BLUE);
			
			label = "SFX";
			labelBounds = menuSelectMetrics.getStringBounds(label, graphics2D);
			
			graphics2D.drawString(label, (int) (405-labelBounds.getWidth()/2+1),
					(int) (205+labelBounds.getHeight()/2));
			
			renderButton(graphics2D, 510, 180, 25, 50, "->", menuSelect);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Controls", 5, 260);
			
			renderButton(graphics2D, 5, 270, 325, 25,
					(KeyInput.up.edit?"Listening...":"Up: " +
							KeyEvent.getKeyText(KeyInput.up.keyCode)), menuSelect);
			
			renderButton(graphics2D, 335, 270, 325, 25,
					(KeyInput.down.edit?"Listening...":"Down: " +
							KeyEvent.getKeyText(KeyInput.down.keyCode)), menuSelect);
			
			renderButton(graphics2D, 5, 300, 325, 25,
					(KeyInput.left.edit?"Listening...":"Left: " +
							KeyEvent.getKeyText(KeyInput.left.keyCode)), menuSelect);
			
			renderButton(graphics2D, 335, 300, 325, 25,
					(KeyInput.right.edit?"Listening...":"Right: " +
							KeyEvent.getKeyText(KeyInput.right.keyCode)), menuSelect);
			
			renderButton(graphics2D, 5, 330, 325, 25,
					(KeyInput.reload.edit?"Listening...":"Reload: " +
							KeyEvent.getKeyText(KeyInput.reload.keyCode)), menuSelect);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			break;
		case INFO:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Information and credits", 23, 40);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("The \"aim\" of the game is to shoot down your enenmies with "
					+ "paintballs,", 5, 100);
			graphics2D.drawString("whilst dodging their own attacks.", 5, 115);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Controls", 5, 150);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Movement: WASD keys (Customisable)", 5, 175);
			graphics2D.drawString("Shoot paintball: Left click", 5, 190);
			graphics2D.drawString("Reload Ammo (Difficulties Normal and above): R key "
					+ "(Customisable)", 5, 205);
			graphics2D.drawString("Pause: ESC key", 5, 220);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Game development", 5, 255);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("General director and programming: Sweetboy13735", 5, 280);
			graphics2D.drawString("Game mechanics and ideas: FateAssassin", 5, 295);
			graphics2D.drawString("Game testing: B-Clark7698", 5, 310);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Music and sounds", 5, 345);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(RED);
			graphics2D.drawString("DISCLAIMER: HyperByte Industries does not own any of the "
					+ "music used.", 5, 370);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Title screen: MDK - Press Start", 5, 385);
			graphics2D.drawString("    - YouTube: https://www.youtube.com/MDKOfficialYT", 5,
					400);
			graphics2D.drawRect(35, 399, 530, 1);
			graphics2D.drawString("    - Facebook: https://www.facebook.com/MDKOfficial", 5,
					415);
			graphics2D.drawRect(35, 414, 528, 1);
			graphics2D.drawString("    - Buy the song here: "
					+ "https://www.mdkofficial.bandcamp.com", 5, 430);
			graphics2D.drawRect(35, 429, 617, 1);
			graphics2D.drawString("    - Free Download: "
					+ "https://www.morgandavidking.com/free-downloads", 5, 445);
			graphics2D.drawRect(35, 444, 712, 1);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			renderButton(graphics2D, Game.WIDTH/2+80, Game.HEIGHT-51, 150, 50, "Page 2",
					menuSelect);
			
			break;
		case INFO_2:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Information and credits", 23, 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Music and sounds (Contiued)", 5, 100);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(RED);
			graphics2D.drawString("DISCLAIMER: HyperByte Industries does not own any of the "
					+ "music used.", 5, 125);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Menu screen: Mario Kart 7 - Menu themes 1, 2 & 3", 5, 140);
			graphics2D.drawString("Singleplayer game: Oscillator Z - Break it down", 5, 155);
			graphics2D.drawString("Multiplayer game: Wildfellas & TRAPECIA - Blow up", 5, 170);
			graphics2D.drawString("Menu select sound: Hitmarker", 5, 185);
			graphics2D.drawString("Paintball shot sound: Intervention 420", 5, 200);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Statistics", 5, 235);
			
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Highscore (Easy): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.EASY.name() + ")"),
					5, 260);
			graphics2D.drawString("Highscore (Normal): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.NORMAL.name() +
							")"), 5, 275);
			graphics2D.drawString("Highscore (Hard): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.HARD.name() + ")"),
					5, 290);
			graphics2D.drawString("Highscore (Extreme): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.EXTREME.name() +
							")"), 5, 305);
			graphics2D.drawString("Time played: " + DataManager.timePlayed[2] + ":" +
							DataManager.timePlayed[1] + ":" + DataManager.timePlayed[0], 5,
							320);
			graphics2D.drawString("Games played: " + DataManager.getStatistic("Games played"),
					5, 335);
			graphics2D.drawString("Shots fired: " + DataManager.getStatistic("Shots fired"), 5,
					350);
			graphics2D.drawString("Total kills: " + DataManager.getStatistic("Total kills"), 5,
					365);
			
			renderButton(graphics2D, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50, "Back",
					menuSelect);
			
			renderButton(graphics2D, Game.WIDTH/2-230, Game.HEIGHT-51, 150, 50, "Page 1",
					menuSelect);
			
			break;
		}
	}

	// Invoked when the mouse is pressed.
	public void mousePressed(MouseEvent event) {
		float mouseX = (float) event.getX();
		float mouseY = (float) event.getY();
		
		switch (menuState) {
		case LOGO:
			if (clickable) {
				if (mouseOver(mouseX, mouseY, 98, 179, 636, 10)) {
					try {
						Desktop.getDesktop().browse(new URI("https://hyperbyteindustries."
								+ "github.io/Pixel-Paintballers"));
					} catch (IOException | URISyntaxException exception) {
						System.err.print(new Date() + " " + ERROR_PREFIX +
								"An exception occured whilst attempting to browse to a webpage "
								+ "- ");
						exception.printStackTrace();
					}
				} else if (mouseOver(mouseX, mouseY, 367, 299, 269, 10)) {
					try {
						Desktop.getDesktop().mail(new URI("mailto:ramonegraham@gmail.com"));
					} catch (IOException | URISyntaxException exception) {
						System.err.print(new Date() + " " + ERROR_PREFIX +
								"An exception occured whilst attempting to open an email "
								+ "client - ");
						exception.printStackTrace();
					}
				} else {
					menuState = State.TITLE_SCREEN;
				}
			}
			
			break;
		case TITLE_SCREEN:
			if (mouseOver(mouseX, mouseY, Game.WIDTH/2-125/2, Game.HEIGHT/2-75, 125, 50))
				menuState = State.MAIN_MENU;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-125/2, Game.HEIGHT/2+25, 125, 50)) {
				DataManager.saveData();
				
				System.exit(1);
			}
			
			break;
		case MAIN_MENU:
			if (mouseOver(mouseX, mouseY, 5, 100, 250, 60))
				menuState = State.DIFFICULTY_SELECT;
			else if (mouseOver(mouseX, mouseY, 5, 175, 250, 60))
				menuState = State.MULTIPLAYER_MENU;
			else if (mouseOver(mouseX, mouseY, 5, 250, 250, 60)) {
				Game.player.setX(Game.WIDTH/2-16);
				Game.player.setY(100);
				handler.addEntity(Game.player);
				
				menuState = State.CUSTOMISATION;
			} else if (mouseOver(mouseX, mouseY, 5, 325, 250, 60)) menuState = State.OPTIONS;
			else if (mouseOver(mouseX, mouseY, 5, 400, 250, 60)) menuState = State.INFO;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-61, 150, 60))
				menuState = State.TITLE_SCREEN;
			
			break;
		case DIFFICULTY_SELECT:
			if (mouseOver(mouseX, mouseY, 5, 100, 200, 60)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 175, 200, 60)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.NORMAL;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 250, 200, 60)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.HARD;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 325, 200, 60)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.EXTREME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-61, 150, 60))
				menuState = State.MAIN_MENU;
			break;
		case GAME:
			if (!(Game.paused)) {
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					if (!(HeadsUpDisplay.ammo == 0) && !HeadsUpDisplay.reloading) {
						Paintball paintball = new Paintball(ID.PAINTBALL, Game.player.getX()+12,
								Game.player.getY()+12, game, handler, Game.player);
						
						handler.addEntity(paintball);
						
						float diffX = paintball.getX() - mouseX + 4,
								diffY = paintball.getY() - mouseY + 4,
								distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
						
						paintball.setVelX(-1/distance*diffX);
						paintball.setVelY(-1/distance*diffY);
						
						if (Game.gameDifficulty != Difficulty.EASY) HeadsUpDisplay.ammo--;
						
						HeadsUpDisplay.shots++;
						
						AudioManager.getSound("Shot").play(1, Game.sfxVolume);
					}
					
					break;
				case MULTIPLAYER:
					if (!Game.player.spectator) {
						if (!(HeadsUpDisplay.ammo == 0) && !HeadsUpDisplay.reloading) {
							Paintball paintball = new Paintball(ID.PAINTBALL,
									Game.player.getX()+12, Game.player.getY()+12, game, handler,
									Game.player);
							
							handler.addEntity(paintball);
							
							float diffX = paintball.getX() - mouseX + 4,
									diffY = paintball.getY() - mouseY + 4,
									distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
							
							paintball.setVelX(-1/distance*diffX);
							paintball.setVelY(-1/distance*diffY);
							
							Packet03PlayerShot packet =
									new Packet03PlayerShot(Game.player.getUsername(),
											paintball.getX(), paintball.getY(),
											paintball.getVelX(), paintball.getVelY());
							packet.writeData(game.client);
							
							HeadsUpDisplay.shots++;
							
							AudioManager.getSound("Shot").play(1, Game.sfxVolume);
						}
					}
					
					break;
				}
			} else {
				if (mouseOver(mouseX, mouseY, Game.WIDTH/2-175/2, Game.HEIGHT/2-75, 175, 50))
					Game.paused = false;
				else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-175/2, Game.HEIGHT/2+25, 175,
						50)) {
					Game.paused = false;
					handler.getEntities().clear();
					
					if (Game.gameMode == Game.Mode.MULTIPLAYER) {
						Packet01Disconnect packet =
								new Packet01Disconnect(Game.player.getUsername());
						packet.writeData(game.client);
						
						if (!(game.server == null)) game.server.stop();
					}
					
					menuState = State.MAIN_MENU;
				}
			}
			
			break;
		case GAME_OVER:
			if (mouseOver(mouseX, mouseY, 5, 220, 200, 60)) {
				if (Game.gameMode == Game.Mode.MULTIPLAYER) {
					Game.player.health = 100;
					
					Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
							Game.player.getX(), Game.player.getY(), Game.player.getFillColour(),
							Game.player.getOutlineColour(), Game.player.getUsernameColour(),
							Game.player.health, true, Game.player.spectator);
					packet.writeData(game.client);
				}
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 295, 200, 60)) {
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					menuState = State.DIFFICULTY_SELECT;
					
					break;
				case MULTIPLAYER:
					Game.player.health = 100;
					Game.player.spectator = true;
					
					Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
							Game.player.getX(), Game.player.getY(), Game.player.getFillColour(),
							Game.player.getOutlineColour(), Game.player.getUsernameColour(),
							Game.player.health, true, Game.player.spectator);
					packet.writeData(game.client);
					
					handler.startGame();
					
					break;
				}
			} else if (mouseOver(mouseX, mouseY, 5, 370, 200, 60)) {
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					menuState = State.MAIN_MENU;
					
					break;
				case MULTIPLAYER:
					Packet01Disconnect packet =
							new Packet01Disconnect(Game.player.getUsername());
					packet.writeData(game.client);
					
					if (!(game.server == null)) game.server.stop();
					
					break;
				}
			}
			
			break;
		case MULTIPLAYER_MENU:
			if (mouseOver(mouseX, mouseY, 5, 135, 250, 60)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Server.gameMode = Server.Mode.PVP;
				
				Game.player.spectator = false;
				
				game.server = new Server();
				game.server.start();
				
				try {
					game.client.setTargetIPAddress(InetAddress.getByName("localhost"));
				} catch (UnknownHostException exception) {
					System.err.print(new Date() + " " + ERROR_PREFIX +
							"An exception occured whilst resolving an IP address - ");
					exception.printStackTrace();
				}
				
				game.client.pingServer();
				
				menuState = State.SERVER_CONNECTION;
			} else if (mouseOver(mouseX, mouseY, 5, 210, 250, 60)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Server.gameMode = Server.Mode.TEAMSURVIVAL;
				
				Game.player.spectator = false;
				
				game.server = new Server();
				game.server.start();
				
				try {
					game.client.setTargetIPAddress(InetAddress.getByName("localhost"));
				} catch (UnknownHostException exception) {
					System.err.print(new Date() + " " + ERROR_PREFIX +
							"An exception occured whilst resolving an IP address - ");
					exception.printStackTrace();
				}
				
				game.client.pingServer();
				
				menuState = State.SERVER_CONNECTION;
			} else if (mouseOver(mouseX, mouseY, 5, 310, 250, 30)) setTargetIPAddress();
			else if (mouseOver(mouseX, mouseY, 5, 355, 256, 64)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Game.player.spectator = false;
				
				game.client.pingServer();
				
				menuState = State.SERVER_CONNECTION;
			} else if (mouseOver(mouseX, mouseY, 260, 355, 256, 64)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Game.player.spectator = true;
				
				game.client.pingServer();
				
				menuState = State.SERVER_CONNECTION;
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-61, 150, 60))
				menuState = State.MAIN_MENU;
			
			break;
		case SERVER_CONNECTION:
			if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-106, 150, 50))
				game.client.pingServer();
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50))
				menuState = State.MULTIPLAYER_MENU;
			
			break;
		case CUSTOMISATION:
			if (mouseOver(mouseX, mouseY, 5, 150, 250, 50)) {
				if (edit) {
					edit = false;
					
					if (Game.player.getUsername() == null ||
							Game.player.getUsername().length() == 0)
						Game.player.setUsername("Player");
				} else edit = true;
			} else if (mouseOver(mouseX, mouseY, 260, 150, 100, 50)) {
				Game.player.setUsername("Player");
				Game.player.setFillColour(RED);
				Game.player.setOutlineColour(WHITE);
				Game.player.setUsernameColour(GRAY);
			} else if (mouseOver(mouseX, mouseY, 5, 240, 32, 32))
				Game.player.setFillColour(CYAN);
			else if (mouseOver(mouseX, mouseY, 40, 240, 32, 32))
				Game.player.setFillColour(BLUE);
			else if (mouseOver(mouseX, mouseY, 75, 240, 32, 32))
				Game.player.setFillColour(DARK_GRAY);
			else if (mouseOver(mouseX, mouseY, 110, 240, 32, 32))
				Game.player.setFillColour(GRAY);
			else if (mouseOver(mouseX, mouseY, 145, 240, 32, 32))
				Game.player.setFillColour(GREEN);
			else if (mouseOver(mouseX, mouseY, 180, 240, 32, 32))
				Game.player.setFillColour(LIGHT_GRAY);
			else if (mouseOver(mouseX, mouseY, 215, 240, 32, 32))
				Game.player.setFillColour(MAGENTA);
			else if (mouseOver(mouseX, mouseY, 250, 240, 32, 32))
				Game.player.setFillColour(ORANGE);
			else if (mouseOver(mouseX, mouseY, 285, 240, 32, 32))
				Game.player.setFillColour(PINK);
			else if (mouseOver(mouseX, mouseY, 320, 240, 32, 32))
				Game.player.setFillColour(RED);
			else if (mouseOver(mouseX, mouseY, 355, 240, 32, 32))
				Game.player.setFillColour(WHITE);
			else if (mouseOver(mouseX, mouseY, 390, 240, 32, 32))
				Game.player.setFillColour(YELLOW);
			else if (mouseOver(mouseX, mouseY, 5, 315, 32, 32))
				Game.player.setOutlineColour(CYAN);
			else if (mouseOver(mouseX, mouseY, 40, 315, 32, 32))
				Game.player.setOutlineColour(BLUE);
			else if (mouseOver(mouseX, mouseY, 75, 315, 32, 32))
				Game.player.setOutlineColour(DARK_GRAY);
			else if (mouseOver(mouseX, mouseY, 110, 315, 32, 32))
				Game.player.setOutlineColour(GRAY);
			else if (mouseOver(mouseX, mouseY, 145, 315, 32, 32))
				Game.player.setOutlineColour(GREEN);
			else if (mouseOver(mouseX, mouseY, 180, 315, 32, 32))
				Game.player.setOutlineColour(LIGHT_GRAY);
			else if (mouseOver(mouseX, mouseY, 215, 315, 32, 32))
				Game.player.setOutlineColour(MAGENTA);
			else if (mouseOver(mouseX, mouseY, 250, 315, 32, 32))
				Game.player.setOutlineColour(ORANGE);
			else if (mouseOver(mouseX, mouseY, 285, 315, 32, 32))
				Game.player.setOutlineColour(PINK);
			else if (mouseOver(mouseX, mouseY, 320, 315, 32, 32))
				Game.player.setOutlineColour(RED);
			else if (mouseOver(mouseX, mouseY, 355, 315, 32, 32))
				Game.player.setOutlineColour(WHITE);
			else if (mouseOver(mouseX, mouseY, 390, 315, 32, 32))
				Game.player.setOutlineColour(YELLOW);
			else if (mouseOver(mouseX, mouseY, 5, 390, 32, 32))
				Game.player.setUsernameColour(CYAN);
			else if (mouseOver(mouseX, mouseY, 40, 390, 32, 32))
				Game.player.setUsernameColour(BLUE);
			else if (mouseOver(mouseX, mouseY, 75, 390, 32, 32))
				Game.player.setUsernameColour(DARK_GRAY);
			else if (mouseOver(mouseX, mouseY, 110, 390, 32, 32))
				Game.player.setUsernameColour(GRAY);
			else if (mouseOver(mouseX, mouseY, 145, 390, 32, 32))
				Game.player.setUsernameColour(GREEN);
			else if (mouseOver(mouseX, mouseY, 180, 390, 32, 32))
				Game.player.setUsernameColour(LIGHT_GRAY);
			else if (mouseOver(mouseX, mouseY, 215, 390, 32, 32))
				Game.player.setUsernameColour(MAGENTA);
			else if (mouseOver(mouseX, mouseY, 250, 390, 32, 32))
				Game.player.setUsernameColour(ORANGE);
			else if (mouseOver(mouseX, mouseY, 285, 390, 32, 32))
				Game.player.setUsernameColour(PINK);
			else if (mouseOver(mouseX, mouseY, 320, 390, 32, 32))
				Game.player.setUsernameColour(RED);
			else if (mouseOver(mouseX, mouseY, 355, 390, 32, 32))
				Game.player.setUsernameColour(WHITE);
			else if (mouseOver(mouseX, mouseY, 390, 390, 32, 32))
				Game.player.setUsernameColour(YELLOW);
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50)) {
				if (edit) AudioManager.getSound("Denied").play(1, Game.sfxVolume);
				else {
					handler.removeEntity(Game.player);
					
					DataManager.saveData();
					
					menuState = State.MAIN_MENU;
				}
			}
			
			break;
		case OPTIONS:
			if (mouseOver(mouseX, mouseY, 5, 110, 250, 30)) {
				GraphicsEnvironment environment =
						GraphicsEnvironment.getLocalGraphicsEnvironment();
				
				if (game.window.fullscreen) {
					environment.getDefaultScreenDevice().setFullScreenWindow(null);
					
					game.window.fullscreen = false;
				} else {
					environment.getDefaultScreenDevice().setFullScreenWindow(game.window);
					environment.getDefaultScreenDevice().
							setDisplayMode(game.window.fullscreenMode);
					
					game.window.fullscreen = true;
				}
			} else if (mouseOver(mouseX, mouseY, 260, 110, 325, 30)) {
				for (int i = 0; i < game.window.fullscreenModes.size(); i++) {
					DisplayMode mode = game.window.fullscreenModes.get(i);
					
					if (game.window.fullscreenMode.equals(mode)) {
						if (i+1 >= game.window.fullscreenModes.size())
							game.window.fullscreenMode = game.window.fullscreenModes.get(0);
						else game.window.fullscreenMode = game.window.fullscreenModes.get(i+1);
						
						if (game.window.fullscreen) {
							GraphicsEnvironment environment =
									GraphicsEnvironment.getLocalGraphicsEnvironment();
							environment.getDefaultScreenDevice().
									setDisplayMode(game.window.fullscreenMode);
						}
						
						break;
					}
				}
			} else if (mouseOver(mouseX, mouseY, 5, 180, 25, 50)) Game.musicVolume -= 0.1f;
			else if (mouseOver(mouseX, mouseY, 240, 180, 25, 50)) Game.musicVolume += 0.1f;
			else if (mouseOver(mouseX, mouseY, 275, 180, 25, 50)) Game.sfxVolume -= 0.1f;
			else if (mouseOver(mouseX, mouseY, 510, 180, 25, 50)) Game.sfxVolume += 0.1f;
			else if (mouseOver(mouseX, mouseY, 5, 270, 325, 25)) {
				if (KeyInput.up.edit) KeyInput.up.edit = false;
				else KeyInput.up.edit = true;
			} else if (mouseOver(mouseX, mouseY, 335, 270, 325, 25)) {
				if (KeyInput.down.edit) KeyInput.down.edit = false;
				else KeyInput.down.edit = true;
			} else if (mouseOver(mouseX, mouseY, 5, 300, 325, 25)) {
				if (KeyInput.left.edit) KeyInput.left.edit = false;
				else KeyInput.left.edit = true;
			} else if (mouseOver(mouseX, mouseY, 335, 300, 325, 25)) {
				if (KeyInput.right.edit) KeyInput.right.edit = false;
				else KeyInput.right.edit = true;
			} else if (mouseOver(mouseX, mouseY, 5, 330, 325, 25)) {
				if (KeyInput.reload.edit) KeyInput.reload.edit = false;
				else KeyInput.reload.edit = true;
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50))
				menuState = State.MAIN_MENU;
			
			break;
		case INFO:
			if (mouseOver(mouseX, mouseY, 36, 389, 529, 10)) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.youtube.com/"
							+ "MDKOfficialYT"));
				} catch (IOException | URISyntaxException exception) {
					System.err.print(new Date() + " " + ERROR_PREFIX +
							"An exception occured whilst attempting to browse to a webpage - ");
					exception.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, 35, 404, 528, 10)) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.facebook.com/"
							+ "MDKOfficial"));
				} catch (IOException | URISyntaxException exception) {
					System.err.print(new Date() + " " + ERROR_PREFIX +
							"An exception occured whilst attempting to browse to a webpage - ");
					exception.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, 35, 419, 617, 10)) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.mdkofficial.bandcamp."
							+ "com"));
				} catch (IOException | URISyntaxException exception) {
					System.err.print(new Date() + " " + ERROR_PREFIX +
							"An exception occured whilst attempting to browse to a webpage - ");
					exception.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, 35, 434, 712, 10)) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.morgandavidking.com/"
							+ "free-downloads"));
				} catch (IOException | URISyntaxException exception) {
					System.err.print(new Date() + " " + ERROR_PREFIX +
							"An exception occured whilst attempting to browse to a webpage - ");
					exception.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50))
				menuState = State.MAIN_MENU;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2+80, Game.HEIGHT-51, 150, 50))
				menuState = State.INFO_2;
			
			break;
		case INFO_2:
			if (mouseOver(mouseX, mouseY, Game.WIDTH/2-75, Game.HEIGHT-51, 150, 50))
				menuState = State.MAIN_MENU;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-230, Game.HEIGHT-51, 150, 50))
				menuState = State.INFO;
			
			break;
		}
	}
	
	/**
	 * Renders a menu button.
	 * @param graphics2D - The graphics context used to update the button.
	 * @param x - The X coordinate of the button.
	 * @param y - The Y coordinate of the button.
	 * @param width - The width of the button.
	 * @param height - The height of the button.
	 * @param label - The text of the button.
	 * @param font - The font used for the label of the button.
	 */
	private void renderButton(Graphics2D graphics2D, int x, int y, int width, int height,
			String label, Font font) {
		graphics2D.setFont(font);
		graphics2D.setColor(RED);
		graphics2D.fillRect(x, y, width, height);
		graphics2D.setColor(WHITE);
		graphics2D.drawRect(x, y, width, height);
		graphics2D.setColor(BLUE);
		
		FontMetrics metrics = graphics2D.getFontMetrics();
		Rectangle2D labelBounds = metrics.getStringBounds(label, graphics2D);
		
		graphics2D.drawString(label, (int) (x+width/2-labelBounds.getWidth()/2+1),
				(int) (y+height/2+labelBounds.getHeight()/2));
	}
	
	/**
	 * Checks to see if the user has clicked over a button.
	 * @param mouseX - The X coordinate of the mouse.
	 * @param mouseY - The Y coordinate of the mouse.
	 * @param x - The X coordinate of the button.
	 * @param y - The Y coordinate of the button.
	 * @param width - The width of the button.
	 * @param height - The height of the button.
	 * @return <code>true</code> if the mouse has clicked over the button, else
	 * <code>false</code>.
	 */
	private boolean mouseOver(float mouseX, float mouseY, int x, int y, int width, int height) {
		if (x <= mouseX && mouseX <= x+width && y <= mouseY && mouseY <= y+height) {
			AudioManager.getSound("Select").play(1, Game.sfxVolume);
			
			return true;
		} else return false;
	}
	
	/**
	 * Sets a valid target IP address of a server.
	 */
	private void setTargetIPAddress() {
		boolean validIP = false;
		
		while (!validIP) {
			validIP = true;
			
			String targetIPAddress = JOptionPane.showInputDialog(game,
					"Input target IP address.", Game.TITLE, JOptionPane.PLAIN_MESSAGE);
			
			if (!(targetIPAddress == null)) {
				try {
					game.client.setTargetIPAddress(InetAddress.getByName(targetIPAddress));
				} catch (UnknownHostException exception) {
					validIP = false;
					
					JOptionPane.showMessageDialog(game, "Invalid IP address!", Game.TITLE,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
