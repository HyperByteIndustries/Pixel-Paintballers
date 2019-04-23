package io.github.hyperbyteindustries.pixel_paintballers.managers;

import static io.github.hyperbyteindustries.pixel_paintballers.Game.ERROR_PREFIX;
import static io.github.hyperbyteindustries.pixel_paintballers.Game.INFO_PREFIX;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * Represents the Audio manager of the game.
 * When utilised, this class will store audio files and play them.
 * @author Ramone Graham
 *
 */
public class AudioManager {

	private static Map<String, Music> musicMap = new HashMap<String, Music>();
	private static Map<String, Sound> soundMap = new HashMap<String, Sound>();
	
	/**
	 * Initialises the audio manager.
	 */
	public static void init() {
		System.out.println(new Date() + " " + INFO_PREFIX + "Initialising Audio manager...");
		
		try {
			musicMap.put("Title", new Music("Title screen.ogg"));
			musicMap.put("Menu 1", new Music("Main menu 1.ogg"));
			musicMap.put("Menu 2", new Music("Main menu 2.ogg"));
			musicMap.put("Menu 3", new Music("Main menu 3.ogg"));
			musicMap.put("Game 1", new Music("Singleplayer game.ogg"));
			musicMap.put("Game 2", new Music("Multiplayer game.ogg"));
			
			soundMap.put("Select", new Sound("Option select.ogg"));
			soundMap.put("Shot", new Sound("Paintball shot.ogg"));
			soundMap.put("Denied", new Sound("Access denied.ogg"));
			
			System.out.println(new Date() + " " + INFO_PREFIX + "Audio manager initialised.");
		} catch (SlickException exception) {
			System.err.print(new Date() + " " + ERROR_PREFIX + "An exception occured whilst "
					+ "initialising the Audio Manager - ");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Gets a piece of music to play.
	 * @param music - The name tag assigned to the music.
	 * @return The music to play.
	 */
	public static Music getMusic(String music) {
		return musicMap.get(music);
	}
	
	/**
	 * Gets a sound to play.
	 * @param sound - The name tag assigned to the sound.
	 * @return The sound to play.
	 */
	public static Sound getSound(String sound) {
		return soundMap.get(sound);
	}
}
