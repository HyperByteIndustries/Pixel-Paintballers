package io.github.hyperbyteindustries.pixel_paintballers.net;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.GameObject;
import io.github.hyperbyteindustries.pixel_paintballers.Handler;
import io.github.hyperbyteindustries.pixel_paintballers.ID;
import io.github.hyperbyteindustries.pixel_paintballers.Paintball;
import io.github.hyperbyteindustries.pixel_paintballers.Player;
import io.github.hyperbyteindustries.pixel_paintballers.Trail;

/**
 * Represents a paintball in the multiplayer system.
 * When constructed, this class is responsible for the online management of the paintball.
 * @author Ramone Graham
 *
 */
public class IPaintball extends Paintball {

	private Player target;
	
	public IPaintball(float x, float y, ID id, Game game, Handler handler, GameObject shooter,
			Player target) {
		super(x, y, id, game, handler, shooter);
		
		this.target = target;
	}
	
	public void tick() {
		x += velX;
		y += velY;
		
		if (id == ID.PAINTBALL) {
			if (x <= 0 || x >= Game.XBOUND-9) handler.removeObject(this);
			if (y <= 0 || y >= Game.YBOUND-9) handler.removeObject(this);
		} else if (id == ID.BOUNCYPAINTBALL) {
			if (x <= 0 || x >= Game.XBOUND-9) velX *= -1;
			if (y <= 0 || y >= Game.YBOUND-9) velY *= -1;
		} else if (id == ID.HOMINGPAINTBALL) {
			float diffX = x-(target.getX()+12), diffY = y-(target.getY()+12),
					distance = (float) Math.sqrt((x-target.getX())*(x-target.getX())+
							(y-target.getY())*(y-target.getY()));
			
			velX = (float) (((-1.0/distance)*diffX)*7);
			velY = (float) (((-1.0/distance)*diffY)*7);
		}
		
		handler.addObject(new Trail(x, y, ID.TRAIL, handler, colour, getBounds().width,
				getBounds().height, 0.075f));
		
		collision();
	}
	
	public void setTarget(Player target) {
		this.target = target;
	}
	
	public Player getTarget() {
		return target;
	}
}
