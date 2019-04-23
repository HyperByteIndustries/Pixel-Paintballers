package io.github.hyperbyteindustries.pixel_paintballers.net;

import static java.awt.Color.WHITE;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.entities.Enemy;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Entity;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.entities.Player;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet08EnemyShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet09TargetChange;

/**
 * Represents an enemy in the game's multiplayer system.
 * When constructed, this class is responsible for the online management of the enemy.
 * @author Ramone Graham
 *
 */
public class IEnemy extends Enemy {

	private final Server server;
	
	private int enemyNumber;
	
	/**
	 * Creates a new enemy
	 * @param id - The identification tag of the object.
	 * @param x - The X coordinate of the enemy.
	 * @param y - The Y coordinate of the enemy.
	 * @param handler - An instance of the Handler class, used to create paintballs.
	 * @param server - An instance of the Server class, used to write packets to clients
	 * @param enemyNumber - The enemy's number, used to identify an enemy.
	 */
	public IEnemy(ID id, float x, float y, Handler handler, Server server, int enemyNumber) {
		super(id, x, y, null, handler);
		
		this.server = server;
		this.enemyNumber = enemyNumber;
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.entities.Enemy#tick()
	 */
	/**
	 * {@inheritDoc}
	 */
	public void tick() {
		x += velX;
		y += velY;
		
		boolean targetConnected = false;
		
		for (int i = 0; i < handler.getEntities().size(); i++) {
			Entity tempObject = handler.getEntities().get(i);
			
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
			
			for (int i = 0; i < handler.getEntities().size(); i++) {
				Entity tempObject = handler.getEntities().get(i);
				
				if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER)
					targets.add((Player) tempObject);
			}
			
			target = targets.get(random.nextInt(targets.size()));
			
			Packet09TargetChange packet = new Packet09TargetChange(enemyNumber,
					target.getUsername());
			packet.writeData(server);
		 }
		
		if (id == ID.IMOVINGENEMY) {
			float diffX = x - target.getX() - 4, diffY = y - target.getY() - 4,
					distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
			
			velX = -1/distance*diffX;
			velY = -1/distance*diffY;
			
			if (getBounds().intersects(target.getBounds())) {
				if (System.currentTimeMillis() - attackTimer >= 1500) {
					attackTimer = System.currentTimeMillis();
					
					if (target.getID() == ID.PLAYER) target.health -= 2;
				}
			}
		}
		
		if (System.currentTimeMillis()-shootTimer >= shootTime*1000) {
			shootTimer = System.currentTimeMillis();
			
			if (!(server == null)) {
				IPaintball paintball = null;
				
				if (id == ID.IENEMY || id == ID.IMOVINGENEMY)
					paintball = new IPaintball(ID.PAINTBALL, x+8,y+8, handler, this);
				else if (id == ID.IBOUNCYENEMY)
					paintball = new IPaintball(ID.BOUNCYPAINTBALL, x+8, y+8, handler, this);
				else if (id == ID.IHOMINGENEMY)
					paintball = new IPaintball(ID.HOMINGPAINTBALL, x+8, y+8, handler, this);
				
				handler.addEntity(paintball);
				
				float diffX = paintball.getX() - target.getX() - 12,
						diffY = paintball.getY() - target.getY() - 12,
						distance = (float) Math.sqrt(diffX*diffX+diffY*diffY);
				
				paintball.setVelX(-1/distance*diffX);
				paintball.setVelY(-1/distance*diffY);
				
				Packet08EnemyShot packet = new Packet08EnemyShot(enemyNumber,
						target.getUsername(), paintball.getID(), paintball.getX(),
						paintball.getY(), paintball.getVelX(), paintball.getVelY());
				packet.writeData(server);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.github.hyperbyteindustries.pixel_paintballers.entities.Enemy#
	 * render(java.awt.Graphics2D)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void render(Graphics2D graphics2D) {
		super.render(graphics2D);
		
		graphics2D.setColor(WHITE);
		
		String targetMessage = "Target: " + target.getUsername();
		FontMetrics metrics = graphics2D.getFontMetrics();
		Rectangle2D stringBounds = metrics.getStringBounds(targetMessage, graphics2D);
		
		graphics2D.drawString(targetMessage,
				(int) (getBounds().getCenterX()-stringBounds.getWidth()/2), y-5);
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
	
	public Server getServer() {
		return server;
	}
}
