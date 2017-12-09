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

	public static File playerData;
	public static File statData;
	
	public static HashMap<String, Long> savedStatistics = new HashMap<String, Long>();
	
	/**
	 * Finds external data files and creates them if not present.
	 */
	public static void init() {
		playerData = new File("res/Player.txt");
		statData = new File("res/Stats.txt");
		
		if (!(playerData.exists())) {
			System.out.println(playerData.getName() + " doesn't exist. Creating a new file...");
			
			try {
				playerData.createNewFile();
				
				System.out.println(playerData.getName() + " created.");
			} catch (IOException e) {
				System.out.println("Failed to create " + playerData.getName() + ".");
				
				e.printStackTrace();
			}
		}
		
		if (!(statData.exists())) {
			System.out.println(statData.getName() + " doesn't exist. Creating a new file...");
			
			try {
				statData.createNewFile();
				
				System.out.println(statData.getName() + " created.");
			} catch (IOException e) {
				System.out.println("Failed to create " + statData.getName() + ".");
				
				e.printStackTrace();
			}
		}
		
		readData();
		
		if (savedStatistics.size() == 0) {
			createStatistics();
		}
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
	}
	
	/**
	 * Saves all data of the game.
	 */
	public static void saveAllData() {
		savePlayerData();
		saveStatistics();
	}

	/**
	 * Saves the player data to an external file.
	 */
	public static void savePlayerData() {
		System.out.println("Saving player data...");
		
		try {
			FileWriter fileWriter = new FileWriter(playerData);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			printWriter.println("Username: " + Game.player.getUsername());
			printWriter.println("Fill colour: " + Game.player.getFillColour().getRGB());
			printWriter.println("Outline colour: " + Game.player.getOutlineColour().getRGB());
			printWriter.println("Username colour: " + Game.player.getUsernameColour().getRGB());
			printWriter.close();
			
			System.out.println("Data successfully saved.");
		} catch (IOException e) {
			System.out.println("Failed to save data.");
			
			e.printStackTrace();
		}
	}

	/**
	 * Saves the player's statistics to the external file.
	 */
	public static void saveStatistics() {
		System.out.println("Saving statistics...");
		
		try {
			FileWriter fileWriter = new FileWriter(statData);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			for (String stat : savedStatistics.keySet()) {
				printWriter.println(stat + ": " + savedStatistics.get(stat));
			}
			
			printWriter.close();
			
			System.out.println("Data successfully saved.");
		} catch (IOException e) {
			System.out.println("Failed to save data.");
			
			e.printStackTrace();
		}
	}

	/**
	 * Reads the external data of the game.
	 */
	public static void readData() {
		readPlayerData();
		readStatistics();
	}

	/**
	 * Reads the external player data, constructing the player's customisation.
	 */
	private static void readPlayerData() {
		System.out.println("Reading player data...");
		
		try {
			FileReader fileReader = new FileReader(playerData);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String data;
			
			while (!((data = bufferedReader.readLine()) == null)) {
				if (data.startsWith("Username:")) {
					Game.player.setUsername(data.substring(10));
				} else if (data.startsWith("Fill colour:")) {
					Game.player.setFillColour(new Color(Integer.parseInt(data.substring(13))));
				} else if (data.startsWith("Outline colour:")) {
					Game.player.setOutlineColour(new Color(Integer.parseInt(data
							.substring(16))));
				} else if (data.startsWith("Username colour:")) {
					Game.player.setUsernameColour(new Color(Integer.parseInt(data
							.substring(17))));
				}
			}
			
			bufferedReader.close();
			
			System.out.println("Data successfully read.");
		} catch (IOException e) {
			System.out.println("Failed to read data.");
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the player's statistics from the external file, constructing its database.
	 */
	private static void readStatistics() {
		System.out.println("Reading statistics...");
		
		try {
			FileReader fileReader = new FileReader(statData);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String data;
			
			while (!((data = bufferedReader.readLine()) == null)) {
				String key = data.replace(": ", "%").split("%")[0];
				long stat = Long.parseLong(data.replace(": ", "%").split("%")[1]);
				setStatistic(key, stat);
			}
			
			bufferedReader.close();
			
			System.out.println("Data successfully read.");
		} catch (IOException e) {
			System.out.println("Failed to read data.");
			
			e.printStackTrace();
		}
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
	 * Gets a statistic from the database. 
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
