package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Paintball;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Player;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Spawner;
import io.github.hyperbyteindustries.pixel_paintballers.managers.AudioManager;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet.PacketType;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet00Connect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet02PlayerMove;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet03PlayerShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet04Damage;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet05PlayerDeath;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet06LevelUp;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet07Spawn;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet08EnemyShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet09TargetChange;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu;
import io.github.hyperbyteindustries.pixel_paintballers.ui.Menu.State;

/**
 * Represents the client of the game's multiplayer system.
 * When the game is initialised, this class is responsible for the management of data sent from
 * the server, manipulating the game in correlation.
 * @author Ramone Graham
 *
 */
public class Client implements Runnable {

	private boolean running = false;
	private Thread thread;

	private Game game;
	private Handler handler;
	
	private InetAddress serverIPAddress;
	private DatagramSocket socket;
	
	private final String infoPrefix = "[Client INFO]: ", errorPrefix = "[Client ERROR]: ";
	
	/**
	 * Creates a new client.
	 * @param game - An instance of the Game class, used to create game objects.
	 * @param handler - An instance of the Handler class, used to add and remove objects in the
	 * game.
	 * @param serverIPAddress - The target IP address of the server.
	 */
	public Client(Game game, Handler handler, String serverIPAddress) {
		this.game = game;
		this.handler = handler;
		
		try {
			this.serverIPAddress = InetAddress.getByName(serverIPAddress);
			socket = new DatagramSocket();
		} catch (UnknownHostException | SocketException exception) {
			System.err.print(new Date() + " " + errorPrefix +
					"An exception occured whilst creating the client - ");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Starts execution of the client.
	 */
	public synchronized void start() {
		running = true;
		thread = new Thread(this, Game.TITLE + " [CLIENT]");
		
		thread.start();
	}
	
	/**
	 * Stops execution of the client.
	 */
	public synchronized void stop() {
		running = false;
		
		try {
			thread.join();
		} catch (InterruptedException exception) {
			System.err.print(new Date() + " " + errorPrefix +
					"An exception occured whilst stopping the thread - ");
			exception.printStackTrace();
		}
	}

	// Runs the client loop.
	public void run() {
		while (running) {
			byte[] data = new byte[3072];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
			} catch (IOException exception) {
				System.err.print(new Date() + " " + errorPrefix +
						"An exception occured whilst receiving a packet - ");
				exception.printStackTrace();
			}
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
		
		stop();
	}
	
	/**
	 * Analyses the received packet data and manages it accordingly.
	 * @param data - The byte array of data received from the server.
	 * @param address - The source IP address of the packet.
	 * @param port - The source port of the packet.
	 */
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		
		if (message.length() > 4 && message.substring(0, 4).equalsIgnoreCase("Pong")) {
			System.out.println(new Date() + " " + infoPrefix + "Pong recieved from SERVER [" +
					address.getHostAddress() + ":" + port + "].");
			
			String[] dataArray = message.substring(4).split(",");
			boolean canConnect = Integer.parseInt(dataArray[0]) == 1;
			
			if (canConnect) {
				Menu.menuState = State.GAME;
				
				handler.startGame();
				
				Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
						Game.player.getX(), Game.player.getY(), Game.player.getFillColour(),
						Game.player.getOutlineColour(), Game.player.getUsernameColour(),
						Game.player.health, false, Game.player.spectator);
				packet.writeData(this);
			} else JOptionPane.showMessageDialog(game, dataArray[1], "Connection refused!",
					JOptionPane.ERROR_MESSAGE);
		} else {
			Packet packet;
			PacketType type = Packet.lookupPacket(message.substring(0, 2));
			
			switch (type) {
			case INVALID:
				System.err.println(new Date() + " " + errorPrefix +
						"Invalid packet received from [" + address.getHostAddress() + ":" +
						port + "]: " + message);
				
				break;
			case CONNECT:
				packet = new Packet00Connect(data);
				
				IPlayer player = new IPlayer(((Packet00Connect) packet).getX(),
						((Packet00Connect) packet).getY(),
						((Packet00Connect) packet).getUsername(),
						((Packet00Connect) packet).getFillColour(),
						((Packet00Connect) packet).getOutlineColour(),
						((Packet00Connect) packet).getUsernameColour(), address, port);
				
				player.health = ((Packet00Connect) packet).getHealth();
				player.spectator = ((Packet00Connect) packet).isSpectator();
				
				handleConnect(player, (Packet00Connect) packet);
				
				break;
			case DISCONNECT:
				packet = new Packet01Disconnect(data);
				
				handleDisconnect((Packet01Disconnect) packet);
				
				break;
			case PLAYERMOVE:
				packet = new Packet02PlayerMove(data);
				
				handlePlayerMove((Packet02PlayerMove) packet);
				
				break;
			case PLAYERSHOT:
				packet = new Packet03PlayerShot(data);
				
				handlePlayerShot((Packet03PlayerShot) packet);
				
				break;
			case DAMAGE:
				packet = new Packet04Damage(data);
				
				handleDamageTaken((Packet04Damage) packet);
				
				break;
			case PLAYERDEATH:
				packet = new Packet05PlayerDeath(data);
				
				handlePlayerDeath((Packet05PlayerDeath) packet);
				
				break;
			case LEVELUP:
				packet = new Packet06LevelUp(data);
				
				handleLevelUp((Packet06LevelUp) packet);
				
				break;
			case SPAWN:
				packet = new Packet07Spawn(data);
				
				handleSpawn((Packet07Spawn) packet);
				
				break;
			case ENEMYSHOT:
				packet = new Packet08EnemyShot(data);
				
				handleEnemyShot((Packet08EnemyShot) packet);
				
				break;
			case TARGETCHANGE:
				packet = new Packet09TargetChange(data);
				
				handleTargetChange((Packet09TargetChange) packet);

				break;
			}
		}
	}

