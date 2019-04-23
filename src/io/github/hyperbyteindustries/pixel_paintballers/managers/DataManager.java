package io.github.hyperbyteindustries.pixel_paintballers.managers;

import static io.github.hyperbyteindustries.pixel_paintballers.Game.ERROR_PREFIX;
import static io.github.hyperbyteindustries.pixel_paintballers.Game.INFO_PREFIX;
import static io.github.hyperbyteindustries.pixel_paintballers.Game.WARN_PREFIX;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;

import io.github.hyperbyteindustries.pixel_paintballers.Game;
import io.github.hyperbyteindustries.pixel_paintballers.ui.KeyInput;

/**
 * Represents the data manager of the game.
 * When utilised, this class will store statistics and write the data to an external file.
 * @author Ramone Graham
 *
 */
public class DataManager {

	private static File dataFile;
	private static HashMap<String, Long> savedStatistics = new HashMap<String, Long>();
	
	public static int[] timePlayed = new int[3];
	
	/**
	 * Initialises the data manager.
	 */
	public static void init() {
		System.out.println(new Date() + " " + INFO_PREFIX + "Initialising Data Manager...");
		
		dataFile = new File("Data.txt");
		
		if (!(dataFile.exists())) {
			System.out.println(new Date() + " " + WARN_PREFIX + dataFile.getName() +
					" doesn't exist. Creating a new file...");
			
			try {
				dataFile.createNewFile();
				
				System.out.println(new Date() + " " + INFO_PREFIX + dataFile.getName() +
						" created.");
			} catch (IOException exception) {
				System.err.println(new Date() + " " + ERROR_PREFIX + "Failed to create " +
						dataFile.getName() + " - ");
				exception.printStackTrace();
			}
		}
		
		readData();
		
		if (savedStatistics.size() == 0) {
			System.out.println(new Date() + " " + WARN_PREFIX +
					"Statistics not found. Creating new statistics...");
			
			createStatistics();
		}
		
		System.out.println(new Date() + " " + INFO_PREFIX + "Data Manager Initialised.");
	}

	/**
	 * Creates the statistics for the game if they are not found from the data file.
	 */
	private static void createStatistics() {
		setStatistic("Highscore (" + Game.Difficulty.EASY.name() + ")", 0);
		setStatistic("Highscore (" + Game.Difficulty.NORMAL.name() + ")", 0);
		setStatistic("Highscore (" + Game.Difficulty.HARD.name() + ")", 0);
		setStatistic("Highscore (" + Game.Difficulty.EXTREME.name() + ")", 0);
		setStatistic("Time played", 0);
		setStatistic("Games played", 0);
		setStatistic("Shots fired", 0);
		setStatistic("Total kills", 0);
		
		System.out.println(new Date() + " " + INFO_PREFIX + "Statistics successfully created.");
	}
	
