package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

import io.github.hyperbyteindustries.pixel_paintballers.net.Client;
import io.github.hyperbyteindustries.pixel_paintballers.net.Server;

/**
 * Represents the "skeleton" of all packets in the multiplayer system.
 * When a packet class is created (E.g. Packet00Connect), it will extend this class, inheriting
 * it's methods and variables. 
 * @author Ramone Graham
 *
 */
public abstract class Packet {

	/**
	 * Represents the type tag of a packet.
	 * When a packet class is created, (E.g. Packet00Connect), it will get a type tag in order to
	 * inherit certain behaviours.
	 * @author Ramone Graham
	 *
	 */
	public enum PacketType {
		INVALID("-1"), CONNECT("00"), DISCONNECT("01"), PLAYERMOVE("02"), PLAYERSHOT("03"),
		DAMAGE("04"), PLAYERDEATH("05"), LEVELUP("06"), SPAWN("07"), ENEMYSHOT("08"),
		TARGETCHANGE("09");
		
		private String packetID;
		
		/**
		 * Creates a new packet type.
		 * @param packetID - The ID code of packet type.
		 */
		private PacketType(String packetID) {
			this.packetID = packetID;
		}
		
		/**
		 * Gets the packet ID code of a packet type.
		 * @return The packet ID code.
		 */
		public String getPacketID() {
			return packetID;
		}
	}
	
	protected String packetID;
	
	/**
	 * Creates a new packet.
	 * @param packetID - The packet type ID code for the packet.
	 */
	public Packet(String packetID) {
		this.packetID = packetID;
	}
	
	/**
	 * Compiles the packet data into a byte array.
	 * @return The byte array of data that is sent between the server and clients.
	 */
	public abstract byte[] getData();
	
	/**
	 * Writes the packet data to the server.
	 * @param client - The client sending the data to the server.
	 */
	public void writeData(Client client) {
		client.sendData(getData());
	}
	
	/**
	 * Writes the data to all connected clients.
	 * @param server - The server sending the data to all clients.
	 */
	public void writeData(Server server) {
		server.sendDataToAll(getData());
	}
	
	/**
	 * Gets the packet type.
	 * @param packetID - The ID code of the desired packet type.
	 * @return The packet type if found, else <code>INVALID</code>.
	 */
	public static PacketType lookupPacket(String packetID) {
		for (PacketType type : PacketType.values()) {
			if (type.getPacketID().equals(packetID)) return type;
		}
		
		return PacketType.INVALID;
	}
	
	/**
	 * Compiles the byte array of data into a readable string.
	 * @param data - The byte array of data to compile.
	 * @return The String of data, omitting the packetID code.
	 */
	public String readData(byte[] data) {
		return new String(data).trim().substring(2);
	}
	
	/**
	 * Gets the packet type ID code of the packet.
	 * @return The packet type ID code.
	 */
	public String getPacketID() {
		return packetID;
	}
}
