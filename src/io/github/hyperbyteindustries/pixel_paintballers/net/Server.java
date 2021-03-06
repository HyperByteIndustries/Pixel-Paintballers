package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity.ID;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Paintball;
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

/**
 * Represents the server of the game's multiplayer system.
 * When the server is initialised, this class is responsible for the management of all data sent
 * from connected clients, relaying the data back to all other clients.
 * @author Ramone Graham
 *
 */
public class Server implements Runnable {

	/**
	 * Represents the game modes of the server.
	 * When utilised, this enum is responsible for defining the server's current game mode.
	 * @author Ramone Graham
	 *
	 */
	public enum Mode {
		PVP(), TEAMSURVIVAL();
	}
	
	public static Mode gameMode = null;

	private Thread thread;
	private boolean running = false;
	
	private Handler handler;
	private ServerSpawner spawner;
	
	private DatagramSocket socket;
	
	private final String infoPrefix = "[Server INFO]: ", warnPrefix = "[Server WARN]: ",
			errorPrefix = "[Server ERROR]: ";
	
	private LinkedList<IPlayer> connectedPlayers = new LinkedList<IPlayer>();
	
	/**
	 * Creates a new server.
	 */
	public Server() {
		handler = new Handler();
		spawner = new ServerSpawner(handler, this);
		
		try {
			socket = new DatagramSocket(1331);
		} catch (SocketException exception) {
			System.err.print(new Date() + " " + errorPrefix +
					"An exception occured whilst creating the server - ");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Starts execution of the server.
	 */
	public synchronized void start() {
		System.out.println(new Date() + " " + infoPrefix + "Starting server...");
		
		thread = new Thread(this, Game.TITLE + " [SERVER]");
		thread.start();
		
		running = true;
		
		System.out.println(new Date() + " " + infoPrefix + "Server startup complete!");
	}
	
	/**
	 * Stops execution of the server.
	 */
	public synchronized void stop() {
		System.out.println(new Date() + " " + infoPrefix + "Stopping server...");
		
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			Packet01Disconnect packet = new Packet01Disconnect(player.getUsername());
			packet.writeData(this);
		}
		
		handler.getEntities().clear();
		
		running = false;
	}

	// Runs the server loop.
	public void run() {
		while (running) {
			byte[] data = new byte[1024];
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
		
		try {
			socket.close();
			
			System.out.println(new Date() + " " + infoPrefix + "Server shutdown complete!");
			
			thread.join();
		} catch (InterruptedException exception) {
			System.err.print(new Date() + " " + errorPrefix +
					"An exception occured whilst stopping the server - ");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Analyses the received packet data and manages it accordingly.
	 * @param data - The byte array of data received from a client.
	 * @param address - The source IP address of the packet.
	 * @param port - The source port of the packet.
	 */
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		
		if (message.length() > 4 && message.substring(0, 4).equalsIgnoreCase("Ping")) {
			System.out.println(new Date() + " " + infoPrefix + "Ping recieved from CLIENT [" +
					address.getHostAddress() + ":" + port + "].");
			
			String[] dataArray = message.substring(4).split(",");
			
			for (int i = 0; i < getConnectedPlayers().size(); i++) {
				IPlayer player = getConnectedPlayers().get(i);
				
				if (player.getUsername().equalsIgnoreCase(dataArray[0])) {
					sendData(("Pong" + 0 + ",Username already taken").getBytes(), address, port);
					
					return;
				}
			}
			
			if (Integer.parseInt(dataArray[1]) == 0) {
				for (int i = 0, players = 0; i < handler.getEntities().size(); i++) {
					Entity tempObject = handler.getEntities().get(i);
					
					if (tempObject.getID() == ID.IPLAYER) players++;
					
					if (tempObject.equals(handler.getEntities().getLast())) {
						if (!(players < 4)) {
							sendData(("Pong" + 0 + ",4 players reached").getBytes(), address,
									port);
							
							return;
						}
					}
				}
			}
			
			sendData(("Pong" + 1).getBytes(), address, port);
		} else {
			Packet packet;
			PacketType type = Packet.lookupPacket(message.substring(0, 2));
			
			switch (type) {
			default:
				break;
			case INVALID:
				System.err.println(new Date() + " " + errorPrefix +
						"Invalid packet received from [" + address.getHostAddress() + ":" +
						port + "]: " + message);
				
				break;
			case CONNECT:
				packet = new Packet00Connect(data);
				
				System.out.println(new Date() + " " + infoPrefix +
						((Packet00Connect) packet).getUsername() + " [" +
						address.getHostAddress() + ":" + port + "] is connecting...");
				
				IPlayer player = new IPlayer(((Packet00Connect) packet).getX(),
						((Packet00Connect) packet).getY(),
						((Packet00Connect) packet).getUsername(),
						((Packet00Connect) packet).getFillColour(),
						((Packet00Connect) packet).getOutlineColour(),
						((Packet00Connect) packet).getUsernameColour(), address, port);
				
				player.health = ((Packet00Connect) packet).getHealth();
				player.spectator = ((Packet00Connect) packet).isSpectator();
				
				addConnection(player, (Packet00Connect) packet);
				
				break;
			case DISCONNECT:
				packet = new Packet01Disconnect(data);
				
				System.out.println(new Date() + " " + infoPrefix +
						((Packet01Disconnect) packet).getUsername() + " [" +
						address.getHostAddress() + ":" + port + "] is disconnecting...");
				
				removeConnection((Packet01Disconnect) packet);
				
				break;
			case PLAYERMOVE:
				packet = new Packet02PlayerMove(data);
				
				movePlayer((Packet02PlayerMove) packet);
				
				break;
			case PLAYERSHOT:
				packet = new Packet03PlayerShot(data);
				
				shootPaintball((Packet03PlayerShot) packet);
				
				break;
			case DAMAGE:
				packet = new Packet04Damage(data);
				
				hurtPlayer((Packet04Damage) packet);
				
				break;
			case PLAYERDEATH:
				packet = new Packet05PlayerDeath(data);
				
				killPlayer((Packet05PlayerDeath) packet);
				
				break;
			case LEVELUP:
				System.err.println(new Date() + " " + errorPrefix + "A " + type.name() +
						" packet has been received from [" + address.getHostAddress() + ":" +
						port + "]: " + message);
				
				break;
			case SPAWN:
				System.err.println(new Date() + " " + errorPrefix + "A " + type.name() +
						" packet has been received from [" + address.getHostAddress() + ":" +
						port + "]: " + message);
				
				break;
			case ENEMYSHOT:
				System.err.println(new Date() + " " + errorPrefix + "A " + type.name() +
						" packet has been received from [" + address.getHostAddress() + ":" +
						port + "]: " + message);
				
				break;
			case TARGETCHANGE:
				System.err.println(new Date() + " " + errorPrefix + "A " + type.name() +
						" packet has been received from [" + address.getHostAddress() + ":" +
						port + "]: " + message);
				
				break;
			}
		}
	}

	/**
	 * Adds a newly connected player, or respawns a player who is already connected, assuming
	 * they are respawning after dying.
	 * @param player - The new player connection to add.
	 * @param packet - The connect packet associated with the connecting player.
	 */
	private void addConnection(IPlayer player, Packet00Connect packet) {
		boolean alreadyConnected = false;
		
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer connectedPlayer = getConnectedPlayers().get(i);
			
			if (connectedPlayer.getUsername().equalsIgnoreCase(player.getUsername())) {
				connectedPlayer.health = player.health;
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), connectedPlayer.getIPAddress(),
						connectedPlayer.getPort());
				Packet00Connect packet2 = new Packet00Connect(connectedPlayer.getUsername(),
						connectedPlayer.getX(), connectedPlayer.getY(),
						connectedPlayer.getFillColour(), connectedPlayer.getOutlineColour(),
						connectedPlayer.getUsernameColour(), connectedPlayer.health, true,
						connectedPlayer.spectator);
				
				sendData(packet2.getData(), player.getIPAddress(), player.getPort());
			}
		}
		
		if (alreadyConnected) {
			System.out.println(new Date() + " " + warnPrefix + player.getUsername() + " [" +
					player.getIPAddress().getHostAddress() + ":" + player.getPort() +
					"] is already connected; assuming that a previously dead player has "
					+ "respawned...");
		} else {
			getConnectedPlayers().add(player);
			
			System.out.println(new Date() + " " + infoPrefix + player.getUsername() + " [" +
					player.getIPAddress().getHostAddress() + ":" + player.getPort() +
					"] has connected.");
		}
		
		if (!player.spectator) handler.addEntity(player);
		
		Packet06LevelUp levelUpPacket = new Packet06LevelUp(spawner.level);
		sendData(levelUpPacket.getData(), player.getIPAddress(), player.getPort());
		
		LinkedList<IEnemy> enemyList = new LinkedList<IEnemy>();
		
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity tempObject = handler.getEntities().get(i);
			
			if (tempObject.getID() == ID.IENEMY || tempObject.getID() == ID.IMOVINGENEMY ||
					tempObject.getID() == ID.IBOUNCYENEMY || tempObject.getID() ==
					ID.IHOMINGENEMY) enemyList.add((IEnemy) tempObject);
		}
		