	/**
	 * Saves the game data to an external file.
	 */
	public static void saveData() {
		System.out.println(new Date() + " " + INFO_PREFIX + "Saving data...");
		
		try {
			FileWriter fileWriter = new FileWriter(dataFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			System.out.println(new Date() + " " + INFO_PREFIX + "Saving player data...");
			
			printWriter.println("Username: " + Game.player.getUsername());
			printWriter.println("Fill colour: " + Game.player.getFillColour().getRGB());
			printWriter.println("Outline colour: " + Game.player.getOutlineColour().getRGB());
			printWriter.println("Username colour: " + Game.player.getUsernameColour().getRGB());
			
			System.out.println(new Date() + " " + INFO_PREFIX + "Saving option data...");

			printWriter.println("Music volume: " + Game.musicVolume);
			printWriter.println("SFX volume: " + Game.sfxVolume);
			printWriter.println("Up keybind: " + KeyInput.up.keyCode);
			printWriter.println("Down keybind: " + KeyInput.down.keyCode);
			printWriter.println("Left keybind: " + KeyInput.left.keyCode);
			printWriter.println("Right keybind: " + KeyInput.right.keyCode);
			printWriter.println("Reload keybind: " + KeyInput.reload.keyCode);
			
			System.out.println(new Date() + " " + INFO_PREFIX + "Saving statistic data...");
			
			for (String stat : savedStatistics.keySet()) {
				if (stat.equalsIgnoreCase("Time played")) {
					long seconds = timePlayed[0];
					int minutes = timePlayed[1], hours = timePlayed[2];
					
					minutes += hours*60;
					seconds += minutes*60;
					
					setStatistic(stat, seconds);
				}
				
				printWriter.println(stat + ": " + savedStatistics.get(stat));
			}
			
			printWriter.close();
			
			System.out.println(new Date() + " " + INFO_PREFIX + "Data successfully saved.");
		} catch (IOException exception) {
			System.err.println(new Date() + " " + ERROR_PREFIX + "Failed to save data - ");
			exception.printStackTrace();
		}
	}

	/**
	 * Reads the external data of the game, constructing the player's customisation and the
	 * statistic database.
	 */
	public static void readData() {
		System.out.println(new Date() + " " + INFO_PREFIX + "Reading data...");
		
		try {
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String data;
			
			while (!((data = bufferedReader.readLine()) == null)) {
				String key = data.split(": ")[0];
				String value = data.split(": ")[1];
				
				if (key.equals("Username")) Game.player.setUsername(value);
				else if (key.equals("Fill colour"))
					Game.player.setFillColour(new Color(Integer.parseInt(value)));
				else if (key.equals("Outline colour"))
					Game.player.setOutlineColour(new Color(Integer.parseInt(value)));
				else if (key.equals("Username colour"))
					Game.player.setUsernameColour(new Color(Integer.parseInt(value)));
				else if (key.equals("Music volume")) Game.musicVolume = Float.parseFloat(value);
				else if (key.equals("SFX volume")) Game.sfxVolume = Float.parseFloat(value);
				else if (key.equals("Up keybind")) KeyInput.up.keyCode = Integer.parseInt(value);
				else if (key.equals("Down keybind"))
					KeyInput.down.keyCode = Integer.parseInt(value);
				else if (key.equals("Left keybind"))
					KeyInput.left.keyCode = Integer.parseInt(value);
				else if (key.equals("Right keybind"))
					KeyInput.right.keyCode = Integer.parseInt(value);
				else if (key.equals("Reload keybind"))
					KeyInput.reload.keyCode = Integer.parseInt(value);
				else {
					if (key.equals("Time played")) convertTime(Long.parseLong(value));
					
					setStatistic(key, Long.parseLong(value));
				}
			}
			
			bufferedReader.close();
			
			System.out.println(new Date() + " " + INFO_PREFIX + "Data successfully read.");
		} catch (IOException exception) {
			System.err.println(new Date() + " " + ERROR_PREFIX + "Failed to read data - ");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Coverts the time played from seconds to the format of hours:minutes:seconds.
	 * @param time - Time played in seconds.
	 */
	private static void convertTime(long time) {
		int seconds = (int) time, minutes = 0, hours = 0;
		
		while (seconds >= 60) {
			minutes += 1;
			seconds -= 60;
		}
		
		while (minutes >= 60) {
			hours += 1;
			minutes -= 60;
		}
		
		timePlayed = new int[] {seconds, minutes, hours};
	}

	/**
	 * Sets a statistic to a given value.
	 * @param stat - The name of the statistic.
	 * @param value - The value to set.
	 */
	public static void setStatistic(String stat, long value) {
		savedStatistics.put(stat, value);
	}
	
	/**
	 * Returns a statistic from the database. 
	 * @param stat - The statistic to retrieve.
	 * @return The requested statistic.
	 */
	public static long getStatistic(String stat) {
		return savedStatistics.get(stat);
	}
	
	/**
	 * Increases a statistic by a given value.
	 * @param stat - The statistic to be increased.
	 * @param count - The amount to increase the statistic by.
	 */
	public static void increaseStatistic(String stat, int count) {
		setStatistic(stat, getStatistic(stat) + count);
	}
}
