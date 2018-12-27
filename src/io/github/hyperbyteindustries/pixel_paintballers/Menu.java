package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.*;
import static java.awt.Font.*;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;
import io.github.hyperbyteindustries.pixel_paintballers.Game.State;
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

	public static boolean editText = false;
	
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
		
		companyLogo = new ImageIcon("res/Company logo.png").getImage();
		gameLogo = new ImageIcon("res/Game logo.png").getImage();
		titleSelect = new Font("Pixel EX", PLAIN, 32);
		menuHeader = new Font("Pixel EX", BOLD, 40);
		menuSelect = new Font("Pixel EX", PLAIN, 20);
		menuText = new Font("Pixel EX", ITALIC, 15);
	}
	
	/**
	 * Updates the logic of the menu.
	 */
	public void tick() {
		if (Game.gameState == State.LOGO) {
			if (clickable) {
				logoY -= 10;
				
				logoY = Game.clamp(logoY, -600, 0);
			}
		} else if (Game.gameState == State.TITLESCREEN) {
			if (!AudioManager.getMusic("Title").playing())
				AudioManager.getMusic("Title").loop(1.0f, 0.15f);
		} else if (Game.gameState == State.MAINMENU) {
			if (!AudioManager.getMusic("Menu 1").playing()) {
				if (AudioManager.getMusic("Menu 2").playing()) {
					AudioManager.getMusic("Menu 2").pause();
					AudioManager.getMusic("Menu 1").setPosition(AudioManager.getMusic("Menu 2").
							getPosition());
				}

				AudioManager.getMusic("Menu 1").loop(1.0f, 0.15f);
			}
		} else if (Game.gameState == State.DIFFICULTYSELECT ||
				Game.gameState == State.MULTIPLAYER || Game.gameState == State.CUSTOMISATION ||
				Game.gameState == State.INFO) {
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

				AudioManager.getMusic("Menu 2").loop(1.0f, 0.15f);
			}
		} else if (Game.gameState == State.GAME) {
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				if (!AudioManager.getMusic("Game 1").playing())
					AudioManager.getMusic("Game 1").loop(1.0f, 0.15f);
				
				break;
			case MULTIPLAYER:
				if (!AudioManager.getMusic("Game 2").playing())
					AudioManager.getMusic("Game 2").loop(1.0f, 0.15f);
				
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
				handler.getObjects().clear();
				
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
				
				Game.gameState = State.GAMEOVER;
			}
		} else if (Game.gameState == State.SERVERCONNECTION) {
			if (!AudioManager.getMusic("Menu 3").playing()) {
				if (AudioManager.getMusic("Menu 2").playing()) {
					AudioManager.getMusic("Menu 2").pause();
					AudioManager.getMusic("Menu 3").setPosition(AudioManager.getMusic("Menu 2").
							getPosition());
				}

				AudioManager.getMusic("Menu 3").loop(1.0f, 0.15f);
			}
		}
	}
	
	/**
	 * Updates the visuals for the menu.
	 * @param graphics2D - The graphics used to update the visuals.
	 */
	public void render(Graphics2D graphics2D) {
		switch (Game.gameState) {
		case LOGO:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Software Warning", 129, 40);
			
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
			
			graphics2D.drawString("If you believe that the software has crashed, please send a "
					+ "e-mail", 5, 295);
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
			graphics2D.drawString("Click the screen to continue", 67, Game.HEIGHT-5);
			
			graphics2D.drawImage(companyLogo, 0, logoY, Game.WIDTH, Game.HEIGHT, game);
			
			break;
		case TITLESCREEN:
			graphics2D.drawImage(gameLogo, 0, 0, Game.WIDTH, Game.HEIGHT, game);
			
			graphics2D.setFont(titleSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-64, Game.HEIGHT/2-96, 128, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-64, Game.HEIGHT/2-96, 128, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Play", Game.WIDTH/2-108/2, Game.HEIGHT/2-52);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-64, Game.HEIGHT/2+32, 128, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-64, Game.HEIGHT/2+32, 128, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Quit", Game.WIDTH/2-88/2, Game.HEIGHT/2+76);
			
			break;
		case MAINMENU:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Main menu", 259, 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 125, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 125, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Singleplayer", 37, 165);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Fend off the waves of enemies with your trusty", 269, 140);
			graphics2D.drawString("paintball gun alone!", 269, 155);

			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 200, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 200, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Multiplayer", 46, 240);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("There's nothing better than splatting every-", 269, 215);
			graphics2D.drawString("thing but doing it with friends!", 269, 230);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 275, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 275, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Customisation", 35, 315);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Be your own person. Change your looks and name", 269, 290);
			graphics2D.drawString("here!", 269, 305);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 350, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 350, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Info and credits", 15, 390);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Learn the ropes of combat, or find out who", 269, 365);
			graphics2D.drawString("helped bring this game to life!", 269, 380);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-96, 450, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-96, 450, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Back", 368, 490);
			
			break;
		case DIFFICULTYSELECT:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Select Difficulty", 122, 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 125, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 125, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Easy", 68, 165);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: Infinite (No reloads)", 205, 140);
			graphics2D.drawString("Enemy fire rate: 7 secs", 205, 155);
			graphics2D.drawString("A good start for beginners.", 205, 170);

			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 200, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 200, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Normal", 51, 240);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: 30", 205, 215);
			graphics2D.drawString("Reload ammo: 15", 205, 230);
			graphics2D.drawString("Enemy fire rate: 5 secs", 205, 245);
			graphics2D.drawString("The full basic experience.", 205, 260);

			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 275, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 275, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Hard", 68, 315);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: 20", 205, 290);
			graphics2D.drawString("Reload ammo: 10", 205, 305);
			graphics2D.drawString("Enemy fire rate: 3 secs", 205, 320);
			graphics2D.drawString("Provides a good challenge.", 205, 335);

			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 350, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 350, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Extreme", 43, 390);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Initial ammo: 10", 205, 365);
			graphics2D.drawString("Reload ammo: 5", 205, 380);
			graphics2D.drawString("Enemy fire rate: 2 secs", 205, 395);
			graphics2D.setColor(RED);
			graphics2D.drawString("WARNING: For true professionals only!", 205, 410);
			
			graphics2D.setFont(menuSelect);
			graphics2D.fillRect((Game.WIDTH/2)-96, 450, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect((Game.WIDTH/2)-96, 450, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Back", 368, 490);
			
			break;
		case GAME:
			if (Game.paused) {
				graphics2D.setFont(menuHeader);
				graphics2D.setColor(WHITE);
				graphics2D.drawString("Paused", 291, 40);
				
				graphics2D.setFont(menuSelect);
				graphics2D.setColor(RED);
				graphics2D.fillRect(Game.WIDTH/2-96, Game.HEIGHT/2-96, 192, 64);
				graphics2D.setColor(WHITE);
				graphics2D.drawRect(Game.WIDTH/2-96, Game.HEIGHT/2-96, 192, 64);
				graphics2D.setColor(BLUE);
				graphics2D.drawString("Resume", 350, 240);
				
				graphics2D.setColor(RED);
				graphics2D.fillRect(Game.WIDTH/2-96, Game.HEIGHT/2+32, 192, 64);
				graphics2D.setColor(WHITE);
				graphics2D.drawRect(Game.WIDTH/2-96, Game.HEIGHT/2+32, 192, 64);
				graphics2D.setColor(BLUE);
				
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					graphics2D.drawString("Quit", 373, Game.HEIGHT/2+72);
					
					break;
				case MULTIPLAYER:
					graphics2D.drawString("Disconnect", 322, Game.HEIGHT/2+72);
					
					break;
				}
			}
			
			
			break;
		case GAMEOVER:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				graphics2D.drawString("Game Over!", 240, 40);
				
				break;
			case MULTIPLAYER:
				graphics2D.drawString("You died!", 270, 40);
				
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
			graphics2D.drawString("So, what now?", 5, 220);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 230, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 230, 192, 64);
			graphics2D.setColor(BLUE);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				graphics2D.drawString("Play again", 28, 270);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Play another game using the same settings.", 205, 245);
				
				break;
			case MULTIPLAYER:
				graphics2D.drawString("Rejoin game", 20, 270);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Jump right back into the multiplayer mayhem!", 205, 245);
				
				break;
			}
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 305, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 305, 192, 64);
			graphics2D.setColor(BLUE);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				graphics2D.drawString("Change", 51, 334);
				graphics2D.drawString("Difficulty", 28, 355);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Play another game with a different difficulty.", 205,
						320);
				
				break;
			case MULTIPLAYER:
				graphics2D.drawString("Rejoin as", 37, 334);
				graphics2D.drawString("Spectator", 25, 355);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Relax and watch the rest of the chaos ensue!", 205, 320);
				
				break;
			}
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 380, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 380, 192, 64);
			graphics2D.setColor(BLUE);
			
			switch (Game.gameMode) {
			case SINGLEPLAYER:
				graphics2D.drawString("Main menu", 37, 420);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Return to the main menu.", 205, 395);
				
				break;
			case MULTIPLAYER:
				graphics2D.drawString("Leave game", 23, 420);
				graphics2D.setFont(menuText);
				graphics2D.setColor(YELLOW);
				graphics2D.drawString("Return to the multiplayer menu.", 205, 395);
				
				break;
			}
			
			break;
		case MULTIPLAYER:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Multiplayer", 210, 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Create a server", 5, 100);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Select a gamemode", 5, 125);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 135, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 135, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("PVP", 109, 175);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("It's a free-for-all as a maximum of 4 players", 269, 150);
			graphics2D.drawString("battle it out against each other!", 269, 165);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 210, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 210, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Team survival", 35, 250);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("4 people band together to battle the enemy", 269, 225);
			graphics2D.drawString("waves! Careful tough, friendly fire is enabled!", 269, 240);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Connect to a server", 5, 305);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 315, 256, 48);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 315, 256, 48);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Change address", 21, 347);
			graphics2D.setFont(menuText);
			graphics2D.setColor(YELLOW);
			graphics2D.drawString("Change the target server's IP address", 269, 330);
			graphics2D.drawString("Target server address: " +
					game.client.getTargetIPAddress().getHostAddress(), 269, 345);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 370, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 370, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Join game", 69, 410);

			graphics2D.setColor(RED);
			graphics2D.fillRect(275, 370, 256, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(275, 370, 256, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Spectate game", 299, 410);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-96, 500, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-96, 500, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Back", 368, 540);
			
			break;
		case SERVERCONNECTION:
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
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-96, 425, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-96, 425, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Retry", 358, 465);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-96, 500, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-96, 500, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Back", 368, 540);
			
			break;
		case CUSTOMISATION:
			graphics2D.setFont(menuHeader);
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Customisation", 185, 40);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(5, 150, 256, 48);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 150, 256, 48);
			graphics2D.setColor(BLUE);
			
			if (editText) graphics2D.drawString("Save username", 29, 182);
			else graphics2D.drawString("Change username", 12, 182);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(275, 150, 128, 48);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(275, 150, 128, 48);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Reset", 298, 182);
			
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
			graphics2D.drawString("Select outline colour", 5, 300);
			
			graphics2D.setColor(CYAN);
			graphics2D.fillRect(5, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 310, 32, 32);
			
			graphics2D.setColor(BLUE);
			graphics2D.fillRect(40, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(40, 310, 32, 32);
			
			graphics2D.setColor(DARK_GRAY);
			graphics2D.fillRect(75, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(75, 310, 32, 32);
			
			graphics2D.setColor(GRAY);
			graphics2D.fillRect(110, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(110, 310, 32, 32);
			
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(145, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(145, 310, 32, 32);
			
			graphics2D.setColor(LIGHT_GRAY);
			graphics2D.fillRect(180, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(180, 310, 32, 32);
			
			graphics2D.setColor(MAGENTA);
			graphics2D.fillRect(215, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(215, 310, 32, 32);
			
			graphics2D.setColor(ORANGE);
			graphics2D.fillRect(250, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(250, 310, 32, 32);
			
			graphics2D.setColor(PINK);
			graphics2D.fillRect(285, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(285, 310, 32, 32);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(320, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(320, 310, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.fillRect(355, 310, 32, 32);
			graphics2D.drawRect(355, 310, 32, 32);
			
			graphics2D.setColor(YELLOW);
			graphics2D.fillRect(390, 310, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(390, 310, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.drawString("Select username colour", 5, 370);
			
			graphics2D.setColor(CYAN);
			graphics2D.fillRect(5, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(5, 380, 32, 32);
			
			graphics2D.setColor(BLUE);
			graphics2D.fillRect(40, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(40, 380, 32, 32);
			
			graphics2D.setColor(DARK_GRAY);
			graphics2D.fillRect(75, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(75, 380, 32, 32);
			
			graphics2D.setColor(GRAY);
			graphics2D.fillRect(110, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(110, 380, 32, 32);
			
			graphics2D.setColor(GREEN);
			graphics2D.fillRect(145, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(145, 380, 32, 32);
			
			graphics2D.setColor(LIGHT_GRAY);
			graphics2D.fillRect(180, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(180, 380, 32, 32);
			
			graphics2D.setColor(MAGENTA);
			graphics2D.fillRect(215, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(215, 380, 32, 32);
			
			graphics2D.setColor(ORANGE);
			graphics2D.fillRect(250, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(250, 380, 32, 32);
			
			graphics2D.setColor(PINK);
			graphics2D.fillRect(285, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(285, 380, 32, 32);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(320, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(320, 380, 32, 32);
			
			graphics2D.setColor(WHITE);
			graphics2D.fillRect(355, 380, 32, 32);
			graphics2D.drawRect(355, 380, 32, 32);
			
			graphics2D.setColor(YELLOW);
			graphics2D.fillRect(390, 380, 32, 32);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(390, 380, 32, 32);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-96, 450, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-96, 450, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Back", 368, 490);
			
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
			graphics2D.drawString("Movement: WASD keys", 5, 175);
			graphics2D.drawString("Shoot paintball: Left click", 5, 190);
			graphics2D.drawString("Reload Ammo (Difficulties Normal and above): R key", 5, 205);
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
			graphics2D.drawString("    - Free Download:"
					+ " https://www.morgandavidking.com/free-downloads", 5, 445);
			graphics2D.drawRect(35, 444, 712, 1);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-96, 500, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-96, 500, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Back", 368, 540);
			
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2+100, 500, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2+100, 500, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Page 2", 552, 540);
			
			break;
		case INFO2:
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
					DataManager.timePlayed[1] + ":" + DataManager.timePlayed[0], 5, 320);
			graphics2D.drawString("Games played: " + DataManager.getStatistic("Games played"),
					5, 335);
			graphics2D.drawString("Shots fired: " + DataManager.getStatistic("Shots fired"), 5,
					350);
			graphics2D.drawString("Total kills: " + DataManager.getStatistic("Total kills"), 5,
					365);
			
			graphics2D.setFont(menuSelect);
			graphics2D.setColor(RED);
			graphics2D.fillRect(Game.WIDTH/2-292, 500, 192, 64);
			graphics2D.setColor(WHITE);
			graphics2D.drawRect(Game.WIDTH/2-292, 500, 192, 64);
			graphics2D.setColor(BLUE);
			graphics2D.drawString("Page 1", 165, 540);
			
			break;
		}
	}

	// Invoked when the mouse is pressed.
	public void mousePressed(MouseEvent event) {
		float mouseX = (float) event.getX();
		float mouseY = (float) event.getY();
		
		switch (Game.gameState) {
		case LOGO:
			if (clickable) {
				if (mouseOver(mouseX, mouseY, 98, 179, 636, 10)) {
					try {
						Desktop.getDesktop().browse(new URI(
								"https://hyperbyteindustries.github.io/Pixel-Paintballers"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				} else if (mouseOver(mouseX, mouseY, 367, 299, 269, 10)) {
					try {
						Desktop.getDesktop().mail(new URI("mailto:ramonegraham@gmail.com"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				} else {
					Game.gameState = State.TITLESCREEN;
				}
			}
			
			break;
		case TITLESCREEN:
			if (mouseOver(mouseX, mouseY, Game.WIDTH/2-64, Game.HEIGHT/2-96, 128, 64))
				Game.gameState = State.MAINMENU;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-64, Game.HEIGHT/2+32, 128, 64)) {
				DataManager.saveData();
				
				System.exit(1);
			}
			
			break;
		case MAINMENU:
			if (mouseOver(mouseX, mouseY, 5, 125, 256, 64))
				Game.gameState = State.DIFFICULTYSELECT;
			else if (mouseOver(mouseX, mouseY, 5, 200, 256, 64))
				Game.gameState = State.MULTIPLAYER;
			else if (mouseOver(mouseX, mouseY, 5, 275, 256, 64)) {
				Game.player.setX(Game.WIDTH/2-16);
				Game.player.setY(100);
				handler.addObject(Game.player);
				
				Game.gameState = State.CUSTOMISATION;
			} else if (mouseOver(mouseX, mouseY, 5, 350, 256, 64))
				Game.gameState = State.INFO;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 450, 192, 64))
				Game.gameState = State.TITLESCREEN;
			
			break;
		case DIFFICULTYSELECT:
			if (mouseOver(mouseX, mouseY, 5, 125, 192, 64)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 200, 192, 64)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.NORMAL;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 275, 192, 64)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.HARD;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 350, 192, 64)) {
				Game.gameMode = Game.Mode.SINGLEPLAYER;
				Game.gameDifficulty = Difficulty.EXTREME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 450, 192, 64))
				Game.gameState = State.MAINMENU;
			
			break;
		case GAME:
			if (!(Game.paused)) {
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					if (!(HeadsUpDisplay.ammo == 0) && !HeadsUpDisplay.reloading) {
						Paintball paintball = new Paintball(Game.player.getX()+12,
								Game.player.getY()+12, ID.PAINTBALL, game, handler,
								Game.player);
						
						handler.addObject(paintball);
						
						float diffX = paintball.getX() - mouseX + 4,
								diffY = paintball.getY() - mouseY + 4,
								distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
						
						if (distance >= 50) {
							paintball.setVelX((float) ((-1.0/distance)*diffX)*7);
							paintball.setVelY((float) ((-1.0/distance)*diffY)*7);
						} else if (distance >= 40) {
							paintball.setVelX((float) (((-1.0/distance)*diffX)*6.5));
							paintball.setVelY((float) (((-1.0/distance)*diffY)*6.5));
						} else if (distance >= 30) {
							paintball.setVelX((float) ((-1.0/distance)*diffX)*6);
							paintball.setVelY((float) ((-1.0/distance)*diffY)*6);
						} else if (distance >= 20) {
							paintball.setVelX((float) (((-1.0/distance)*diffX)*5.5));
							paintball.setVelY((float) (((-1.0/distance)*diffY)*5.5));
						} else if (distance >= 10) {
							paintball.setVelX((float) ((-1.0/distance)*diffX)*5);
							paintball.setVelY((float) ((-1.0/distance)*diffY)*5);
						} else {
							paintball.setVelX((float) (((-1.0/distance)*diffX)*4.5));
							paintball.setVelY((float) (((-1.0/distance)*diffY)*4.5));
						}
						
						if (Game.gameDifficulty != Difficulty.EASY) HeadsUpDisplay.ammo--;
						
						HeadsUpDisplay.shots++;
						
						AudioManager.getSound("Shot").play(1.0f, 0.10f);
					}
					
					break;
				case MULTIPLAYER:
					if (!Game.player.spectator) {
						if (!(HeadsUpDisplay.ammo == 0) && !HeadsUpDisplay.reloading) {
							Paintball paintball = new Paintball(Game.player.getX()+12,
									Game.player.getY()+12, ID.PAINTBALL, game, handler,
									Game.player);
							
							handler.addObject(paintball);
							
							float diffX = paintball.getX() - mouseX + 4,
									diffY = paintball.getY() - mouseY + 4,
									distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
							
							if (distance >= 50) {
								paintball.setVelX((float) ((-1.0/distance)*diffX)*7);
								paintball.setVelY((float) ((-1.0/distance)*diffY)*7);
							} else if (distance >= 40) {
								paintball.setVelX((float) (((-1.0/distance)*diffX)*6.5));
								paintball.setVelY((float) (((-1.0/distance)*diffY)*6.5));
							} else if (distance >= 30) {
								paintball.setVelX((float) ((-1.0/distance)*diffX)*6);
								paintball.setVelY((float) ((-1.0/distance)*diffY)*6);
							} else if (distance >= 20) {
								paintball.setVelX((float) (((-1.0/distance)*diffX)*5.5));
								paintball.setVelY((float) (((-1.0/distance)*diffY)*5.5));
							} else if (distance >= 10) {
								paintball.setVelX((float) ((-1.0/distance)*diffX)*5);
								paintball.setVelY((float) ((-1.0/distance)*diffY)*5);
							} else {
								paintball.setVelX((float) (((-1.0/distance)*diffX)*4.5));
								paintball.setVelY((float) (((-1.0/distance)*diffY)*4.5));
							}
							
							Packet03PlayerShot packet =
									new Packet03PlayerShot(Game.player.getUsername(),
											paintball.getX(), paintball.getY(),
											paintball.getVelX(), paintball.getVelY());
							packet.writeData(game.client);
							
							HeadsUpDisplay.shots++;
							
							AudioManager.getSound("Shot").play(1.0f, 0.10f);
						}
					}
					
					break;
				}
			} else {
				if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, Game.HEIGHT/2-96, 192, 64))
					Game.paused = false;
				else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, Game.HEIGHT/2+32, 192,
						64)) {
					Game.paused = false;
					handler.getObjects().clear();
					
					if (Game.gameMode == Game.Mode.MULTIPLAYER) {
						Packet01Disconnect packet =
								new Packet01Disconnect(Game.player.getUsername());
						packet.writeData(game.client);
						
						if (!(game.server == null)) game.server.stop();
					}
					
					Game.gameState = State.MAINMENU;
				}
			}
			
			break;
		case GAMEOVER:
			if (mouseOver(mouseX, mouseY, 5, 230, 192, 64)) {
				if (Game.gameMode == Game.Mode.MULTIPLAYER) {
					Game.player.health = 100;
					
					Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
							Game.player.getX(), Game.player.getY(), Game.player.getFillColour(),
							Game.player.getOutlineColour(), Game.player.getUsernameColour(),
							Game.player.health, true, Game.player.spectator);
					packet.writeData(game.client);
				}
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 305, 192, 64)) {
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					Game.gameState = State.DIFFICULTYSELECT;
					
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
			} else if (mouseOver(mouseX, mouseY, 5, 380, 192, 64)) {
				switch (Game.gameMode) {
				case SINGLEPLAYER:
					Game.gameState = State.MAINMENU;
					
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
		case MULTIPLAYER:
			if (mouseOver(mouseX, mouseY, 5, 135, 256, 64)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Server.gameMode = Server.Mode.PVP;
				
				Game.player.spectator = false;
				
				game.server = new Server();
				game.server.start();
				
				try {
					game.client.setTargetIPAddress(InetAddress.getByName("localhost"));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				
				game.client.pingServer();
				
				Game.gameState = State.SERVERCONNECTION;
			} else if (mouseOver(mouseX, mouseY, 5, 210, 256, 64)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Server.gameMode = Server.Mode.TEAMSURVIVAL;
				
				Game.player.spectator = false;
				
				game.server = new Server();
				game.server.start();
				
				try {
					game.client.setTargetIPAddress(InetAddress.getByName("localhost"));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				
				game.client.pingServer();
				
				Game.gameState = State.SERVERCONNECTION;
			} else if (mouseOver(mouseX, mouseY, 5, 315, 256, 48)) setTargetIPAddress();
			else if (mouseOver(mouseX, mouseY, 5, 370, 256, 64)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Game.player.spectator = false;
				
				game.client.pingServer();
				
				Game.gameState = State.SERVERCONNECTION;
			} else if (mouseOver(mouseX, mouseY, 275, 370, 256, 64)) {
				Game.gameMode = Game.Mode.MULTIPLAYER;
				Game.gameDifficulty = Difficulty.EASY;
				
				Game.player.spectator = true;
				
				game.client.pingServer();
				
				Game.gameState = State.SERVERCONNECTION;
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 500, 192, 64))
				Game.gameState = State.MAINMENU;
			
			break;
		case SERVERCONNECTION:
			if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 425, 192, 64)) {
				game.client.pingServer();
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 500, 192, 64))
				Game.gameState = State.MULTIPLAYER;
			
			break;
		case CUSTOMISATION:
			if (mouseOver(mouseX, mouseY, 5, 150, 256, 48)) {
				if (editText) {
					editText = false;
					
					if (Game.player.getUsername() == null ||
							Game.player.getUsername().length() == 0)
						Game.player.setUsername("Player");
				} else editText = true;
			} else if (mouseOver(mouseX, mouseY, 275, 150, 128, 48)) {
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
			else if (mouseOver(mouseX, mouseY, 5, 310, 32, 32))
				Game.player.setOutlineColour(CYAN);
			else if (mouseOver(mouseX, mouseY, 40, 310, 32, 32))
				Game.player.setOutlineColour(BLUE);
			else if (mouseOver(mouseX, mouseY, 75, 310, 32, 32))
				Game.player.setOutlineColour(DARK_GRAY);
			else if (mouseOver(mouseX, mouseY, 110, 310, 32, 32))
				Game.player.setOutlineColour(GRAY);
			else if (mouseOver(mouseX, mouseY, 145, 310, 32, 32))
				Game.player.setOutlineColour(GREEN);
			else if (mouseOver(mouseX, mouseY, 180, 310, 32, 32))
				Game.player.setOutlineColour(LIGHT_GRAY);
			else if (mouseOver(mouseX, mouseY, 215, 310, 32, 32))
				Game.player.setOutlineColour(MAGENTA);
			else if (mouseOver(mouseX, mouseY, 250, 310, 32, 32))
				Game.player.setOutlineColour(ORANGE);
			else if (mouseOver(mouseX, mouseY, 285, 310, 32, 32))
				Game.player.setOutlineColour(PINK);
			else if (mouseOver(mouseX, mouseY, 320, 310, 32, 32))
				Game.player.setOutlineColour(RED);
			else if (mouseOver(mouseX, mouseY, 355, 310, 32, 32))
				Game.player.setOutlineColour(WHITE);
			else if (mouseOver(mouseX, mouseY, 390, 310, 32, 32))
				Game.player.setOutlineColour(YELLOW);
			else if (mouseOver(mouseX, mouseY, 5, 380, 32, 32))
				Game.player.setUsernameColour(CYAN);
			else if (mouseOver(mouseX, mouseY, 40, 380, 32, 32))
				Game.player.setUsernameColour(BLUE);
			else if (mouseOver(mouseX, mouseY, 75, 380, 32, 32))
				Game.player.setUsernameColour(DARK_GRAY);
			else if (mouseOver(mouseX, mouseY, 110, 380, 32, 32))
				Game.player.setUsernameColour(GRAY);
			else if (mouseOver(mouseX, mouseY, 145, 380, 32, 32))
				Game.player.setUsernameColour(GREEN);
			else if (mouseOver(mouseX, mouseY, 180, 380, 32, 32))
				Game.player.setUsernameColour(LIGHT_GRAY);
			else if (mouseOver(mouseX, mouseY, 215, 380, 32, 32))
				Game.player.setUsernameColour(MAGENTA);
			else if (mouseOver(mouseX, mouseY, 250, 380, 32, 32))
				Game.player.setUsernameColour(ORANGE);
			else if (mouseOver(mouseX, mouseY, 285, 380, 32, 32))
				Game.player.setUsernameColour(PINK);
			else if (mouseOver(mouseX, mouseY, 320, 380, 32, 32))
				Game.player.setUsernameColour(RED);
			else if (mouseOver(mouseX, mouseY, 355, 380, 32, 32))
				Game.player.setUsernameColour(WHITE);
			else if (mouseOver(mouseX, mouseY, 390, 380, 32, 32))
				Game.player.setUsernameColour(YELLOW);
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 450, 192, 64)) {
				if (editText) AudioManager.getSound("Denied").play();
				else {
					handler.removeObject(Game.player);
					
					DataManager.saveData();
					
					Game.gameState = State.MAINMENU;
				}
			}
			
			break;
		case INFO:
			if (mouseOver(mouseX, mouseY, 36, 389, 529, 10)) {
				try {
					Desktop.getDesktop().browse(new URI(
							"https://www.youtube.com/MDKOfficialYT"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, 35, 404, 528, 10)) {
				try {
					Desktop.getDesktop().browse(new URI(
							"https://www.facebook.com/MDKOfficial"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, 35, 419, 617, 10)) {
				try {
					Desktop.getDesktop().browse(new URI(
							"https://www.mdkofficial.bandcamp.com"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, 35, 434, 712, 10)) {
				try {
					Desktop.getDesktop().browse(new URI(
							"https://www.morgandavidking.com/free-downloads"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (mouseOver(mouseX, mouseY, Game.WIDTH/2+100, 500, 192, 64))
				Game.gameState = State.INFO2;
			else if (mouseOver(mouseX, mouseY, Game.WIDTH/2-96, 500, 192, 64))
				Game.gameState = State.MAINMENU;
			
			break;
		case INFO2:
			if (mouseOver(mouseX, mouseY, Game.WIDTH/2-292, 500, 192, 64))
				Game.gameState = State.INFO;
			
			break;
		}
	}
	
	/**
	 * Sets a valid target IP address of a server.
	 */
	private void setTargetIPAddress() {
		String targetIPAddress = JOptionPane.showInputDialog(game, "Input target IP address.",
				Game.TITLE, JOptionPane.PLAIN_MESSAGE);
		
		if (!(targetIPAddress == null)) {
			try {
				game.client.setTargetIPAddress(InetAddress.getByName(targetIPAddress));
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(game, "Invalid IP address!", Game.TITLE,
						JOptionPane.ERROR_MESSAGE);
				
				setTargetIPAddress();
			}
		}
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
			AudioManager.getSound("Select").play(1.0f, 0.2f);
			return true;
		} else return false;
	}
}
