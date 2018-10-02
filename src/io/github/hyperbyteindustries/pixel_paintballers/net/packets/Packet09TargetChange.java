package io.github.hyperbyteindustries.pixel_paintballers.net.packets;

public class Packet09TargetChange extends Packet {

	private int enemyNumber;
	private String target;
	
	public Packet09TargetChange(int enemyNumber, String target) {
		super("09");
		
		this.enemyNumber = enemyNumber;
		this.target = target;
	}
	
	public Packet09TargetChange(byte[] data) {
		super("09");
		
		String[] dataArray = readData(data).split(",");
		
		enemyNumber = Integer.parseInt(dataArray[0]);
		target = dataArray[1];
	}

	public byte[] getData() {
		return (packetID + enemyNumber + "," + target).getBytes();
	}
	
	public int getEnemyNumber() {
		return enemyNumber;
	}
	
	public String getTarget() {
		return target;
	}
}
