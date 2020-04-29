package affix.java.effective.moneyservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
		consHandler.setLevel(Level.WARNING);
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
		// MAIN STARTING HERE
		
		// Setting up logger.
		setupLogger();
		
		
		// BELOW CODE IS JUST FOR TESTING PURPOSE AND SHOULD NOT BE USED
		currencyMap = HQApp.readCurrencyConfigFile("ExchangeRates/CurrencyConfig_2020-04-01.txt");
		
		Site testSite = new Site("TestSite");
		
		LocalDate startDate = LocalDate.of(2020, 04, 01);
		LocalDate endDate = LocalDate.of(2020, 04, 20);
		
		testSite.readTransactions(startDate, endDate);
		List<Transaction> testTrans = testSite.getCompletedTransactions();
		
		ArrayList<String> currencyList = new ArrayList<String>(currencyMap.keySet());
		
		
		Statistic stats = new Statistic(testTrans, currencyList, testSite.getSiteName());
		
		
		Map<String, Integer> resultMap = createProfitStatistics(stats);
		
		int total = 0;
		for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
		    System.out.println(entry.getKey() + ":" + entry.getValue().toString());
		    total = total + entry.getValue();
		}
		System.out.format("PROFIT: %d", total);
		// CODE ABOVE UP TO COMENT SHOULD NOT BE USED AND IS ONLY USED FOR TESTING PURPOSE
		
	}
	
	private static Map<String, Integer> createProfitStatistics(Statistic stats) {
		Map<String, Integer> resultMap = new HashMap<>();
		Map<String, Integer> dayResultMap = new HashMap<>();
		Set<String> dateList = new TreeSet<>();
		
		stats.getTransactions().stream()
				.map(t -> String.format("%s", t.getTimeStamp().toLocalDate()))
				.forEach((t) -> { dateList.add(t);});
				
		for (Iterator<String> iter = dateList.iterator(); iter.hasNext(); ) {
			String fileDate = iter.next();
			dayResultMap = stats.getProfit(fileDate);
			
			for (Map.Entry<String, Integer> entry : dayResultMap.entrySet()) {
				if(resultMap.containsKey(entry.getKey())) {
					resultMap.replace(entry.getKey(), entry.getValue() + resultMap.get(entry.getKey()));
				}
				else {
					resultMap.putIfAbsent(entry.getKey(), entry.getValue());
				}
			}
		}

		return resultMap;
	}
	
	private void CLIApplication() {
		
	}
	
	
	public static Map<String, Currency> readCurrencyConfigFile(String filename) {
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
			System.out.println("An IOException occurred for file ");
		}
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
