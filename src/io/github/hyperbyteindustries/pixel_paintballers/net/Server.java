package io.github.hyperbyteindustries.pixel_paintballers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet.PacketType;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet00Connect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet02Move;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet03Shot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet04Damage;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet05Death;

/**
 * Represents the game server of the multiplayer system.
 * When the server is initialised, this class is responsible for the management of all data sent
 * from connected clients, relaying the data back to all other clients.
 * @author Ramone Graham
 *
 */
public class Server implements Runnable {

	private Thread thread;
	private boolean running = false;
	
	private DatagramSocket socket;
	
	private String infoPrefix = "[Server INFO]: ", warnPrefix = "[Server WARN]: ",
			errorPrefix = "[Server ERROR]: ";
	
	private LinkedList<IPlayer> connectedPlayers = new LinkedList<IPlayer>();
	private LinkedList<GameObject> onlineObjects = new LinkedList<GameObject>();
	
	/**
	 * Creates a new server.
	 */
	public Server() {
		try {
			socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts execution of the server.
	 */
	public synchronized void start() {
		thread = new Thread(this, Game.TITLE + " [SERVER]");
		thread.start();
		running = true;
	}
	
	/**
	 * Stops execution of the server.
	 */
	public synchronized void stop() {
		System.out.println(infoPrefix + "Stopping server...");
		
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			Packet01Disconnect packet = new Packet01Disconnect(player.getUsername());
			packet.writeData(this);
			
			removeObject(player);
		}
		
		running = false;
	}

	// Runs the server loop.
	public void run() {
		while (running) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
		
		try {
			socket.close();
			
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		
		if (message.substring(0, 4).equalsIgnoreCase("Ping")) {
			System.out.println(infoPrefix + "Ping recieved from CLIENT [" +
					address.getHostAddress() + ":" + port + "].");
			
			for (int i = 0, players = 0; i < getConnectedPlayers().size(); i++) {
				IPlayer player = getConnectedPlayers().get(i);
				
				players++;
				
				if (player.equals(getConnectedPlayers().getLast())) {
					if (!(players < 4)) {
						sendData(("Pong" + 0 + ",4 players reached").getBytes(), address, port);
						return;
					}
				}
			}
			
			for (int i = 0; i < getConnectedPlayers().size(); i++) {
				IPlayer player = getConnectedPlayers().get(i);
				
				if (player.getUsername().equalsIgnoreCase(message.substring(4))) {
					sendData(("Pong" + 0 + ",Username already taken").getBytes(), address, port);
					return;
				}
			}
			
			sendData(("Pong" + 1).getBytes(), address, port);
		} else {
			PacketType type = Packet.lookupPacket(message.substring(0, 2));
			Packet packet;
			
			switch (type) {
			default:
				break;
			case INVALID:
				System.out.println(errorPrefix + "Invalid packet recieved: " + message);
				break;
			case CONNECT:
				packet = new Packet00Connect(data);
				
				System.out.println(infoPrefix + ((Packet00Connect) packet).getUsername() + " [" +
						address.getHostAddress() + ":" + port + "] is connecting...");
				
				IPlayer player = new IPlayer(((Packet00Connect) packet).getX(),
						((Packet00Connect) packet).getY(), ID.IPLAYER,
						((Packet00Connect) packet).getUsername(), address, port);
				
				player.health = ((Packet00Connect) packet).getHealth();
				
				addConnection(player, (Packet00Connect) packet);
				break;
			case DISCONNECT:
				packet = new Packet01Disconnect(data);
				
				System.out.println(infoPrefix + ((Packet01Disconnect) packet).getUsername() +
						" [" + address.getHostAddress() + ":" + port + "] is disconnecting...");
				
				removeConnection((Packet01Disconnect) packet);
				break;
			case MOVE:
				packet = new Packet02Move(data);
				
				movePlayer((Packet02Move) packet);
				break;
			case SHOT:
				packet = new Packet03Shot(data);
				
				shootPaintball((Packet03Shot) packet);
				break;
			case DAMAGE:
				packet = new Packet04Damage(data);
				
				hurtPlayer((Packet04Damage) packet);
				break;
			case DEATH:
				packet = new Packet05Death(data);
				
				killPlayer((Packet05Death) packet);
				break;
			}
		}
	}

	/**
	 * Adds a newly connected player, or rejects them if they have a duplicate username.
	 * @param player - The new player connection to add.
	 * @param packet - The connect packet associated with the connecting player.
	 */
	private void addConnection(IPlayer player, Packet00Connect packet) {
		boolean alreadyConnected = false;
		
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer connectedPlayer = getConnectedPlayers().get(i);
			
			if (connectedPlayer.getUsername().equalsIgnoreCase(player.getUsername()))
				alreadyConnected = true;
			else {
				sendData(packet.getData(), connectedPlayer.getIPAddress(),
						connectedPlayer.getPort());
				Packet00Connect packet2 = new Packet00Connect(connectedPlayer.getUsername(),
						connectedPlayer.getX(), connectedPlayer.getY(), connectedPlayer.health,
						true);
				sendData(packet2.getData(), player.getIPAddress(), player.getPort());
			}
		}
		
		if (alreadyConnected) {
			System.out.println(warnPrefix + player.getUsername() + " [" +
					player.getIPAddress().getHostAddress() + ":" + player.getPort() +
					"] is already connected.");
		} else {
			getConnectedPlayers().add(player);
			
			System.out.println(infoPrefix + player.getUsername() + " [" +
					player.getIPAddress().getHostAddress() + ":" + player.getPort() +
					"] has connected.");
		}
		
		addObject(player);
		
		for (int i = 0; i < getOnlineObjects().size(); i++) {
			GameObject tempObject = getOnlineObjects().get(i);
			
			if (tempObject.getID() == ID.PAINTBALL) {
				IPaintball paintball = (IPaintball) tempObject;
				
				
				if (paintball.getShooter().getID() == ID.IPLAYER) {
					IPlayer shooter = (IPlayer) paintball.getShooter();
					
					Packet03Shot shotPacket = new Packet03Shot(shooter.getUsername(),
							paintball.getX(), paintball.getY(), paintball.getVelX(),
							paintball.getVelY());
					sendData(shotPacket.getData(), player.getIPAddress(), player.getPort());
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
				getConnectedPlayers().remove(player);
				removeObject(player);
				
				System.out.println(infoPrefix + player.getUsername() + " [" +
						player.getIPAddress().getHostAddress() + ":" + player.getPort() +
						"] has disconnected.");
				
				break;
			}
		}
		
		packet.writeData(this);
	}

	/**
	 * Sets a moving player's new velocity.
	 * @param packet - The move packet associated with the moving player.
	 */
	private void movePlayer(Packet02Move packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				if (packet.playerMoving()) {
					player.setVelX(packet.getVarX());
					player.setVelY(packet.getVarY());
					
					System.out.println(infoPrefix + player.getUsername() + " [" +
							player.getIPAddress().getHostAddress() + ":" + player.getPort() +
							"] is moving by (" + packet.getVarX() + "," + packet.getVarY() +
							").");
				} else {
					player.setVelX(0);
					player.setVelY(0);
					player.setX(packet.getVarX());
					player.setY(packet.getVarY());
					
					System.out.println(infoPrefix + player.getUsername() + " [" +
							player.getIPAddress().getHostAddress() + ":" + player.getPort() +
							"] has stopped at (" + packet.getVarX() + "," + packet.getVarY() +
							").");
				}
				
				break;
			}
		}
		
		packet.writeData(this);
	}

