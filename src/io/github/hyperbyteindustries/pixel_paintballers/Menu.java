package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.*;
import static java.awt.Font.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Difficulty;
import io.github.hyperbyteindustries.pixel_paintballers.Game.State;

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
	
	private Image companyLogo;
	private Image gameLogo;
	private Font titleSelect;
	private Font menuHeader;
	private Font menuSelect;
	private Font menuText;
	
	/**
	 * Creates a new instance of this class.
	 * @param handler - An instance of the handler class, used to shoot paintballs.
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
		if (Game.gameState == State.TITLESCREEN) {
			if (!(AudioManager.getMusic("Title").playing()))
				AudioManager.getMusic("Title").loop();
		} else if (Game.gameState == State.MAINMENU) {
			if (!(AudioManager.getMusic("Menu 1").playing())) {
				if (AudioManager.getMusic("Menu 2").playing()) {
					AudioManager.getMusic("Menu 2").pause();
					AudioManager.getMusic("Menu 1").setPosition(AudioManager.getMusic("Menu 2")
							.getPosition());
				}

				AudioManager.getMusic("Menu 1").loop();
			}
		} else if (Game.gameState == State.DIFFICULTYSELECT || Game.gameState ==
				State.CUSTOMISATION || Game.gameState == State.INFO) {
			if (!(AudioManager.getMusic("Menu 2").playing())) {
				if (AudioManager.getMusic("Menu 1").playing()) {
					AudioManager.getMusic("Menu 1").pause();
					AudioManager.getMusic("Menu 2").setPosition(AudioManager.getMusic("Menu 1")
							.getPosition());
				}

				AudioManager.getMusic("Menu 2").loop();
			}
		} else if (Game.gameState == State.GAME) {
			if (HeadsUpDisplay.health == 0) {
				handler.objects.clear();
				Game.player.setVelX(0);
				Game.player.setVelY(0);
				Game.gameState = State.GAMEOVER;
				
				DataManager.increaseStatistic("Shots fired", HeadsUpDisplay.shots);
				DataManager.increaseStatistic("Total kills", HeadsUpDisplay.kills);
				DataManager.increaseStatistic("Games played", 1);
				
				if (HeadsUpDisplay.score > DataManager.getStatistic("Highscore (" +
						Game.gameDifficulty.name() + ")"))
					DataManager.setStatistic("Highscore (" + Game.gameDifficulty.name() + ")",
							HeadsUpDisplay.score);
				
				DataManager.saveData();
			}
			
			if (!(AudioManager.getMusic("Game 1").playing()))
				AudioManager.getMusic("Game 1").loop();
		}
	}
	
	/**
	 * Updates the visuals for the menu.
	 * @param graphics2d - The graphics used to update the visuals.
	 */
	public void render(Graphics2D graphics2d) {
		if (Game.gameState == State.LOGO) graphics2d.drawImage(companyLogo, 0, 0, game);
		else if (Game.gameState == State.TITLESCREEN) {
			graphics2d.drawImage(gameLogo, 0, 0, game);
			
			graphics2d.setFont(titleSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-64, (Game.YBOUND/2)-96, 128, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-64, (Game.YBOUND/2)-96, 128, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Play", (Game.XBOUND/2)-53, (Game.YBOUND/2)-50);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-64, (Game.YBOUND/2)+32, 128, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-64, (Game.YBOUND/2)+32, 128, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Quit", (Game.XBOUND/2)-45, (Game.YBOUND/2)+78);
		} else if (Game.gameState == State.MAINMENU) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Main menu", Game.XBOUND/2-(("Main menu".length()-1)/2*35),
					40);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 125, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 125, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Singleplayer", 128-(("Singleplayer".length()-1)/2*18), 165);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Fend off the hoards of enemies with your trusty", 269, 140);
			graphics2d.drawString("paintball gun alone!", 269, 155);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 200, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 200, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Multiplayer", 128-(("Multiplayer".length()-1)/2*16), 240);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("There's nothing better than splatting enemies", 269, 215);
			graphics2d.drawString("but doing it with friends!", 269, 230);
			graphics2d.setColor(RED);
			graphics2d.drawString("Multiplayer is not available in Alpha releases.", 269, 245);
			graphics2d.drawString("This development will be in the Beta releases.", 269, 260);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 275, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 275, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Customisation", 128-(("Customisation".length()-1)/2*15),
					315);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Be your own person. Change your looks and name", 269, 290);
			graphics2d.drawString("here!", 269, 305);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 350, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 350, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Info and credits", 128-(("Info and credits".length()-1)/2*
					16), 390);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Learn the ropes of combat, or find out who", 269, 365);
			graphics2d.drawString("helped bring this game to life!", 269, 380);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-96, 450, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-96, 450, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Back", Game.XBOUND/2-(("Back".length()-1)/2*35), 490);
		} else if (Game.gameState == State.DIFFICULTYSELECT) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Select Difficulty", Game.XBOUND/2-(("Select Difficulty"
					.length()-1)/2*35), 40);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 125, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 125, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Easy", 101-(("Easy".length()-1)/2*35), 165);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Initial ammo: Infinite (No reloads)", 205, 140);
			graphics2d.drawString("Enemy fire rate: 7 secs", 205, 155);

			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 200, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 200, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Normal", 101-(("Normal".length()-1)/2*25), 240);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Initial ammo: 30", 205, 215);
			graphics2d.drawString("Reload ammo: 15", 205, 230);
			graphics2d.drawString("Enemy fire rate: 5 secs", 205, 245);

			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 275, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 275, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Hard", 101-(("Hard".length()-1)/2*35), 315);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Initial ammo: 20", 205, 290);
			graphics2d.drawString("Reload ammo: 10", 205, 305);
			graphics2d.drawString("Enemy fire rate: 3 secs", 205, 320);

			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 350, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 350, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Extreme", 101-(("Extreme".length()-1)/2*20), 390);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Initial ammo: 10", 205, 365);
			graphics2d.drawString("Reload ammo: 5", 205, 380);
			graphics2d.drawString("Enemy fire rate: 2 secs", 205, 395);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-96, 450, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-96, 450, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Back", Game.XBOUND/2-(("Back".length()-1)/2*35), 490);
		} if (Game.gameState == State.GAME) {
			if (Game.paused) {
				graphics2d.setFont(menuHeader);
				graphics2d.setColor(WHITE);
				graphics2d.drawString("Paused", Game.XBOUND/2-(("Paused".length()-1)/2*53), 40);
				
				graphics2d.setFont(menuSelect);
				graphics2d.setColor(RED);
				graphics2d.fillRect(Game.XBOUND/2-96, Game.YBOUND/2-96, 192, 64);
				graphics2d.setColor(WHITE);
				graphics2d.drawRect(Game.XBOUND/2-96, Game.YBOUND/2-96, 192, 64);
				graphics2d.setColor(BLUE);
				graphics2d.drawString("Resume", Game.XBOUND/2-(("Resume".length()-1)/2*25),
						Game.YBOUND/2-55);
				
				graphics2d.setColor(RED);
				graphics2d.fillRect(Game.XBOUND/2-96, Game.YBOUND/2+32, 192, 64);
				graphics2d.setColor(WHITE);
				graphics2d.drawRect(Game.XBOUND/2-96, Game.YBOUND/2+32, 192, 64);
				graphics2d.setColor(BLUE);
				graphics2d.drawString("Quit", Game.XBOUND/2-(("Quit".length()-1)/2*30),
						Game.YBOUND/2+72);
			}
		} else if (Game.gameState == State.GAMEOVER) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Game Over!", Game.XBOUND/2-(("Game over!".length()-1)/2*40),
					40);
			
			graphics2d.setFont(menuText);
			
			if (HeadsUpDisplay.score >= DataManager.getStatistic("Highscore (" +
					Game.gameDifficulty.name() + ")")) {
				graphics2d.setColor(YELLOW);
				graphics2d.drawString("Your final score was: " + HeadsUpDisplay.score +
						"    HIGHSCORE!!!", 10, 150);
				graphics2d.setColor(WHITE);
			} else {
				graphics2d.drawString("Your final score was: " + HeadsUpDisplay.score, 10, 150);
			}
			
			graphics2d.drawString("Your final level was: " + HeadsUpDisplay.level, 10, 165);
			graphics2d.drawString("Your chosen difficulty: " + Game.gameDifficulty.name(), 10,
					180);
			graphics2d.drawString("Paintballs shot in this game: " + HeadsUpDisplay.shots, 10, 195);
			graphics2d.drawString("Enemies killed: " + HeadsUpDisplay.kills, 10, 210);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-92, 250, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-92, 250, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Change", (Game.XBOUND/2)-45, 280);
			graphics2d.drawString("difficulty", (Game.XBOUND/2)-67, 300);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-92, 350, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-92, 350, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Play again", (Game.XBOUND/2)-67, 390);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-92, 450, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-92, 450, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Quit", (Game.XBOUND/2)-25, 490);
		} else if (Game.gameState == State.CUSTOMISATION) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Customisation", Game.XBOUND/2-(("Customisation".length()-1)/
					2*35), 40);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 150, 256, 48);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 150, 256, 48);
			graphics2d.setColor(BLUE);
			
			if (editText) graphics2d.drawString("Save username", 29, 181);
			else graphics2d.drawString("Change username", 12, 181);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect(275, 150, 128, 48);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(275, 150, 128, 48);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Reset", 298, 181);
			
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Select fill colour", 5, 230);
			
			graphics2d.setColor(CYAN);
			graphics2d.fillRect(5, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 240, 32, 32);
			
			graphics2d.setColor(BLUE);
			graphics2d.fillRect(40, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(40, 240, 32, 32);
			
			graphics2d.setColor(DARK_GRAY);
			graphics2d.fillRect(75, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(75, 240, 32, 32);
			
			graphics2d.setColor(GRAY);
			graphics2d.fillRect(110, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(110, 240, 32, 32);
			
			graphics2d.setColor(GREEN);
			graphics2d.fillRect(145, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(145, 240, 32, 32);
			
			graphics2d.setColor(LIGHT_GRAY);
			graphics2d.fillRect(180, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(180, 240, 32, 32);
			
			graphics2d.setColor(MAGENTA);
			graphics2d.fillRect(215, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(215, 240, 32, 32);
			
			graphics2d.setColor(ORANGE);
			graphics2d.fillRect(250, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(250, 240, 32, 32);
			
			graphics2d.setColor(PINK);
			graphics2d.fillRect(285, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(285, 240, 32, 32);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect(320, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(320, 240, 32, 32);
			
			graphics2d.setColor(WHITE);
			graphics2d.fillRect(355, 240, 32, 32);
			graphics2d.drawRect(355, 240, 32, 32);
			
			graphics2d.setColor(YELLOW);
			graphics2d.fillRect(390, 240, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(390, 240, 32, 32);
			
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Select outline colour", 5, 300);
			
			graphics2d.setColor(CYAN);
			graphics2d.fillRect(5, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 310, 32, 32);
			
			graphics2d.setColor(BLUE);
			graphics2d.fillRect(40, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(40, 310, 32, 32);
			
			graphics2d.setColor(DARK_GRAY);
			graphics2d.fillRect(75, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(75, 310, 32, 32);
			
			graphics2d.setColor(GRAY);
			graphics2d.fillRect(110, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(110, 310, 32, 32);
			
			graphics2d.setColor(GREEN);
			graphics2d.fillRect(145, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(145, 310, 32, 32);
			
			graphics2d.setColor(LIGHT_GRAY);
			graphics2d.fillRect(180, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(180, 310, 32, 32);
			
			graphics2d.setColor(MAGENTA);
			graphics2d.fillRect(215, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(215, 310, 32, 32);
			
			graphics2d.setColor(ORANGE);
			graphics2d.fillRect(250, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(250, 310, 32, 32);
			
			graphics2d.setColor(PINK);
			graphics2d.fillRect(285, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(285, 310, 32, 32);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect(320, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(320, 310, 32, 32);
			
			graphics2d.setColor(WHITE);
			graphics2d.fillRect(355, 310, 32, 32);
			graphics2d.drawRect(355, 310, 32, 32);
			
			graphics2d.setColor(YELLOW);
			graphics2d.fillRect(390, 310, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(390, 310, 32, 32);
			
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Select username colour", 5, 370);
			
			graphics2d.setColor(CYAN);
			graphics2d.fillRect(5, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 380, 32, 32);
			
			graphics2d.setColor(BLUE);
			graphics2d.fillRect(40, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(40, 380, 32, 32);
			
			graphics2d.setColor(DARK_GRAY);
			graphics2d.fillRect(75, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(75, 380, 32, 32);
			
			graphics2d.setColor(GRAY);
			graphics2d.fillRect(110, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(110, 380, 32, 32);
			
			graphics2d.setColor(GREEN);
			graphics2d.fillRect(145, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(145, 380, 32, 32);
			
			graphics2d.setColor(LIGHT_GRAY);
			graphics2d.fillRect(180, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(180, 380, 32, 32);
			
			graphics2d.setColor(MAGENTA);
			graphics2d.fillRect(215, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(215, 380, 32, 32);
			
			graphics2d.setColor(ORANGE);
			graphics2d.fillRect(250, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(250, 380, 32, 32);
			
			graphics2d.setColor(PINK);
			graphics2d.fillRect(285, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(285, 380, 32, 32);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect(320, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(320, 380, 32, 32);
			
			graphics2d.setColor(WHITE);
			graphics2d.fillRect(355, 380, 32, 32);
			graphics2d.drawRect(355, 380, 32, 32);
			
			graphics2d.setColor(YELLOW);
			graphics2d.fillRect(390, 380, 32, 32);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(390, 380, 32, 32);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-96, 450, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-96, 450, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Back", Game.XBOUND/2-(("Back".length()-1)/2*35), 490);
		} else if (Game.gameState == State.INFO) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Information and credits", (Game.XBOUND)/2-
					(("Information and credits".length()-1)/2*34), 40);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("The \"aim\" of the game is to shoot down your enenmies with "
					+ "paintballs,", 5, 100);
			graphics2d.drawString("whilst dodging their own attacks.", 5, 115);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Controls", 5, 150);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Movement: WASD keys", 5, 175);
			graphics2d.drawString("Shoot paintball: Left click", 5, 190);
			graphics2d.drawString("Reload Ammo (Difficulties Normal and above): R key", 5, 205);
			graphics2d.drawString("Pause: ESC key", 5, 220);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Game development", 5, 255);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("General director and programming: Sweetboy13735", 5, 280);
			graphics2d.drawString("Game mechanics and ideas: FateAssassin", 5, 295);
			graphics2d.drawString("Game testing: B-Clark7698", 5, 310);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Music and sounds", 5, 345);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(RED);
			graphics2d.drawString("DISCLAIMER: HyperByte Industries does not own any of the "
					+ "music used.", 5, 370);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Title screen: MDK - Press Start", 5, 385);
			graphics2d.drawString("    - YouTube: http://www.youtube.com/MDKOfficialYT", 5,
					400);
			graphics2d.drawString("    - Facebook: http://www.facebook.com/MDKOfficial", 5,
					415);
			graphics2d.drawString("    - Buy the song here: http://www.mdkofficial.bandcamp"
					+ ".com", 5, 430);
			graphics2d.drawString("    - Free Download: http://www.morgandavidking.com/free-"
					+ "downloads", 5, 445);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(Game.XBOUND/2+100, 500, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(Game.XBOUND/2+100, 500, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Page 2", Game.XBOUND/2+150, 540);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect(Game.XBOUND/2-96, 500, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(Game.XBOUND/2-96, 500, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Back", Game.XBOUND/2-(("Back".length()-1)/2*35), 540);
		} else if (Game.gameState == State.INFO2) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Information and credits", (Game.XBOUND)/2-
					(("Information and credits".length()-1)/2*34), 40);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Music and sounds (Contiued)", 5, 100);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(RED);
			graphics2d.drawString("DISCLAIMER: HyperByte Industries does not own any of the "
					+ "music used.", 5, 125);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Menu screen: Mario Kart 7 - Menu themes 1, 2 & 3", 5, 140);
			graphics2d.drawString("Singleplayer game: Oscillator Z - Break it down", 5, 155);
			graphics2d.drawString("Multiplayer game: Wildfellas & TRAPECIA - Blow up", 5, 170);
			graphics2d.drawString("Menu select sound: Hitmarker", 5, 185);
			graphics2d.drawString("Paintball shot sound: Intervention 420", 5, 200);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Statistics", 5, 235);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Highscore (Easy): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.EASY.name() + ")"),
							5, 250);
			graphics2d.drawString("Highscore (Normal): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.NORMAL.name() +
							")"), 5, 265);
			graphics2d.drawString("Highscore (Hard): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.HARD.name() + ")"),
							5, 280);
			graphics2d.drawString("Highscore (Extreme): " +
					DataManager.getStatistic("Highscore (" + Game.Difficulty.EXTREME.name() +
							")"), 5, 295);
			graphics2d.drawString("Time played: " + DataManager.timePlayed[2] + ":" +
					DataManager.timePlayed[1] + ":" + DataManager.timePlayed[0], 5, 310);
			graphics2d.drawString("Games played: " + DataManager.getStatistic("Games played"),
					5, 325);
			graphics2d.drawString("Shots fired: " + DataManager.getStatistic("Shots fired"), 5,
					340);
			graphics2d.drawString("Total kills: " + DataManager.getStatistic("Total kills"), 5,
					355);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(Game.XBOUND/2-292, 500, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(Game.XBOUND/2-292, 500, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Page 1", (Game.XBOUND/2-(("Page 1".length()-1)/2*35))-166,
					540);
		}
	}

	// Invoked when the mouse is pressed.
	public void mousePressed(MouseEvent e) {
		float mouseX = (float) e.getX();
		float mouseY = (float) e.getY();
		
		if (Game.gameState == State.TITLESCREEN) {
			if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-64, (Game.YBOUND/2)-96, 128, 64)) {
				Game.gameState = State.MAINMENU;
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-64, (Game.YBOUND/2)+32, 128,
					64)) {
				DataManager.saveData();
				
				System.exit(1);
			}
		} else if (Game.gameState == State.MAINMENU) {
			if (mouseOver(mouseX, mouseY, 5, 125, 256, 64)) {
				Game.gameState = State.DIFFICULTYSELECT;
			} else if (mouseOver(mouseX, mouseY, 5, 200, 256, 64)) {
				AudioManager.getSound("Denied").play();
			} else if (mouseOver(mouseX, mouseY, 5, 275, 256, 64)) {
				Game.gameState = State.CUSTOMISATION;
				
				Game.player.setX(Game.XBOUND/2-16);
				Game.player.setY(100);
				handler.addObject(Game.player);
			} else if (mouseOver(mouseX, mouseY, 5, 350, 256, 64)) {
				Game.gameState = State.INFO;
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-96, 450, 192, 64)) {
				Game.gameState = State.TITLESCREEN;
			}
		} else if (Game.gameState == State.DIFFICULTYSELECT) {
			if (mouseOver(mouseX, mouseY, 5, 125, 192, 64)) {
				Game.gameDifficulty = Difficulty.EASY;
				Game.gameState = State.GAME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 200, 192, 64)) {
				Game.gameDifficulty = Difficulty.NORMAL;
				Game.gameState = State.GAME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 275, 192, 64)) {
				Game.gameDifficulty = Difficulty.HARD;
				Game.gameState = State.GAME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, 5, 350, 192, 64)) {
				Game.gameDifficulty = Difficulty.EXTREME;
				Game.gameState = State.GAME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-96, 450, 192, 64)) {
				Game.gameState = State.MAINMENU;
			}
		} else if (Game.gameState == State.GAME) {
			if (!(Game.paused)) {
				if (HeadsUpDisplay.ammo != 0 && HeadsUpDisplay.shoot) {
					Paintball paintball = new Paintball(Game.player.getX()+12,
							Game.player.getY()+12, ID.PAINTBALL, handler, Game.player);
					
					handler.addObject(paintball);
					
					float diffX = paintball.getX()-(mouseX-4), diffY =
							paintball.getY()-(mouseY-4), distance = (float)
							Math.sqrt((paintball.getX()-mouseX)*(paintball.getX()-mouseX) +
									(paintball.getY()-mouseY)*(paintball.getY()-mouseY));
					
					paintball.setVelX((float) ((-1.0/distance) * diffX)*7);
					paintball.setVelY((float) ((-1.0/distance) * diffY)*7);
					
					if (Game.gameDifficulty != Difficulty.EASY) HeadsUpDisplay.ammo--;
					
					HeadsUpDisplay.shots++;
					
					AudioManager.getSound("Shot").play();
				}
			} else {
				if (mouseOver(mouseX, mouseY, Game.XBOUND/2-96, Game.YBOUND/2-96, 192, 64)) {
					Game.paused = false;
				}
				else if (mouseOver(mouseX, mouseY, Game.XBOUND/2-96, Game.YBOUND/2+32, 192,
						64)) {
					Game.paused = false;
					handler.objects.clear();
					Game.gameState = State.TITLESCREEN;
				}
			}
		} else if (Game.gameState == State.GAMEOVER) {
			if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, 250, 192, 64)) {
				Game.gameState = State.DIFFICULTYSELECT;
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, 350, 192, 64)) {
				Game.gameState = State.GAME;
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, 450, 192, 64)) {
				Game.gameState = State.TITLESCREEN;
			}
		} else if (Game.gameState == State.CUSTOMISATION) {
			if (mouseOver(mouseX, mouseY, 5, 150, 256, 48)) {
				if (editText) {
					editText = false;
					
					if (Game.player.getUsername() == null||
							Game.player.getUsername().length() == 0)
						Game.player.setUsername("Player");
				} else editText = true;
			} else if (mouseOver(mouseX, mouseY, 275, 150, 128, 48)) {
				Game.player.setUsername("Player");
				Game.player.setFillColour(RED);
				Game.player.setOutlineColour(WHITE);
				Game.player.setUsernameColour(GRAY);
			} else if (mouseOver(mouseX, mouseY, 5, 240, 32, 32)) {
				Game.player.setFillColour(CYAN);
			} else if (mouseOver(mouseX, mouseY, 40, 240, 32, 32)) {
				Game.player.setFillColour(BLUE);
			} else if (mouseOver(mouseX, mouseY, 75, 240, 32, 32)) {
				Game.player.setFillColour(DARK_GRAY);
			} else if (mouseOver(mouseX, mouseY, 110, 240, 32, 32)) {
				Game.player.setFillColour(GRAY);
			} else if (mouseOver(mouseX, mouseY, 145, 240, 32, 32)) {
				Game.player.setFillColour(GREEN);
			} else if (mouseOver(mouseX, mouseY, 180, 240, 32, 32)) {
				Game.player.setFillColour(LIGHT_GRAY);
			} else if (mouseOver(mouseX, mouseY, 215, 240, 32, 32)) {
				Game.player.setFillColour(MAGENTA);
			} else if (mouseOver(mouseX, mouseY, 250, 240, 32, 32)) {
				Game.player.setFillColour(ORANGE);
			} else if (mouseOver(mouseX, mouseY, 285, 240, 32, 32)) {
				Game.player.setFillColour(PINK);
			} else if (mouseOver(mouseX, mouseY, 320, 240, 32, 32)) {
				Game.player.setFillColour(RED);
			} else if (mouseOver(mouseX, mouseY, 355, 240, 32, 32)) {
				Game.player.setFillColour(WHITE);
			} else if (mouseOver(mouseX, mouseY, 390, 240, 32, 32)) {
				Game.player.setFillColour(YELLOW);
			} else if (mouseOver(mouseX, mouseY, 5, 310, 32, 32)) {
				Game.player.setOutlineColour(CYAN);
			} else if (mouseOver(mouseX, mouseY, 40, 310, 32, 32)) {
				Game.player.setOutlineColour(BLUE);
			} else if (mouseOver(mouseX, mouseY, 75, 310, 32, 32)) {
				Game.player.setOutlineColour(DARK_GRAY);
			} else if (mouseOver(mouseX, mouseY, 110, 310, 32, 32)) {
				Game.player.setOutlineColour(GRAY);
			} else if (mouseOver(mouseX, mouseY, 145, 310, 32, 32)) {
				Game.player.setOutlineColour(GREEN);
			} else if (mouseOver(mouseX, mouseY, 180, 310, 32, 32)) {
				Game.player.setOutlineColour(LIGHT_GRAY);
			} else if (mouseOver(mouseX, mouseY, 215, 310, 32, 32)) {
				Game.player.setOutlineColour(MAGENTA);
			} else if (mouseOver(mouseX, mouseY, 250, 310, 32, 32)) {
				Game.player.setOutlineColour(ORANGE);
			} else if (mouseOver(mouseX, mouseY, 285, 310, 32, 32)) {
				Game.player.setOutlineColour(PINK);
			} else if (mouseOver(mouseX, mouseY, 320, 310, 32, 32)) {
				Game.player.setOutlineColour(RED);
			} else if (mouseOver(mouseX, mouseY, 355, 310, 32, 32)) {
				Game.player.setOutlineColour(WHITE);
			} else if (mouseOver(mouseX, mouseY, 390, 310, 32, 32)) {
				Game.player.setOutlineColour(YELLOW);
			} else if (mouseOver(mouseX, mouseY, 5, 380, 32, 32)) {
				Game.player.setUsernameColour(CYAN);
			} else if (mouseOver(mouseX, mouseY, 40, 380, 32, 32)) {
				Game.player.setUsernameColour(BLUE);
			} else if (mouseOver(mouseX, mouseY, 75, 380, 32, 32)) {
				Game.player.setUsernameColour(DARK_GRAY);
			} else if (mouseOver(mouseX, mouseY, 110, 380, 32, 32)) {
				Game.player.setUsernameColour(GRAY);
			} else if (mouseOver(mouseX, mouseY, 145, 380, 32, 32)) {
				Game.player.setUsernameColour(GREEN);
			} else if (mouseOver(mouseX, mouseY, 180, 380, 32, 32)) {
				Game.player.setUsernameColour(LIGHT_GRAY);
			} else if (mouseOver(mouseX, mouseY, 215, 380, 32, 32)) {
				Game.player.setUsernameColour(MAGENTA);
			} else if (mouseOver(mouseX, mouseY, 250, 380, 32, 32)) {
				Game.player.setUsernameColour(ORANGE);
			} else if (mouseOver(mouseX, mouseY, 285, 380, 32, 32)) {
				Game.player.setUsernameColour(PINK);
			} else if (mouseOver(mouseX, mouseY, 320, 380, 32, 32)) {
				Game.player.setUsernameColour(RED);
			} else if (mouseOver(mouseX, mouseY, 355, 380, 32, 32)) {
				Game.player.setUsernameColour(WHITE);
			} else if (mouseOver(mouseX, mouseY, 390, 380, 32, 32)) {
				Game.player.setUsernameColour(YELLOW);
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-96, 450, 192, 64)) {
				Game.gameState = State.MAINMENU;
				handler.removeObject(Game.player);
				
				DataManager.saveData();
			}
		} else if (Game.gameState == State.INFO) {
			if (mouseOver(mouseX, mouseY, Game.XBOUND/2+100, 500, 192, 64)) {
				Game.gameState = State.INFO2;
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-96, 500, 192, 64)) {
				Game.gameState = State.MAINMENU;
			}
		} else if (Game.gameState == State.INFO2) {
			if (mouseOver(mouseX, mouseY, Game.XBOUND/2-292, 500, 192, 64)) {
				Game.gameState = State.INFO;
			}
		}
	}
	
	/**
	 * Checks to see if the user has clicked over a button.
	 * @param mouseX - The x coordinate of the mouse.
	 * @param mouseY - The y coordinate of the mouse.
	 * @param x - The x coordinate of the button.
	 * @param y - The y coordinate of the button.
	 * @param width - The width of the button.
	 * @param height - The height of the button.
	 * @return True if the mouse has clicked over the button, false if the mouse
	 * hasn't.
	 */
	private boolean mouseOver(float mouseX, float mouseY, int x, int y, int width, int height) {
		if (x <= mouseX && mouseX <= x+width && y <= mouseY && mouseY <= y+height) {
			AudioManager.getSound("Select").play();
			return true;
		}
		else return false;
	}
}
