package affix.java.effective.moneyservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class HQApp {

	static Map<String, Site> sites = new HashMap<>();
	static Map<String, Currency> currencyMap = new HashMap<>();

	// create logger
	private static Logger logger;

	static {
		logger = Logger.getLogger("affix.java.effective.moneyservice");
	}


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

	public static void main(String[] args) {
		// Setting up logger.
		setupLogger();

		if(args.length > 0) {
			currencyMap = readCurrencyConfigFile(args[0]);
		}
		else {
			currencyMap = HQApp.readCurrencyConfigFile("ExchangeRates/CurrencyConfig_Default_Accepted.txt");
		}
		logger.info("-------Configuration_Ends-------\n");
		CLIapplication();
	}

	private static void CLIapplication() {

		boolean done = false;
		do {
			int choice = HQmenu();
			Site newSite;

			switch(choice) {
			case 1:
				System.out.println("Register exchange office");
				newSite = createNewSite();
				sites.putIfAbsent(newSite.getSiteName(), newSite);
				break;
			case 2:
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
				logger.log(Level.SEVERE, "choice: " + choice + " made exception! " + e);
				System.out.format("Your choice %s is not accepted!", userChoice);
				ok = false;
			}
		}while(!ok);
		
		logger.info("Exiting HQmenu method <--");
		return choice;
	}

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
			System.out.println("An IOException occurred for file ");
		}
		logger.info("Exiting readCurrencyConfigFIle method <--");
		return tempMap;
	}

	public static void readConfigFile() {

	}

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
}