	/**
	 * Creates a new paintball that has been shot by a player.
	 * @param packet - The shot packet associated with the shooting player.
	 */
	private void shootPaintball(Packet03Shot packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				IPaintball paintball = new IPaintball(player.getX()+12, player.getY()+12,
						ID.PAINTBALL, player, this);
				
				addObject(paintball);
				
				paintball.setVelX(packet.getVelX());
				paintball.setVelY(packet.getVelY());
				
				System.out.println(infoPrefix + player.getUsername() + " [" +
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
				
				System.out.println(infoPrefix + player.getUsername() + " [" +
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
	private void killPlayer(Packet05Death packet) {
		for (int i = 0; i < getConnectedPlayers().size(); i++) {
			IPlayer player = getConnectedPlayers().get(i);
			
			if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				removeObject(player);
				
				System.out.println(infoPrefix + packet.getUsername() + " [" +
						player.getIPAddress().getHostAddress() + ":" + player.getPort() +
						"] has died.");
				break;
			}
		}
		
		packet.writeData(this);
	}
	
	/**
	 * Updates the logic of the objects.
	 */
	public void tick() {
		for (int i = 0; i < getOnlineObjects().size(); i++) {
			GameObject tempObject = getOnlineObjects().get(i);
			
			tempObject.tick();
		}
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
		} catch (IOException e) {
			e.printStackTrace();
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
	 * Adds a game object to the objects list.
	 * @param object - The object to be added to the list.
	 */
	public void addObject(GameObject object) {
		getOnlineObjects().add(object);
	}
	
	/**
	 * Removes a game object from the objects list.
	 * @param object - The object to be removed from the list.
	 */
	public void removeObject(GameObject object) {
		getOnlineObjects().remove(object);
	}
	
	/**
	 * Returns the list of connected players, optimised to prevent concurrent modification
	 * exceptions.
	 * @return The list of connected players.
	 */
	public synchronized LinkedList<IPlayer> getConnectedPlayers() {
		return connectedPlayers;
	}
	
	/**
	 * Returns the list of online objects, optimised to prevent concurrent modification
	 * exceptions.
	 * @return The list of online objects.
	 */
	public synchronized LinkedList<GameObject> getOnlineObjects() {
		return onlineObjects;
	}
}
