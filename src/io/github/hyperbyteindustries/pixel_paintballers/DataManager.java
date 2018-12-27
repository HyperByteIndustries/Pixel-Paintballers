package io.github.hyperbyteindustries.pixel_paintballers;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

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
	 * Finds the external data file and creates it if not present.
	 */
	public static void init() {
		System.out.println(Game.MAINPREFIX + "Initialising Data Manager...");
		
		dataFile = new File("res/Data.txt");
		
		if (!(dataFile.exists())) {
			System.out.println(Game.WARNPREFIX + dataFile.getName() +
					" doesn't exist. Creating a new file...");
			
			try {
				dataFile.createNewFile();
				
				System.out.println(Game.MAINPREFIX + dataFile.getName() + " created.");
			} catch (IOException e) {
				System.err.println(Game.ERRORPREFIX + "Failed to create " + dataFile.getName() +
						":");
				
				e.printStackTrace();
			}
		}
		
		readData();
		
		if (savedStatistics.size() == 0) {
			System.out.println(Game.WARNPREFIX +
					"Statistics not found. Creating new statistics...");
			
			createStatistics();
		}
		
		System.out.println(Game.MAINPREFIX + "Data Manager Initialised.");
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
		
		System.out.println(Game.MAINPREFIX + "Statistics successfully created.");
	}
	
	/**
	 * Saves the game data to an external file.
	 */
	public static void saveData() {
		System.out.println(Game.MAINPREFIX + "Saving data...");
		
		try {
			FileWriter fileWriter = new FileWriter(dataFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			System.out.println(Game.MAINPREFIX + "Saving player data...");
			
			printWriter.println("Username: " + Game.player.getUsername());
			printWriter.println("Fill colour: " + Game.player.getFillColour().getRGB());
			printWriter.println("Outline colour: " + Game.player.getOutlineColour().getRGB());
			printWriter.println("Username colour: " + Game.player.getUsernameColour().getRGB());
			
			System.out.println(Game.MAINPREFIX + "Saving statistic data...");
			
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
			
			System.out.println(Game.MAINPREFIX + "Data successfully saved.");
		} catch (IOException e) {
			System.err.println(Game.ERRORPREFIX + "Failed to save data:");
			
			e.printStackTrace();
		}
	}

	/**
	 * Reads the external data of the game, constructing the player's customisation and the
	 * statistic database.
	 */
	public static void readData() {
		System.out.println(Game.MAINPREFIX + "Reading data...");
		
		try {
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String data;
			
			while (!((data = bufferedReader.readLine()) == null)) {
				if (data.startsWith("Username:"))
					Game.player.setUsername(data.substring(10));
				else if (data.startsWith("Fill colour:"))
					Game.player.setFillColour(new Color(Integer.parseInt(data.substring(13))));
				else if (data.startsWith("Outline colour:"))
					Game.player.setOutlineColour(new Color(Integer.parseInt(
							data.substring(16))));
				else if (data.startsWith("Username colour:"))
					Game.player.setUsernameColour(new Color(Integer.parseInt(
							data.substring(17))));
				else {
					String key = data.split(": ")[0];
					long stat = Long.parseLong(data.split(": ")[1]);
					
					if (key.equalsIgnoreCase("Time played")) convertTime(stat);
					
					setStatistic(key, stat);
				}
			}
			
			bufferedReader.close();
			
			System.out.println(Game.MAINPREFIX + "Data successfully read.");
		} catch (IOException e) {
			System.err.println(Game.ERRORPREFIX + "Failed to read data:");
			
			e.printStackTrace();
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
