package io.github.hyperbyteindustries.pixel_paintballers;

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
	 * Stores the game's music in audio maps.
	 */
	public static void init() {
		try {
			musicMap.put("Title", new Music("res/Title screen.ogg"));
			musicMap.put("Menu 1", new Music("res/Main menu 1.ogg"));
			musicMap.put("Menu 2", new Music("res/Main menu 2.ogg"));
			musicMap.put("Menu 3", new Music("res/Main menu 3.ogg"));
			musicMap.put("Game 1", new Music("res/Singleplayer game.ogg"));
			musicMap.put("Game 2", new Music("res/Multiplayer game.ogg"));
			
			soundMap.put("Select", new Sound("res/Option select.ogg"));
			soundMap.put("Shot", new Sound("res/Paintball shot.ogg"));
			soundMap.put("Denied", new Sound("res/Access denied.ogg"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a piece of music to play.
	 * @param key - The name tag assigned to the music.
	 * @return The music to play.
	 */
	public static Music getMusic(String key) {
		return musicMap.get(key);
	}
	
	/**
	 * Gets a piece of sound to play.
	 * @param key - The name tag assigned to the sound.
	 * @return The sound to play.
	 */
	public static Sound getSound(String key) {
		return soundMap.get(key);
	}
}
