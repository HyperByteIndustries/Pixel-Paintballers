package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.Game.State;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.HeadsUpDisplay;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Paintball;
import io.github.hyperbyteindustries.pixel_paintballers.Player;
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
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet08EnemyMove;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet09EnemyShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet10TargetChange;

/**
 * Represents the client of the game's multiplayer system.
 * When the game is initialised, this class is responsible for the management of data sent from
 * the server, manipulating the game in correlation.
 * @author Ramone Graham
 *
 */
public class Client implements Runnable {

	private Thread thread;
	private boolean running = false;

	private Game game;
	private Handler handler;
	
	private InetAddress serverIPAddress;
	private DatagramSocket socket;
	
	private String infoPrefix = "[Client INFO]: ", errorPrefix = "[Client ERROR]: ";
	
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
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts execution of the client.
	 */
	public synchronized void start() {
		thread = new Thread(this, Game.TITLE + " [CLIENT]");
		thread.start();
		running = true;
	}
	
	/**
	 * Stops execution of the client.
	 */
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Runs the client loop.
	public void run() {
		while (running) {
			byte[] data = new byte[2048];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
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
			System.out.println(infoPrefix + "Pong recieved from SERVER [" +
					address.getHostAddress() + ":" + port + "].");
			
			String[] dataArray = message.substring(4).split(",");
			
			boolean canConnect = Integer.parseInt(dataArray[0]) == 1;
			
			if (canConnect) {
				Game.gameState = State.GAME;
				
				handler.startGame();
				
				Packet00Connect packet = new Packet00Connect(Game.player.getUsername(),
						Game.player.getX(), Game.player.getY(), Game.player.health, false,
						Game.gameMode);
				packet.writeData(this);
			} else JOptionPane.showMessageDialog(game, dataArray[1], "Connection refused!",
					JOptionPane.ERROR_MESSAGE);
		} else {
			PacketType type = Packet.lookupPacket(message.substring(0, 2));
			Packet packet;
			
			switch (type) {
			default:
				break;
			case INVALID:
				System.err.println(errorPrefix + "Invalid packet received from [" +
						address.getHostAddress() + ":" + port + "]: " + message);
				
				break;
			case CONNECT:
				packet = new Packet00Connect(data);
				
				IPlayer player = new IPlayer(((Packet00Connect) packet).getX(),
						((Packet00Connect) packet).getY(), ID.IPLAYER,
						((Packet00Connect) packet).getUsername(), address, port);
				
				player.health = ((Packet00Connect) packet).getHealth();
				
				player.gameMode = ((Packet00Connect) packet).getGameMode();
				
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
			case ENEMYMOVE:
				packet = new Packet08EnemyMove(data);
				
				handleEnemyMove((Packet08EnemyMove) packet);
				
				break;
			case ENEMYSHOT:
				packet = new Packet09EnemyShot(data);
				
				handleEnemyShot((Packet09EnemyShot) packet);
				
				break;
			case TARGETCHANGE:
				packet = new Packet10TargetChange(data);
				
				handleTargetChange((Packet10TargetChange) packet);

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
		if (player.gameMode == Game.Mode.PLAYER) handler.addObject(player);
		
		if (!(packet.isAlreadyConnected()))
			System.out.println(infoPrefix + player.getUsername() + " has joined the game.");
	}

	/**
	 * Removes a disconnected player from the game handler.
	 * @param packet - The disconnect packet associated with the player disconnecting.
	 */
	private void handleDisconnect(Packet01Disconnect packet) {
		if (Game.player.getUsername().equalsIgnoreCase(packet.getUsername())) {
			Game.paused = false;
			handler.getObjects().clear();
			Game.gameState = State.MAINMENU;
		} else {
			for (int i = 0; i < handler.getObjects().size(); i++) {
				GameObject tempObject = handler.getObjects().get(i);
				
				if (tempObject.getID() == ID.IPLAYER) {
					IPlayer player = (IPlayer) tempObject;
					
					if (player.getUsername().equals(packet.getUsername())) {
						if (player.gameMode == Game.Mode.PLAYER) handler.removeObject(player);
						
						System.out.println(infoPrefix + player.getUsername() +
								" has left the game.");
						
						break;
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
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) {
				IPlayer player = (IPlayer) tempObject;
				
				if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
					if (packet.playerMoving()) {
						player.setVelX(packet.getVarX());
						player.setVelY(packet.getVarY());
						
						System.out.println(infoPrefix + player.getUsername() + " has moved.");
					} else {
						player.setVelX(0);
						player.setVelY(0);
						player.setX(packet.getVarX());
						player.setY(packet.getVarY());
						
						System.out.println(infoPrefix + player.getUsername() +
								" has stopped moving.");
					}
					
					break;
				}
			}
		}
	}

	/**
	 * Creates a paintball shot by a player.
	 * @param packet - The shot packet associated with the player shooting.
	 */
	private void handlePlayerShot(Packet03PlayerShot packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) {
				IPlayer player = (IPlayer) tempObject;
				
				if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
					Paintball paintball = new Paintball(packet.getX(), packet.getY(),
							ID.PAINTBALL, game, handler, player);
					
					handler.addObject(paintball);
					
					paintball.setVelX(packet.getVelX());
					paintball.setVelY(packet.getVelY());
					
					System.out.println(infoPrefix + player.getUsername() +
							" has shot a paintball.");
					
					break;
				}
			}
		}
	}

