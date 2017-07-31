package io.github.hyperbyteindustries.pixel_paintballers;

import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents the main player of the game.
 * When constructed, this class is responsible for the management of the player.
 * @author Ramone Graham
 *
 */
public class Player extends GameObject {

	// See constructor in GameObject.
	public Player(float x, float y, ID id) {
		super(x, y, id);
	}

	// See getBounds() in GameObject.
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 32, 32);
	}

	// See tick() in GameObject.
	public void tick() {
		x += velX;
		y += velY;
		
		x = Game.clamp(x, 0, Game.XBOUND-33);
		y = Game.clamp(y, 0, Game.YBOUND-33);
	}

	// See render(Graphics2D graphics2d) in GameObject.
	public void render(Graphics2D graphics2d) {
		graphics2d.setColor(RED);
		graphics2d.fill(getBounds());
		graphics2d.setColor(WHITE);
		graphics2d.draw(getBounds());
	}
}
