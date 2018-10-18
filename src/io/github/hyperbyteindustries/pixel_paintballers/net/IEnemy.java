package io.github.hyperbyteindustries.pixel_paintballers.net;

import static java.awt.Color.WHITE;

import java.awt.Graphics2D;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Enemy;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Player;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet08EnemyMove;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet09EnemyShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet10TargetChange;

/**
 * Represents an enemy in the game's multiplayer system.
 * When constructed, this class is responsible for the online management of the enemy.
 * @author Ramone Graham
 *
 */
public class IEnemy extends Enemy {

	private Server server;
	
	private int enemyNumber;
	
	/**
	 * Creates a new enemy
	 * @param x - The x coordinate of the enemy.
	 * @param y - The y coordinate of the enemy.
	 * @param id - The ID tag of the enemy.
	 * @param handler - An instance of the Handler class, used to create paintballs.
	 * @param server - An instance of the Server class, used to write packets to clients
	 * @param enemyNumber - The enemy's number, used to identify an enemy.
	 */
	public IEnemy(float x, float y, ID id, Handler handler, Server server, int enemyNumber) {
		super(x, y, id, null, handler);
		
		this.server = server;
		this.enemyNumber = enemyNumber;
	}
	
	// See tick() in GameObject.
	public void tick() {
		float prevX = x, prevY = y;
		
		x += velX;
		y += velY;
		
		if (!(server == null)) {
			if (!(prevX == x) && !(prevY == y)) {
				Packet08EnemyMove movePacket = new Packet08EnemyMove(enemyNumber, x, y);
				movePacket.writeData(server);
			}
		}
		
		boolean targetConnected = false;
		
		for (int i = 0; i < handler.getObjects().size(); i++) {
			GameObject tempObject = handler.getObjects().get(i);
			
			if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER) {
				Player player = (Player) tempObject;
				
				if (player.getUsername().equals(target.getUsername())) {
					targetConnected = true;
					
					break;
				}
			}
		}
		
		if (!(targetConnected) && !(server == null)) {
			LinkedList<Player> targets = new LinkedList<Player>();
			
			for (int i = 0; i < handler.getObjects().size(); i++) {
				GameObject tempObject = handler.getObjects().get(i);
				
				if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER) {
					targets.add((Player) tempObject);
				}
			}
			
			target = targets.get(random.nextInt(targets.size()));
			
			Packet10TargetChange packet = new Packet10TargetChange(enemyNumber,
					target.getUsername());
			packet.writeData(server);
		 }
		
		if (id == ID.IMOVINGENEMY) {
			if (!(server == null)) {
				float diffX = x-(target.getX()+4), diffY = y-(target.getY()+4),
						distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
								(y-target.getY())*(y-target.getY()));
				
				velX = (float) ((-1.0/distance)*diffX);
				velY = (float) ((-1.0/distance)*diffY);
			}
			
			if (getBounds().intersects(target.getBounds())) {
				if (attackTime == 100) {
					target.health -= 2;
					attackTime--;
				} else if (attackTime == 0) attackTime = 100;
				else attackTime--;
			}
		}
		
		if (shootTime == 0) {
			if (!(server == null)) {
				IPaintball paintball = null;
				
				if (id == ID.IENEMY || id == ID.IMOVINGENEMY) paintball = new IPaintball(x+8,
						y+8, ID.PAINTBALL, handler, this);
				else if (id == ID.IBOUNCYENEMY) paintball = new IPaintball(x+8, y+8,
						ID.BOUNCYPAINTBALL, handler, this);
				else if (id == ID.IHOMINGENEMY) paintball = new IPaintball(x+8, y+8,
						ID.HOMINGPAINTBALL, handler, this);
				
				handler.addObject(paintball);
				
				float diffX = x-target.getX(), diffY = y-target.getY(),
						distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
								(y-target.getY())*(y-target.getY()));
				
				paintball.setVelX((float) (((-1.0/distance)*diffX)*7));
				paintball.setVelY((float) (((-1.0/distance)*diffY)*7));
				
				Packet09EnemyShot packet = new Packet09EnemyShot(enemyNumber,
						target.getUsername(), paintball.getID(), paintball.getX(),
						paintball.getY(), paintball.getVelX(), paintball.getVelY());
				packet.writeData(server);
			}
			
			shootTime = 420;
		} else shootTime--;
	}
	
	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		super.render(graphics2d);
		
		String targetMessage = "Target: " + target.getUsername();
		
		graphics2d.setColor(WHITE);
		graphics2d.drawString(targetMessage,
				(float) (x-((targetMessage.length()-1)/2*7.5)), y-5);
	}
	
	/**
	 * Sets the enemy number to the given value.
	 * @param enemyNumber - The number to be set.
	 */
	public void setEnemyNumber(int enemyNumber) {
		this.enemyNumber = enemyNumber;
	}
	
	/**
	 * Gets the enemy number of the enemy.
	 * @return The enemy number.
	 */
	public int getEnemyNumber() {
		return enemyNumber;
	}
}
