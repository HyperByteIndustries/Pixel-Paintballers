package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.*;
import static java.awt.Font.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import io.github.hyperbyteindustries.pixel_paintballers.Game.State;

/**
 * Represents the menu system and mouse input handler of the game.
 * When constructed, this class is responsible for the management of the menu
 * display and certain mouse events triggered by the user.
 * @author Ramone Graham
 *
 */
public class Menu extends MouseAdapter {

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
	public Menu(Handler handler) {
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
		if (Game.gameState == State.GAME) {
			if (HeadsUpDisplay.health == 0) {
				handler.objects.clear();
				Game.gameState = State.GAMEOVER;
			}
		}
	}
	
	/**
	 * Updates the visuals for the menu.
	 * @param graphics2d - The graphics used to update the visuals.
	 */
	public void render(Graphics2D graphics2d) {
		if (Game.gameState == State.LOGO) graphics2d.drawImage(companyLogo, 0, 0,
				null);
		else if (Game.gameState == State.TITLESCREEN) {
			graphics2d.drawImage(gameLogo, 0, 0, null);
			
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
		} if (Game.gameState == State.GAME) {
			if (Game.paused) {
				graphics2d.setFont(menuHeader);
				graphics2d.setColor(WHITE);
				graphics2d.drawString("Paused", Game.XBOUND/2-(("Paused".length()-1)
						/2*53), 40);
				
				graphics2d.setFont(menuSelect);
				graphics2d.setColor(RED);
				graphics2d.fillRect(Game.XBOUND/2-96, Game.YBOUND/2-96, 192, 64);
				graphics2d.setColor(WHITE);
				graphics2d.drawRect(Game.XBOUND/2-96, Game.YBOUND/2-96, 192, 64);
				graphics2d.setColor(BLUE);
				graphics2d.drawString("Resume", Game.XBOUND/2-(("Resume".length()-1)
						/2*25), Game.YBOUND/2-55);
				
				graphics2d.setColor(RED);
				graphics2d.fillRect(Game.XBOUND/2-96, Game.YBOUND/2+32, 192, 64);
				graphics2d.setColor(WHITE);
				graphics2d.drawRect(Game.XBOUND/2-96, Game.YBOUND/2+32, 192, 64);
				graphics2d.setColor(BLUE);
				graphics2d.drawString("Quit", Game.XBOUND/2-(("Quit".length()-1)/2*
						30), Game.YBOUND/2+72);
			}
		} else if (Game.gameState == State.GAMEOVER) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Game Over!", Game.XBOUND/2-(("Quit".length()-1)/
					2*150), 40);
			
			graphics2d.setFont(menuText);
			graphics2d.drawString("Your final score was: " + HeadsUpDisplay.score,
					10, 175);
			graphics2d.drawString("Your final level was: " + HeadsUpDisplay.level,
					10, 195);
			
			graphics2d.setFont(menuSelect);graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-92, Game.YBOUND-135, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-92, Game.YBOUND-135, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Play again", (Game.XBOUND/2)-67, Game.YBOUND-95);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-92, Game.YBOUND-65, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-92, Game.YBOUND-65, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Quit", (Game.XBOUND/2)-25, Game.YBOUND-23);
		}
	}
	
	// Invoked when the mouse is pressed.
	public void mousePressed(MouseEvent e) {
		float mouseX = (float) e.getX();
		float mouseY = (float) e.getY();
		
		if (Game.gameState == State.TITLESCREEN) {
			if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-64, (Game.YBOUND/2)-96,
					128, 64)) {
				Game.gameState = State.GAME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-64, (Game.YBOUND/2)
					+32, 128, 64)) System.exit(1);
		} else if (Game.gameState == State.GAME) {
			if (!(Game.paused)) {
				Paintball paintball = new Paintball(Game.player.getX()+12,
						Game.player.getY()+12, ID.PAINTBALL, handler, Game.player);
				
				handler.addObject(paintball);
				
				float diffX = paintball.getX()-(mouseX-4), diffY = paintball.getY()-
						(mouseY-4), distance = (float) Math.sqrt((paintball.getX()-
								mouseX)*(paintball.getX()-mouseX) +
								(paintball.getY()-mouseY)*(paintball.getY()-
										mouseY));
				
				paintball.setVelX((float) ((-1.0/distance) * diffX)*7);
				paintball.setVelY((float) ((-1.0/distance) * diffY)*7);
			} else {
				if (mouseOver(mouseX, mouseY, Game.XBOUND/2-96, Game.YBOUND/2-96,
						192, 64)) Game.paused = false;
				else if (mouseOver(mouseX, mouseY, Game.XBOUND/2-96, Game.YBOUND/2+
						32, 192, 64)) {
					Game.paused = false;
					handler.objects.clear();
					Game.gameState = State.TITLESCREEN;
				}
			}
		} else if (Game.gameState == State.GAMEOVER) {
			if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, Game.YBOUND-135, 192,
					64)) {
				Game.gameState = State.GAME;
				
				handler.startGame();
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, Game.YBOUND-65,
					192, 64)) Game.gameState = State.TITLESCREEN;
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
	private boolean mouseOver(float mouseX, float mouseY, int x, int y, int width,
			int height) {
		if (x <= mouseX && mouseX <= x+width && y <= mouseY && mouseY <= y+height)
			return true;
		else return false;
	}
}
