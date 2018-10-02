package io.github.hyperbyteindustries.pixel_paintballers.net;

import static java.awt.Color.WHITE;

import java.awt.Graphics2D;
import java.util.LinkedList;

import io.github.hyperbyteindustries.pixel_paintballers.Enemy;
import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Player;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet08EnemyShot;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet09TargetChange;
import io.github.hyperbyteindustries.pixel_paintballers.net.packets.Packet10EnemyMove;

/**
 * Represents an enemy in multiplayer system.
 * When constructed, this class is responsible for the online management of the enemy.
 * @author Ramone Graham
 *
 */
public class IEnemy extends Enemy {

	private Server server;
	
	private int enemyNumber;
	
	public IEnemy(float x, float y, ID id, Game game, Handler handler, Server server,
			int enemyNumber) {
		super(x, y, id, game, handler);
		
		this.server = server;
		this.enemyNumber = enemyNumber;
	}
	
	public void tick() {
		x += velX;
		y += velY;
		
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
		
		if (!(targetConnected)) {
			LinkedList<Player> targets = new LinkedList<Player>();
			
			for (int i = 0; i < handler.getObjects().size(); i++) {
				GameObject tempObject = handler.getObjects().get(i);
				
				if (tempObject.getID() == ID.PLAYER || tempObject.getID() == ID.IPLAYER) {
					targets.add((Player) tempObject);
				}
			}
			
			target = targets.get(random.nextInt(targets.size()));
			
			if (!(server == null)) {
				Packet09TargetChange packet = new Packet09TargetChange(enemyNumber,
						target.getUsername());
				packet.writeData(server);
			}
		 }
		
		if (id == ID.MOVINGENEMY) {
			if (!(server == null)) {
				float diffX = x-(target.getX()+4), diffY = y-(target.getY()+4),
						distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
								(y-target.getY())*(y-target.getY()));
				
				velX = (float) ((-1.0/distance)*diffX);
				velY = (float) ((-1.0/distance)*diffY);
				
				Packet10EnemyMove packet = new Packet10EnemyMove(enemyNumber, velX, velY);
				packet.writeData(server);
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
				
				if (id == ID.ENEMY || id == ID.MOVINGENEMY) paintball = new IPaintball(x+8, y+8,
						ID.PAINTBALL, game, handler, this, target);
				else if (id == ID.BOUNCYENEMY) paintball = new IPaintball(x+8, y+8,
						ID.BOUNCYPAINTBALL, game, handler, this, target);
				else if (id == ID.HOMINGENEMY) paintball = new IPaintball(x+8, y+8,
						ID.HOMINGPAINTBALL, game, handler, this, target);
				
				handler.addObject(paintball);
				
				float diffX = x-target.getX(), diffY = y-target.getY(),
						distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
								(y-target.getY())*(y-target.getY()));
				
				paintball.setVelX((float) (((-1.0/distance)*diffX)*7));
				paintball.setVelY((float) (((-1.0/distance)*diffY)*7));
				
				Packet08EnemyShot packet = new Packet08EnemyShot(enemyNumber,
						target.getUsername(), paintball.getID(), paintball.getX(),
						paintball.getY(), paintball.getVelX(), paintball.getVelY());
				packet.writeData(server);
			}
			
			shootTime = 420;
		} else shootTime--;
	}
	
	public void render(Graphics2D graphics2d) {
		super.render(graphics2d);
		
		String targetMessage = "Target: " + target.getUsername();
		
		graphics2d.setColor(WHITE);
		graphics2d.drawString(targetMessage, x-((targetMessage.length()-1)/2*10), y-5);
	}
	
	/**
	 * Sets the target to the given player.
	 * @param target - The target player to be set.
	 */
	public void setTarget(Player target) {
		this.target = target;
	}
	
	/**
	 * Sets the enemy number to the given value.
	 * @param enemyNumber - The number to be set.
	 */
	public void setEnemyNumber(int enemyNumber) {
		this.enemyNumber = enemyNumber;
	}
	
	/**
	 * Gets the player target of the enemy.
	 * @return The player target.
	 */
	public Player getTarget() {
		return target;
	}
	
	/**
	 * Gets the enemy number of the enemy.
	 * @return The enemy number.
	 */
	public int getEnemyNumber() {
		return enemyNumber;
	}
}