	/**
	 * Decreases a player's health based on the damage taken.
	 * @param packet - The damage packet associated with the player damaged.
	 */
	private void handleDamageTaken(Packet04Damage packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) {
				IPlayer player = (IPlayer) tempObject;
				
				if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
					player.health -= packet.getDamageTaken();
					
					System.out.println(infoPrefix + player.getUsername() + " has taken damage.");
					
					break;
				}
			}
		}
	}

	/**
	 * Removes a dead player from the game.
	 * @param packet - The death packet associated with the player who died.
	 */
	private void handlePlayerDeath(Packet05PlayerDeath packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IPLAYER) {
				IPlayer player = (IPlayer) tempObject;
				
				if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
					handler.removeObject(player);
					
					System.out.println(infoPrefix + player.getUsername() + " has died.");
					
					break;
				}
			}
		}
	}

	/**
	 * Changes the level of the game.
	 * @param packet - The level-up packet associated with the level-up.
	 */
	private void handleLevelUp(Packet06LevelUp packet) {
		HeadsUpDisplay.level = packet.getLevel();
		
		System.out.println(infoPrefix + "The team is now level " + HeadsUpDisplay.level + "!");
	}

	/**
	 * Spawns a new wave of enemies into the game.
	 * @param packet - The spawn packet associated with the enemy wave created.
	 */
	private void handleSpawn(Packet07Spawn packet) {
		for (int i = 0; i < packet.getEnemyCount(); i++) {
			for (int j = 0; j < handler.getObjects().size(); j++) {
				GameObject tempObject = handler.getObjects().get(j);
				
				if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER) {
					Player player = (Player) tempObject;
					
					if (player.getUsername().equals(packet.getTarget(i))) {
						IEnemy enemy = new IEnemy(packet.getX(i), packet.getY(i),
								packet.getID(i), handler, null, packet.getEnemyNumber(i));
						
						handler.addObject(enemy);
						
						enemy.attackTime = packet.getAttackTime(i);
						enemy.shootTime = packet.getShootTime(i);
						
						enemy.setTarget(player);
						
						break;
					}
				}
			}
		}
		
		System.out.println(infoPrefix + "The next enemy wave has spawned!");
	}

	/**
	 * Moves an enemy.
	 * @param packet - The move packet associated with the enemy moving.
	 */
	private void handleEnemyMove(Packet08EnemyMove packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IMOVINGENEMY) {
				IEnemy enemy = (IEnemy) tempObject;
				
				if (enemy.getEnemyNumber() == packet.getEnemyNumber()) {
					enemy.setX(packet.getX());
					enemy.setY(packet.getY());
					
					break;
				}
			}
		}
	}

	/**
	 * Creates a paintball shot by an enemy.
	 * @param packet - The shot packet associated with the enemy shooting.
	 */
	private void handleEnemyShot(Packet09EnemyShot packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IENEMY || tempObject.getID() == ID.IMOVINGENEMY ||
					tempObject.getID() == ID.IBOUNCYENEMY || tempObject.getID() ==
					ID.IHOMINGENEMY) {
				IEnemy enemy = (IEnemy) tempObject;
				
				if (enemy.getEnemyNumber() == packet.getEnemyNumber()) {
					Paintball paintball = new Paintball(packet.getX(),
							packet.getY(), packet.getID(), game, handler, enemy);
					
					handler.addObject(paintball);
					
					paintball.setVelX(packet.getVelX());
					paintball.setVelY(packet.getVelY());
					
					System.out.println(infoPrefix + "Enemy " + enemy.getEnemyNumber() +
							" has shot a paintball!");
					
					break;
				}
			}
		}
	}

	/**
	 * Changes the enemy's target.
	 * @param packet - The target change packet associated with the enemy.
	 */
	private void handleTargetChange(Packet10TargetChange packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.IENEMY || tempObject.getID() == ID.IMOVINGENEMY ||
					tempObject.getID() == ID.IBOUNCYENEMY || tempObject.getID() ==
					ID.IHOMINGENEMY) {
				IEnemy enemy = (IEnemy) tempObject;
				
				if (enemy.getEnemyNumber() == packet.getEnemyNumber()) {
					for (int j = 0; j < handler.getObjects().size(); j++) {
						GameObject tempObject2 = handler.getObjects().get(j);
						
						if (tempObject2.getID() == ID.PLAYER || tempObject2.getID() ==
								ID.IPLAYER) {
							Player player = (Player) tempObject2;
							
							if (player.getUsername().equals(packet.getTarget())) {
								enemy.setTarget(player);
								
								System.out.println(infoPrefix + "Enemy " +
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

	/**
	 * Sends a packet of data to the server.
	 * @param data - The packet of data to send to the server.
	 */
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIPAddress, 1331);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
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
	 * @return The current target IP address of the server.
	 */
	public InetAddress getTargetIPAddress() {
		return serverIPAddress;
	}
}