	/**
	 * Adds an online player to the game handler.
	 * @param player - The player to add to the game handler.
	 * @param packet - The connect packet associated with the player.
	 */
	private void handleConnect(IPlayer player, Packet00Connect packet) {
		if (!player.spectator) handler.addEntity(player);
		
		if (!packet.isAlreadyConnected()) System.out.println(new Date() + " " + infoPrefix +
				player.getUsername() + " has joined the game.");
	}

	/**
	 * Removes a disconnected player from the game handler.
	 * @param packet - The disconnect packet associated with the player disconnecting.
	 */
	private void handleDisconnect(Packet01Disconnect packet) {
		if (Game.player.getUsername().equalsIgnoreCase(packet.getUsername())) {
			Game.paused = false;
			handler.getEntities().clear();
			Menu.menuState = State.MULTIPLAYER_MENU;
		} else {
			synchronized (handler.getEntities()) {
				for (int i = 0; i < handler.getEntities().size(); i++) {
					Entity entity = handler.getEntities().get(i);
					
					if (entity.getID() == ID.IPLAYER) {
						IPlayer player = (IPlayer) entity;
						
						if (player.getUsername().equals(packet.getUsername())) {
							if (!player.spectator) handler.removeEntity(player);
							
							System.out.println(new Date() + " " + infoPrefix +
									player.getUsername() + " has left the game.");
							
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Either sets a moving player's velocity, or sets a player's stationary coordinates.
	 * @param packet - The move packet associated with the moving / stationary player.
	 */
	private void handlePlayerMove(Packet02PlayerMove packet) {
		synchronized (handler.getEntities()) {
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (entity.getID() == ID.IPLAYER) {
					IPlayer player = (IPlayer) entity;
					
					if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
						if (packet.isPlayerMoving()) {
							player.setVelX(packet.getVarX());
							player.setVelY(packet.getVarY());
							
							System.out.println(new Date() + " " + infoPrefix +
									player.getUsername() + " has moved.");
						} else {
							player.setVelX(0);
							player.setVelY(0);
							player.setX(packet.getVarX());
							player.setY(packet.getVarY());
							
							System.out.println(new Date() + " " + infoPrefix +
									player.getUsername() + " has stopped moving.");
						}
						
						break;
					}
				}
			}
		}
	}

	/**
	 * Creates a paintball shot by a player.
	 * @param packet - The shot packet associated with the player shooting.
	 */
	private void handlePlayerShot(Packet03PlayerShot packet) {
		synchronized (handler.getEntities()) {
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (entity.getID() == ID.IPLAYER) {
					IPlayer player = (IPlayer) entity;
					
					if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
						Paintball paintball = new Paintball(ID.PAINTBALL, packet.getX(),
								packet.getY(), game, handler, player);
						
						handler.addEntity(paintball);
						
						paintball.setVelX(packet.getVelX());
						paintball.setVelY(packet.getVelY());
						
						AudioManager.getSound("Shot").play(1, Game.sfxVolume);
						
						System.out.println(new Date() + " " + infoPrefix + player.getUsername() +
								" has shot a paintball.");
						
						break;
					}
				}
			}
		}
	}

	/**
	 * Decreases a player's health based on the damage taken.
	 * @param packet - The damage packet associated with the player damaged.
	 */
	private void handleDamageTaken(Packet04Damage packet) {
		synchronized (handler.getEntities()) {
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (entity.getID() == ID.IPLAYER) {
					IPlayer player = (IPlayer) entity;
					
					if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
						player.health -= packet.getDamageTaken();
						
						System.out.println(new Date() + " " + infoPrefix + player.getUsername() +
								" has taken damage.");
						
						break;
					}
				}
			}
		}
	}

