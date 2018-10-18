package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.*;
import static java.awt.Font.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import io.github.hyperbyteindustries.pixel_paintballers.Game.Mode;
import io.github.hyperbyteindustries.pixel_paintballers.Game.State;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet00Connect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet03PlayerShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet05PlayerDeath;

/**
 * Represents the menu system and mouse input handler of the game.
 * When constructed, this class is responsible for the management of the menu
 * display and certain mouse events triggered by the user.
 * @author Ramone Graham
 *
 */
public class Menu extends MouseAdapter {

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
		if (Game.gameState == State.GAME) {
			if (Game.player.health == 0) {
				Packet05PlayerDeath packet = new Packet05PlayerDeath(Game.player.getUsername());
				packet.writeData(game.client);
				
				Game.paused = false;
				handler.getObjects().clear();
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
		} else if (Game.gameState == State.MAINMENU) {
			graphics2d.setFont(menuHeader);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Main menu", Game.XBOUND/2-(("Main menu".length()-1)/2*35),
					40);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Create a server", 5, 100);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Select a gamemode", 5, 125);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 150, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 150, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("PVP", 133-(("PVP".length()-1)/2*18), 190);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("It's a free-for-all as a maximum of 4 players", 269, 165);
			graphics2d.drawString("battle it out against each other!", 269, 180);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 225, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 225, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Team survival", 133-(("Team survival".length()-1)/2*16), 265);
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("4 people band together to battle the enemy", 269, 240);
			graphics2d.drawString("waves! Careful tough, friendly fire is enabled!", 269, 255);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(WHITE);
			graphics2d.drawString("Connect to a server", 5, 325);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 335, 256, 48);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 335, 256, 48);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Change address", 128-("Change address".length()-1)/2*18, 366);
			
			graphics2d.setFont(menuText);
			graphics2d.setColor(YELLOW);
			graphics2d.drawString("Target server address: " +
					game.client.getTargetIPAddress().getHostAddress(), 269, 350);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect(5, 390, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(5, 390, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Join game", 133-(("Join game".length()-1)/2*16), 430);

			graphics2d.setColor(RED);
			graphics2d.fillRect(275, 390, 256, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(275, 390, 256, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Spectate game", 403-(("Spectate game".length()-1)/2*17), 430);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect(Game.XBOUND/2-96, 500, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect(Game.XBOUND/2-96, 500, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Back", Game.XBOUND/2-(("Back".length()-1)/2*35), 540);
		} else if (Game.gameState == State.GAME) {
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
			graphics2d.drawString("You died!", Game.XBOUND/2-(("You died!".length()-1)/
					2*35), 40);
			
			graphics2d.setFont(menuSelect);
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-(192/2), 250, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-(192/2), 250, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Rejoin", Game.XBOUND/2-(("Rejoin".length()-1)/2*22), 290);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-(192/2), 350, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-(192/2), 350, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Spectate", Game.XBOUND/2-(("Spectate".length()-1)/2*22), 390);
			
			graphics2d.setColor(RED);
			graphics2d.fillRect((Game.XBOUND/2)-(192/2), 450, 192, 64);
			graphics2d.setColor(WHITE);
			graphics2d.drawRect((Game.XBOUND/2)-(192/2), 450, 192, 64);
			graphics2d.setColor(BLUE);
			graphics2d.drawString("Quit", Game.XBOUND/2-(("Quit".length()-1)/2*30), 490);
		}
	}
	
	// Invoked when the mouse is pressed.
	public void mousePressed(MouseEvent e) {
		float mouseX = (float) e.getX();
		float mouseY = (float) e.getY();
		
		if (Game.gameState == State.TITLESCREEN) {
			if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-64, (Game.YBOUND/2)-96,
					128, 64)) {
				Game.gameState = State.MAINMENU;
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-64, (Game.YBOUND/2)
					+32, 128, 64)) System.exit(1);
		} else if (Game.gameState == State.MAINMENU) {
			if (mouseOver(mouseX, mouseY, 5, 150, 256, 64)) {
				Game.gameMode = Game.Mode.PLAYER;
				Server.gameMode = Server.Mode.PVP;
				
				game.server = new Server();
				game.server.start();
				
				try {
					game.client.setTargetIPAddress(InetAddress.getByName("localhost"));
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				
				game.client.sendData(("Ping" + Game.player.getUsername() + "," +
				Game.gameMode.name()).getBytes());
			} else if (mouseOver(mouseX, mouseY, 5, 225, 256, 64)) {
				Game.gameMode = Game.Mode.PLAYER;
				Server.gameMode = Server.Mode.TEAMSURVIVAL;
				
				game.server = new Server();
				game.server.start();
				
				try {
					game.client.setTargetIPAddress(InetAddress.getByName("localhost"));
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				
				game.client.sendData(("Ping" + Game.player.getUsername() + "," +
						Game.gameMode.name()).getBytes());
			} else if (mouseOver(mouseX, mouseY, 5, 335, 256, 48)) {
				setTargetIPAddress();
			} else if (mouseOver(mouseX, mouseY, 5, 390, 256, 64)) {
				Game.gameMode = Game.Mode.PLAYER;
				
				game.client.sendData(("Ping" + Game.player.getUsername() + "," +
						Game.gameMode.name()).getBytes());
			} else if (mouseOver(mouseX, mouseY, 275, 390, 256, 64)) {
				Game.gameMode = Game.Mode.SPECTATOR;
				
				game.client.sendData(("Ping" + Game.player.getUsername() + "," +
						Game.gameMode.name()).getBytes());
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-96, 500, 192, 64)) {
				Game.gameState = State.TITLESCREEN;
			}
		} else if (Game.gameState == State.GAME) {
			if (!(Game.paused)) {
				if (Game.gameMode == Mode.PLAYER) {
					Paintball paintball = new Paintball(Game.player.getX()+12,
							Game.player.getY()+12,
							ID.PAINTBALL, game, handler, Game.player);
					
					handler.addObject(paintball);
					
					float diffX = paintball.getX()-(mouseX-4), diffY =
							paintball.getY()-(mouseY-4), distance = (float)
							Math.sqrt((paintball.getX()-mouseX)*(paintball.getX()-
									mouseX) + (paintball.getY()-mouseY)*
									(paintball.getY()-mouseY));
					
					paintball.setVelX((float) ((-1.0/distance) * diffX)*7);
					paintball.setVelY((float) ((-1.0/distance) * diffY)*7);
					
					Packet03PlayerShot packet = new Packet03PlayerShot(Game.player.getUsername(),
							paintball.getX(), paintball.getY(), paintball.getVelX(),
							paintball.getVelY());
					packet.writeData(game.client);
				}
			} else {
				if (mouseOver(mouseX, mouseY, Game.XBOUND/2-96, Game.YBOUND/2-96, 192, 64))
					Game.paused = false;
				else if (mouseOver(mouseX, mouseY, Game.XBOUND/2-96, Game.YBOUND/2+32, 192,
						64)) {
					Packet01Disconnect packet =
							new Packet01Disconnect(Game.player.getUsername());
					packet.writeData(game.client);
					
					if (!(game.server == null)) {
						game.server.stop();
					}
				}
			}
		} else if (Game.gameState == State.GAMEOVER) {
			if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, 250, 192, 64)) {
				Game.gameState = State.GAME;
				
				handler.startGame();
				
				Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
						Game.player.getX(), Game.player.getY(), Game.player.health, false,
						Game.gameMode);
				packet.writeData(game.client);
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, 350, 192, 64)) {
				Game.gameMode = Mode.SPECTATOR;
				Game.gameState = State.GAME;
				
				handler.startGame();
				
				Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
						Game.player.getX(), Game.player.getY(), Game.player.health, false,
						Game.gameMode);
				packet.writeData(game.client);
			} else if (mouseOver(mouseX, mouseY, (Game.XBOUND/2)-92, 450, 192, 64)) {
				if (!(game.server == null)) {
					game.server.stop();
				}
				
				Game.gameState = State.MAINMENU;
			}
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
