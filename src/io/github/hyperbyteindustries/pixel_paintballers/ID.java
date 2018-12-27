package io.github.hyperbyteindustries.pixel_paintballers;

/**
 * Represents the identification tag of a game object.
 * When a game object class is created, (E.g. Player), it will get an ID tag in order to inherit certain behaviours.
 * @author Ramone Graham
 *
 */
public enum ID {

	PLAYER(), IPLAYER(), ENEMY(), MOVINGENEMY(), BOUNCYENEMY(), HOMINGENEMY(), IENEMY(),
	IMOVINGENEMY(), IBOUNCYENEMY(), IHOMINGENEMY(), PAINTBALL(), BOUNCYPAINTBALL(),
	HOMINGPAINTBALL(), TRAIL();
}
