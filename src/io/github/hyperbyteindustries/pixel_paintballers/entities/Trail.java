package io.github.hyperbyteindustries.pixel_paintballers.entities;

import static java.awt.AlphaComposite.SRC_OVER;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents the paintball trail of the game.
 * When constructed, this class is responsible for creating transparent trail frames that follow
 * the paintball.
 * @author Ramone Graham
 *
 */
public class Trail extends Entity {

	private Handler handler;
	
	private Rectangle bounds;
	private Color colour;
	private float life, alpha = 1;
	
	/**
	 * Creates a new trail frame.
	 * @param x - The X coordinate of the frame.
	 * @param y - The Y coordinate of the frame.
	 * @param handler - An instance of the Handler class, used to remove the frame.
	 * @param colour - The colour of the frame.
	 * @param width - The width of the frame.
	 * @param height The height of the frame.
	 * @param life - The life of the frame.
	 */
	public Trail(Handler handler, Rectangle bounds, Color colour, float life) {
		super(ID.TRAIL, bounds.x, bounds.y);
		
		this.handler = handler;
		this.bounds = bounds;
		this.colour = colour;
		this.life = life;
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return bounds;
	}

	// See tick() in GameObject.
	public void tick() {
		if (alpha > life) alpha -= (life - 0.0001f);
		else handler.removeEntity(this);
	}

	// See render(Graphics2D graphics2d) in GameObject
	public void render(Graphics2D graphics2D) {
		graphics2D.setComposite(AlphaComposite.getInstance(SRC_OVER, alpha));
		graphics2D.setColor(colour);
		graphics2D.draw(getBounds());
		graphics2D.setComposite(AlphaComposite.getInstance(SRC_OVER, 1));
	}
}