	/**
	 * Removes a dead player from the game.
	 * @param packet - The death packet associated with the player who died.
	 */
	private void handlePlayerDeath(Packet05PlayerDeath packet) {
		synchronized (handler.getEntities()) {
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (entity.getID() == ID.IPLAYER) {
					IPlayer player = (IPlayer) entity;
					
					if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
						handler.removeEntity(player);
						
						System.out.println(new Date() + " " + infoPrefix + player.getUsername() +
								" has died.");
						
						break;
					}
				}
			}
		}
	}

	/**
	 * Changes the level of the game.
	 * @param packet - The level-up packet associated with the level-up.
	 */
	private void handleLevelUp(Packet06LevelUp packet) {
		Spawner.level = packet.getLevel();
		
		System.out.println(new Date() + " " + infoPrefix + "The team is now level " +
				Spawner.level + ".");
	}

	/**
	 * Spawns a new wave of enemies into the game.
	 * @param packet - The spawn packet associated with the enemy wave created.
	 */
	private void handleSpawn(Packet07Spawn packet) {
		for (int i = 0; i < packet.getEnemyCount(); i++) {
			synchronized (handler.getEntities()) {
				for (int j = 0; j < handler.getEntities().size(); j++) {
					Entity entity = handler.getEntities().get(j);
					
					if (entity.getID() == ID.PLAYER || entity.getID() == ID.IPLAYER) {
						Player player = (Player) entity;
						
						if (player.getUsername().equals(packet.getTarget(i))) {
							IEnemy enemy = new IEnemy(packet.getID(i), packet.getX(i),
									packet.getY(i), handler, null, packet.getEnemyNumber(i));
							
							handler.addEntity(enemy);
							
							enemy.attackTimer = packet.getAttackTimer(i);
							enemy.shootTimer = packet.getShootTimer(i);
							
							enemy.setTarget(player);
							
							break;
						}
					}
				}
			}
		}
		
		System.out.println(new Date() + " " + infoPrefix + "The next enemy wave has spawned.");
	}

	/**
	 * Creates a paintball shot by an enemy.
	 * @param packet - The shot packet associated with the enemy shooting.
	 */
	private void handleEnemyShot(Packet08EnemyShot packet) {
		synchronized (handler.getEntities()) {
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (entity.getID() == ID.IENEMY || entity.getID() == ID.IMOVINGENEMY ||
						entity.getID() == ID.IBOUNCYENEMY || entity.getID() == ID.IHOMINGENEMY) {
					IEnemy enemy = (IEnemy) entity;
					
					if (enemy.getEnemyNumber() == packet.getEnemyNumber()) {
						Paintball paintball = new Paintball(packet.getID(), packet.getX(),
								packet.getY(), game, handler, enemy);
						
						handler.addEntity(paintball);
						
						paintball.setVelX(packet.getVelX());
						paintball.setVelY(packet.getVelY());
						
						AudioManager.getSound("Shot").play(1, Game.sfxVolume);
						
						System.out.println(new Date() + " " + infoPrefix + "Enemy " +
								enemy.getEnemyNumber() + " has shot a paintball.");
						
						break;
					}
				}
			}
		}
	}

	/**
	 * Changes the enemy's target.
	 * @param packet - The target change packet associated with the enemy.
	 */
	private void handleTargetChange(Packet09TargetChange packet) {
		synchronized (handler.getEntities()) {
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity entity = handler.getEntities().get(i);
				
				if (entity.getID() == ID.IENEMY || entity.getID() == ID.IMOVINGENEMY ||
						entity.getID() == ID.IBOUNCYENEMY || entity.getID() == ID.IHOMINGENEMY) {
					IEnemy enemy = (IEnemy) entity;
					
					if (enemy.getEnemyNumber() == packet.getEnemyNumber()) {
						for (int j = 0; j < handler.getEntities().size(); j++) {
							Entity entity2 = handler.getEntities().get(j);
							
							if (entity2.getID() == ID.PLAYER || entity2.getID() == ID.IPLAYER) {
								Player player = (Player) entity2;
								
								if (player.getUsername().equals(packet.getTarget())) {
									enemy.setTarget(player);
									
									System.out.println(new Date() + " " + infoPrefix + "Enemy " +
											enemy.getEnemyNumber() + " has changed its target.");
									
									break;
								}
							}
						}
						
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Sends a ping packet to the server, testing the connection and querying availability.
	 */
	public void pingServer() {
		sendData(("Ping" + Game.player.getUsername() + "," +
				(Game.player.spectator?1:0)).getBytes());
	}

	/**
	 * Sends a packet of data to the server.
	 * @param data - The packet of data to send to the server.
	 */
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIPAddress, 1331);
		
		try {
			socket.send(packet);
		} catch (IOException exception) {
			System.err.print(new Date() + " " + errorPrefix +
					"An exception occured whilst sending a packet - ");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Sets the target IP address of the server to the given address.
	 * @param serverIPAddress - The new target IP address to be set.
	 */
	public void setTargetIPAddress(InetAddress serverIPAddress) {
		this.serverIPAddress = serverIPAddress;
	}
	
	/**
	 * Gets the target IP address of the server.
	 * @return The target IP address of the server.
	 */
	public InetAddress getTargetIPAddress() {
		return serverIPAddress;
	}
}
