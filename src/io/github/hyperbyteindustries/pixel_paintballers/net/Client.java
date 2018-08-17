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
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Paintball;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet.PacketType;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet00Connect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet01Disconnect;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet02Move;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet03Shot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet04Damage;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet05Death;

/**
 * Represents the game clent of the multiplayer system.
 * When the game is initialised, this class is responsible for the management of data sent from
 * the server, manipulating the game in correlation with the server.
 * @author Ramone Graham
 *
 */
public class Client implements Runnable {

	private Thread thread;
	private boolean running = false;

	private Game game;
	private Handler handler;
	
	public InetAddress ipAddress;
	
	private DatagramSocket socket;
	
	private String infoPrefix = "[Client INFO]: ", errorPrefix = "[Client ERROR]: ";
	
	/**
	 * Creates a new client.
	 * @param game - An instance of the game class, used to create online players.
	 * @param handler - An instance of the handler class, used to add and remove objects to the
	 * game.
	 * @param ipAddress - The target IP address of the server.
	 */
	public Client(Game game, Handler handler, String ipAddress) {
		try {
			this.game = game;
			this.handler = handler;
			this.ipAddress = InetAddress.getByName(ipAddress);
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
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
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
						Game.player.getX(), Game.player.getY(), Game.player.health, false);
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
				System.out.println(errorPrefix + "Invalid packet recieved: " + message);
				break;
			case CONNECT:
				packet = new Packet00Connect(data);
				
				IPlayer player = new IPlayer(((Packet00Connect) packet).getX(),
						((Packet00Connect) packet).getY(), ID.IPLAYER,
						((Packet00Connect) packet).getUsername(), address, port);
				
				player.health = 100;
				
				handleConnect(player, (Packet00Connect) packet);
				break;
			case DISCONNECT:
				packet = new Packet01Disconnect(data);
				
				handleDisconnect((Packet01Disconnect) packet);
				break;
			case MOVE:
				packet = new Packet02Move(data);
				
				handleMove((Packet02Move) packet);
				break;
			case SHOT:
				packet = new Packet03Shot(data);
				
				handleShot((Packet03Shot) packet);
				break;
			case DAMAGE:
				packet = new Packet04Damage(data);
				
				handleDamageTaken((Packet04Damage) packet);
				break;
			case DEATH:
				packet = new Packet05Death(data);
				
				handleDeath((Packet05Death) packet);
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
		player.health = packet.getHealth();
		
		handler.addObject(player);
		
		if (!(packet.isAlreadyConnected()))
			System.out.println(infoPrefix + player.getUsername() + " has joined the game.");
	}

	/**
	 * Removes a disconnected player from the game handler.
	 * @param packet - The disconnect packet associated with the player disconnecting.
	 */
	private void handleDisconnect(Packet01Disconnect packet) {
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (Game.player.getUsername().equalsIgnoreCase(packet.getUsername())) {
				Game.paused = false;
				handler.getObjects().clear();
				Game.gameState = State.TITLESCREEN;
				
				break;
			} else if (tempObject.getID() == ID.IPLAYER) {
				IPlayer player = (IPlayer) tempObject;
				
				if (player.getUsername().equalsIgnoreCase(packet.getUsername())) {
					handler.removeObject(player);
					
					System.out.println(infoPrefix + player.getUsername() +
							" has left the game.");
					
					break;
				}
			}
		}
	}
	
	/**
	 * Sets a moving player's velocity.
	 * @param packet - The move packet associated with the player moving.
	 */
	private void handleMove(Packet02Move packet) {
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
	private void handleShot(Packet03Shot packet) {
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
	 * @param packet - The damage packet associated with the player hit.
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
	private void handleDeath(Packet05Death packet) {
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
	 * Sends a packet of data to the server.
	 * @param data - The packet of data to send to the server.
	 */
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