		if (enemyList.size() > 0) {
			float[] xCoords = new float[enemyList.size()], yCoords = new float[enemyList.size()];
			ID[] ids = new ID[enemyList.size()];
			int[] enemyNumbers = new int[enemyList.size()];
			long[] attackTimers = new long[enemyList.size()],
					shootTimers = new long[enemyList.size()];
			String[] targets = new String[enemyList.size()];
			
			for (int i = 0; i < enemyList.size(); i++) {
				IEnemy enemy = enemyList.get(i);

				ids[i] = enemy.getID();
				xCoords[i] = enemy.getX();
				yCoords[i] = enemy.getY();
				enemyNumbers[i] = enemy.getEnemyNumber();
				attackTimers[i] = enemy.attackTimer;
				shootTimers[i] = enemy.shootTimer;
				targets[i] = enemy.getTarget().getUsername();
			}
			
			Packet07Spawn spawnPacket = new Packet07Spawn(enemyList.size(), ids, xCoords,
					yCoords, enemyNumbers, attackTimers, shootTimers, targets);
			sendData(spawnPacket.getData(), player.getIPAddress(), player.getPort());
		}
		
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity tempObject = handler.getEntities().get(i);
			
			if (tempObject.getID() == ID.PAINTBALL) {
				Paintball paintball = (Paintball) tempObject;
				
				if (paintball.getShooter().getID() == ID.IPLAYER) {
					IPlayer shooter = (IPlayer) paintball.getShooter();
					
					Packet03PlayerShot playerShotPacket =
							new Packet03PlayerShot(shooter.getUsername(), paintball.getX(),
									paintball.getY(), paintball.getVelX(), paintball.getVelY());
					sendData(playerShotPacket.getData(), player.getIPAddress(),
							player.getPort());
				} else if (paintball.getShooter().getID() == ID.IENEMY ||
						paintball.getShooter().getID() == ID.IMOVINGENEMY ||
						paintball.getShooter().getID() == ID.IBOUNCYENEMY ||
						paintball.getShooter().getID() == ID.IHOMINGENEMY) {
					IEnemy enemy = (IEnemy) paintball.getShooter();
					
					Packet08EnemyShot enemyShotPacket =
							new Packet08EnemyShot(enemy.getEnemyNumber(),
									enemy.getTarget().getUsername(), paintball.getID(),
									paintball.getX(), paintball.getY(), paintball.getVelX(),
									paintball.getVelY());
					
					sendData(enemyShotPacket.getData(), player.getIPAddress(), player.getPort());
				}
			}
		}
	}

	/**
	 * Removes a disconnecting player.
	 * @param packet - The disconnect packet associated with the disconnecting player.
	 */
	private void removeConnection(Packet01Disconnect packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				packet.writeData(this);
				
				getConnectedPlayers().remove(player);
				
				if (!player.spectator) handler.removeEntity(player);
				
				System.out.println(new Date() + " " + infoPrefix + player.getUsername() + " [" +
						player.getIPAddress().getHostAddress() + ":" + player.getPort() +
						"] has disconnected.");
				
				break;
			}
		}
	}

	/**
	 * Either sets a moving player's velocity, or sets a player's stationary coordinates.
	 * @param packet - The move packet associated with the moving / stationary player.
	 */
	private void movePlayer(Packet02PlayerMove packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				if (packet.isPlayerMoving()) {
					player.setVelX(packet.getVarX());
					player.setVelY(packet.getVarY());
					
					System.out.println(new Date() + " " + infoPrefix + player.getUsername() +
							" [" + player.getIPAddress().getHostAddress() + ":" +
							player.getPort() + "] is moving by (" + packet.getVarX() + "," +
							packet.getVarY() + ").");
				} else {
					player.setVelX(0);
					player.setVelY(0);
					player.setX(packet.getVarX());
					player.setY(packet.getVarY());
					
					System.out.println(new Date() + " " + infoPrefix + player.getUsername() +
							" [" + player.getIPAddress().getHostAddress() + ":" +
							player.getPort() + "] has stopped at (" + packet.getVarX() + "," +
							packet.getVarY() + ").");
				}
				
				break;
			}
		}
		
		packet.writeData(this);
	}

	/**
	 * Creates a paintball shot by a player.
	 * @param packet - The shot packet associated with the player shooting.
	 */
	private void shootPaintball(Packet03PlayerShot packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				IPaintball paintball = new IPaintball(ID.PAINTBALL, packet.getX(), packet.getY(),
						handler, player);
				
				handler.addEntity(paintball);
				
				paintball.setVelX(packet.getVelX());
				paintball.setVelY(packet.getVelY());
				
				System.out.println(new Date() + " " + infoPrefix + player.getUsername() + " [" +
						player.getIPAddress().getHostAddress() + ":" + player.getPort() +
						"] has shot a paintball from (" + packet.getX() + "," + packet.getY() +
						"), moving by (" + packet.getVelX() + "," + packet.getVelY() + ").");
				
				break;
			}
		}
		
		packet.writeData(this);
	}

	/**
	 * Decreases a player's health based on the amount of damage they have taken.
	 * @param packet - The damage packet associated with the damaged player.
	 */
	private void hurtPlayer(Packet04Damage packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				player.health -= packet.getDamageTaken();
				
				System.out.println(new Date() + " " + infoPrefix + player.getUsername() + " [" +
						player.getIPAddress().getHostAddress() + ":" + player.getPort() +
						"] has taken " + packet.getDamageTaken() + " damage.");
				
				break;
			}
		}
		
		packet.writeData(this);
	}

	/**
	 * Removes a dead player from the object emulation.
	 * @param packet - The death packet associated with the dead player.
	 */
	private void killPlayer(Packet05PlayerDeath packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				handler.removeEntity(player);
				
				System.out.println(new Date() + " " + infoPrefix + packet.getUsername() + " [" +
						player.getIPAddress().getHostAddress() + ":" + player.getPort() +
						"] has died.");
				
				break;
			}
		}
		
		packet.writeData(this);
	}
	
	/**
	 * Updates the logic of the server.
	 */
	public void tick() {
		handler.tick();
		
		if (gameMode == Mode.TEAMSURVIVAL) spawner.tick();
	}

	/**
	 * Sends a packet of data to a client.
	 * @param data - The packet of data to send to the client.
	 * @param ipAddress - The target IP address of the client.
	 * @param port - The target port of the client.
	 */
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		
		try {
			socket.send(packet);
		} catch (IOException exception) {
			System.err.print(new Date() + " " + errorPrefix +
					"An exception occured whilst trying to send a packet - ");
			exception.printStackTrace();
		}
	}

	/**
	 * Sends a packet of data to all clients connected.
	 * @param data - The packet of data to send to the clients.
	 */
	public void sendDataToAll(byte[] data) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			sendData(data, player.getIPAddress(), player.getPort());
		}
	}
	
	/**
	 * Gets the list of connected players.
	 * @return The list of connected players.
	 */
	public synchronized LinkedList<IPlayer> getConnectedPlayers() {
		return connectedPlayers;
	}
}
