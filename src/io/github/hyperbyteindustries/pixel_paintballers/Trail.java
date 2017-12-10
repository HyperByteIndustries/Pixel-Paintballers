package io.github.hyperbyteindustries.pixel_paintballers;

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
public class Trail extends GameObject {

	private Handler handler;
	
	private Color colour;

	private int width, height;
	private float alpha = 1, life;
	
	/**
	 * Creates a new trail frame.
	 * @param x - The x coordinate of the frame.
	 * @param y - The y coordinate of the frame.
	 * @param id - The ID tag of the frame.
	 * @param handler - An instance of the Handler class, used to remove the frame.
	 * @param colour - The colour of the frame.
	 * @param width - The width of the frame.
	 * @param height The height of the frame.
	 * @param life - The life of the frame.
	 */
	public Trail(float x, float y, ID id, Handler handler, Color colour, int width, int height,
			float life) {
		super(x, y, id);
		
		this.handler = handler;
		this.colour = colour;
		this.width = width;
		this.height = height;
		this.life = life;
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, width, height);
	}

	// See tick() in GameObject.
	public void tick() {
		if (alpha > life) {
			alpha -= (life - 0.0001f);
		} else handler.removeObject(this);
	}

	// See render(Graphics2D graphics2d) in GameObject
	public void render(Graphics2D graphics2d) {
		graphics2d.setComposite(makeTransparent(alpha));
		graphics2d.setColor(colour);
		graphics2d.draw(getBounds());
		graphics2d.setComposite(makeTransparent(1));
	}
	
	/**
	 * Creates the transparent composite.
	 * @param alpha - The alpha value used to create the composite.
	 * @return The transparent composite.
	 */
	private AlphaComposite makeTransparent(float alpha) {
		int rule = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(rule, alpha));
	}
}
