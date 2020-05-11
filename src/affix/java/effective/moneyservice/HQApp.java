package affix.java.effective.moneyservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class triggers an application to show statistics for the chosen transactions
 * 
 * @author Group Center
 *
 */
public class HQApp {

	/**
	 * A map holding 
	 */
	static Map<String, Site> sites = new HashMap<>();
	/**
	 * A map holding currency objects
	 */
	static Map<String, Currency> currencyMap = new HashMap<>();

	/**
	 * A logger object
	 */
	// create logger
	private static Logger logger;

	/**
	 * Variable holding the value for minimum ordered amount
	 */
	static final  int amountLimit = 50;

	static {
		logger = Logger.getLogger("affix.java.effective.moneyservice");
	}


	/**
	 * Method setting up logger info
	 */
	private static void setupLogger() {
		LogManager.getLogManager().reset();
		// set the level of logging.
		logger.setLevel(Level.ALL);
		// Create a new Handler for console.
		ConsoleHandler consHandler = new ConsoleHandler();
		consHandler.setLevel(Level.SEVERE);
		logger.addHandler(consHandler);

		try {
			// Create a new Handler for file.
			FileHandler fHandler = new FileHandler("HQlogger.log");
			fHandler.setFormatter(new SimpleFormatter());
			// set level of logging
			fHandler.setLevel(Level.FINEST);
			logger.addHandler(fHandler);
		}catch(IOException e) {
			logger.log(Level.SEVERE, "File logger not working! ", e);
		}
	}

	/**
	 * @param args
	 * The main method that execute the program 
	 */
	public static void main(String[] args) {
		// Setting up logger.
		setupLogger();

		if(args.length > 0) {
			currencyMap = readCurrencyConfigFile(args[0]);
		}
		else {
			currencyMap = HQApp.readCurrencyConfigFile("ExchangeRates/CurrencyConfig_Default_Accepted.txt");
		}
		readSiteConfig();
		logger.info("-------Configuration_Ends-------\n");
		CLIapplication();
	}

	/**
	 * This method supports user interaction via CLI,
	 * methods "HQmenu()" and "creatNewSite()"
	 */
	private static void CLIapplication() {

		boolean done = false;
		do {
			int choice = HQmenu();
			Site newSite;

			switch(choice) {
			case 1:
				System.out.println("Register exchange office");
				newSite = createNewSite();
				if(sites.containsKey(newSite.getSiteName())) {
					System.out.println("Site already registred!1");
				}
				else {
					sites.putIfAbsent(newSite.getSiteName(), newSite);					
					writeNewSiteToConfigFile(newSite.getSiteName());
				}
				break;
			case 2:
				if(sites.isEmpty()) {
					System.out.println("You need to register site(s) first.");
					break;
				}
				CLIHelper.menuInput();
				break;
			case 0:
				done = true;
				break;
			default:
				System.out.println("Not a valid menu choice!");
			}
			logger.info("-------Task_Done-------\n");
		}while(!done);
	}

	/**
	 * A menu for user choice of operation 
	 * @return int - an int based on the user input
	 */
	private static int HQmenu() {
		logger.info("Entering HQmenu method -->");
		int choice = 0;
		boolean ok;
		do {
			ok = true;
			System.out.println("Money Service HQ");
			System.out.println("----------------");
			System.out.println("What would you like to do?");
			System.out.println("1 - Register a new exchange office");
			System.out.println("2 - Get statistics for registered offices");
			System.out.println("0 - Exit the HQ application");

			System.out.print("Enter your choice: ");
			String userChoice = CLIHelper.input.next();

			try {
				choice = Integer.parseInt(userChoice);
			}catch(NumberFormatException e) {
				logger.log(Level.WARNING, "choice: " + choice + " made exception! " + e);
				System.out.format("Your choice %s is not accepted!\n", userChoice);
				ok = false;
			}
		}while(!ok);

		logger.info("Exiting HQmenu method <--");
		return choice;
	}

	/**
	 * Creating a new Site from a Scanner input
	 * @return Site - a Site based on the input from the user
	 */
	private static Site createNewSite() {
		logger.info("Entering createNewSite method -->");
		Site newSite = null;
		boolean ok;

		do {
			try {
				ok = true;
				System.out.println("Write the name of the exchange office (must be the same as existing report)");
				String siteName = CLIHelper.input.next();
				newSite = new Site(siteName.toUpperCase());
			}
			catch (IllegalArgumentException e) {
				logger.log(Level.SEVERE, "Site generation exception! " + e);
				System.out.println("The name can not be empty");
				ok = false;
			}
		} while (!ok);

		logger.info("Exiting createNewSite method <--");
		return newSite;
	}

	/**
	 * Read the contents in a currency config file
	 * and convert the contents to a map with currency code as key
	 * @param filename - a currency config text file name
	 * @return a Map with currency code as key and Currency as value 
	 */

	public static Map<String, Currency> readCurrencyConfigFile(String filename) {
		logger.info("Entering readCurrencyConfigFIle method -->");
		int lineNumber = 1;
		Map<String, Currency> tempMap = new HashMap<>();
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			while (br.ready()) {

				String row = br.readLine();
				if (lineNumber++ < 2) continue;

				Currency currency = parseInput(row);

				tempMap.putIfAbsent(currency.getCurrencyCode(), currency);
			}
		}
		catch (IOException ioe) {
			logger.log(Level.WARNING, "Could not read CurrencyConfig file properly! " + ioe);
		}
		logger.info("Exiting readCurrencyConfigFIle method <--");
		return tempMap;
	}

	/**
	 * A helper method to split up the parts in a text file
	 * and convert it to a Currency
	 * @param input - a String holding raw data
	 * @return Currency - a Currency based on parsed input
	 */
	private static Currency parseInput(String input) {

		// The column looks like following:
		// column 0 = Period
		// column 1 = Group
		// column 2 = "Serie" (Currency code)
		// column 3 = Exchange rate
		String[] parts = input.split(";");

		String[] currencyCodeParts = parts[2].split(" ");
		String currencyCode = currencyCodeParts[1].strip();

		String exchangeRateString = parts[3].strip();
		double exchangeRate = Double.parseDouble(exchangeRateString);

		if (currencyCodeParts[0].strip().length() > 1)
			return new Currency(currencyCode, exchangeRate/100);
		else
			return new Currency(currencyCode, exchangeRate);
	}

	/**
	 * Method for reading a stored site from SiteConfiguration.txt file
	 */
	private static void readSiteConfig() {

		try(BufferedReader br = new BufferedReader(new FileReader("SiteConfiguration.txt"))) {
			while (br.ready()) {
				String line = br.readLine().strip().toUpperCase();
				Site readSite = new Site(line);

				sites.putIfAbsent(readSite.getSiteName(), readSite);
			}
		}catch (IOException ioe) {
			logger.log(Level.WARNING, "Could not read SiteConfig file properly! " + ioe);
		}
	}

	/**
	 * Method for writing a string to SiteConfiguration.txt to store a registered site
	 * @param siteName - A string with the name of the newly registered site
	 */
	private static void writeNewSiteToConfigFile(String siteName) {

		try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("SiteConfiguration.txt", true)))) {
			if(sites.isEmpty())
				pw.write(String.format("%s", siteName));
			else
				pw.write(String.format("\n%s", siteName));
		}catch (IOException ioe) {
			logger.log(Level.WARNING, "Could not write to SiteConfiguration file properly! " + ioe);
		}
	}
}
