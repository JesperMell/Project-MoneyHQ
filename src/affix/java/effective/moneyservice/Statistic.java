package affix.java.effective.moneyservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Statistic generator for producing data
 * like total amount, quantity, average amount and profit
 * 
 * @author Group Center
 */
public class Statistic {


	/**
	 * Variable used to get the exchange rate when exchanging one currency to another
	 */
	private static final double BUY_RATE = 0.995;
	/**
	 * Variable used to get the exchange rate when exchanging one currency to another
	 */
	private static final double SELL_RATE = 1.005;
	/**
	 * The profit made from an transaction
	 */
	private static final double PROFIT_MARGIN_RATE = 0.005;

	/**
	 * List holding a number of transaction objects
	 */
	private List<Transaction> transactions = new ArrayList<>();
	/**
	 * List holding all the currencyCodes
	 */
	private List<String> currencyCodes = new ArrayList<>();
	/**
	 * The name of this specific site
	 */
	private String siteName;
	
	/**
	 * A logger object
	 */
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");

	/**
	 * @param transactions - a list of transactions
	 * @param currencyCodes - a list of currency codes
	 * @param siteName - a String holding the site name, like "NORTH", "CENTER", "SOUTH" etc..
	 * @throws java.lang.IllegalArgumentException if
	 *         * No transactions provided
	 *         * Missing currency codes
	 *         * Missing site name
	 */
	public Statistic(List<Transaction> transactions, List<String> currencyCodes, String siteName) {
		logger.info("Entering Statistics constructor -->");
		if(transactions == null || transactions.isEmpty()) {
			logger.log(Level.WARNING, "Transactions not found! ");
			throw new IllegalArgumentException("No transactions provided");
		}
		else {
			if(currencyCodes == null || currencyCodes.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode not found! ");
				throw new IllegalArgumentException("Missing currency codes");
			}
			else {
				if(siteName == null || siteName.isEmpty()) {
					logger.log(Level.WARNING, "siteName not found! ");
					throw new IllegalArgumentException("Missing site name");
				}
			}
		}
		this.transactions = transactions;
		this.currencyCodes = currencyCodes;
		this.siteName = siteName;
		logger.info("Exiting Statistics constructor <--");
	}

	/**
	 * @return the currencyCodes
	 */
	public List<String> getCurrencyCodes() {
		return currencyCodes;
	}

	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	
	/**
	 * Get the total amount for each currency in the chosen reference currency
	 * @param filteredDate - a string holding a date in the format of YYYY-MM-DD
	 * @return Map with an amount for each currency in reference currency
	 */
	public Map<String, Integer> getTotalAmount(String filteredDate) {
		
		logger.info("Entering getTotalAmount method -->");
		// Read the exchange rate for input day and update the currencyMap with the new values
		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {

			String code = iter.next();
			// Get the value from the currencyMap in current currency code
			Currency temp = HQApp.currencyMap.get(code);

			// Convert BUY transactions into reference currency and sum them up
			Integer sumBuyAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))	// Filter the transactions current input day
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))											// Filter on currencyCode
					.filter(t -> t.getMode().equals(TransactionMode.BUY))												// Filter on TransactionMode
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate() * BUY_RATE))						// Convert transaction into reference currency and add profit exchange rate
					.reduce(0, Integer::sum);																			// Sum up the amount into total bought in reference currency

			Integer sumSellAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))	// Filter the transactions current input day
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))											// Filter on currencyCode
					.filter(t -> t.getMode().equals(TransactionMode.SELL))												// Filter on TransactionMode
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate() * SELL_RATE))						// Convert transaction into reference currency and add profit exchange rate
					.reduce(0, Integer::sum);																			// Sum up the amount into total sold in reference currency

			// Calculate the total profit from the sold amount and the bought amount based on profit margin
			Integer differenceSoldBought = sumSellAmount - sumBuyAmount;
			
			resultMap.putIfAbsent(code, differenceSoldBought);
		}
		logger.info("total profit for all currency : " + resultMap);
		logger.info("Exiting getTotalAmount method <--");
		return resultMap;
	}

	/**
	 * The same as method "getTotalAmount" filtered for only BUY-transactions
	 * @param filteredDate - a string holding a date in the format of YYYY-MM-DD
	 * @return The same as method "getTotalAmount" filtered for BUY-transactions
	 */
	public Map<String, Integer> getTotalAmountBuy(String filteredDate) {
		
		logger.info("Entering getTotalAmountBuy method -->");
		// Read the exchange rate for input day and update the currencyMap with the new values
		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {

			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			// Get the value from the currencyMap in current currency code
			Currency temp = HQApp.currencyMap.get(code);

			// Convert BUY transactions into reference currency and sum them up
			Integer sumBuyAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))	// Filter the transactions current input day
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))											// Filter on currencyCode
					.filter(t -> t.getMode().equals(TransactionMode.BUY))												// Filter on TransactionMode
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate() * BUY_RATE))						// Convert transaction into reference currency and add profit exchange rate
					.reduce(0, Integer::sum);
			
			resultMap.putIfAbsent(code, sumBuyAmount);
		}
		logger.info("Total amount bought for every currency: " + resultMap);
		logger.info("Exiting getTotalAmountBuy method <--");
		return resultMap;
	}

	/**
	 * The same as method "getTotalAmount" filtered for only SELL-transactions
	 * @param filteredDate - a string holding a date in the format of YYYY-MM-DD
	 * @return The same as method "getTotalAmount" filtered for SELL-transactions
	 */
	public Map<String, Integer> getTotalAmountSell(String filteredDate) {
		
		logger.info("Entering getTotalAmountSell method -->");
		// Read the exchange rate for input day and update the currencyMap with the new values
		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {

			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			// Get the value from the currencyMap in current currency code
			Currency temp = HQApp.currencyMap.get(code);

			// Convert BUY transactions into reference currency and sum them up
			Integer sumBuyAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))	// Filter the transactions current input day
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))											// Filter on currencyCode
					.filter(t -> t.getMode().equals(TransactionMode.SELL))												// Filter on TransactionMode
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate() * SELL_RATE))						// Convert transaction into reference currency and add profit exchange rate
					.reduce(0, Integer::sum);
			
			resultMap.putIfAbsent(code, sumBuyAmount);
		}
		logger.info("Total amount sold for every currency: " + resultMap);
		logger.info("Exiting getTotalAmountSell method <--");
		return resultMap;
	}	

	/**
	 * Method for calculating number of completed transactions done of each currency
	 * @return Map holding the result of calculation
	 */
	public Map<String, Integer> getTotalTransactions() {
		
		logger.info("Entering getTotalTransactions method -->");
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code)) 	// Filter based on currency code
					.count(); 													// Calculate the number of transactions in filtered currency

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		logger.info("Total Transactionns: " + resultMap);
		logger.info("Exiting getTotalTransactions method <--");
		return resultMap;
	}

	/**
	 * Method for calculating number of completed buy transactions done of each currency
	 * @return Map holding the result of calculation
	 */
	public Map<String, Integer> getTotalTransactionsBuy() {
		
		logger.info("Entering getTotalTransactionsBuy method -->");
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code)) 	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.BUY))       // Filter on TransactionMode.BUY
					.count(); 													// Calculate the number of transactions in filtered currency

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		logger.info("Total TransactionnsBuy: " + resultMap);
		logger.info("Exiting getTotalTransactionsBuy method <--");
		return resultMap;
	}

	/**
	 * Method for calculating number of completed sell transactions done of each currency
	 * @return Map holding the result of calculation
	 */
	public Map<String, Integer> getTotalTransactionsSell() {
		
		logger.info("Entering getTotalTransactionsSell method -->");
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code)) 	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.SELL))       // Filter on TransactionMode.SELL
					.count(); 													// Calculate the number of transactions in filtered currency

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		logger.info("Total TransactionnsSell: " + resultMap);
		logger.info("Exiting getTotalTransactionsSell method <--");
		return resultMap;
	}

	/**
	 * Method for calculating the difference of between sold and bought amount 
	 * in each currency
	 * @return Map holding the result of calculation in each currency
	 */
	public Map<String, Integer> getDiffCurrency() {
		
		logger.info("Entering getDiffCurrency method -->");
		Map<String, Integer> resultMap = new HashMap<>();
		int difference = 0;
		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}

			Integer buyAmount = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.BUY)) 		// Filter on TransactionMode.BUY
					.map(t -> t.getAmount()) 									// Get the amount in the transaction
					.reduce(0, Integer::sum);									// Sum up the amount into total buyAmount

			Integer sellAmount = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.SELL))		// Filter on TransactionMode.SELL
					.map(t -> t.getAmount())									// Get the amount in the transaction
					.reduce(0, Integer::sum);									// Sum up the amount into total sellAmount

			// Calculate the difference in bought amount and sold amount
			difference = buyAmount - sellAmount;
			resultMap.putIfAbsent(code, difference);
		}
		logger.info("Diff of all currency: " + resultMap);
		logger.info("Exiting getDiffCurrency method <--");
		return resultMap;
	}

	/**
	 * Method for calculating the profit in the reference currency for each currency per day
	 * Reading the exchange rates from file based on date.
	 * @param filteredDate - a string holding a date in the format of YYYY-MM-DD
	 * @return Map holding the result of calculation with profit in each currency per day in List{@code<Transaction>}
	 */
	public Map<String, Integer> getProfit(String filteredDate) {
		
		logger.info("Entering getProfit method -->");
		// Read the exchange rate for input day and update the currencyMap with the new values
		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {

			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			// Get the value from the currencyMap in current currency code
			Currency temp = HQApp.currencyMap.get(code);

			// Convert BUY transactions into reference currency and sum them up
			Integer sumAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))	// Filter the transactions current input day
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))											// Filter on currencyCode
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate()))									// Convert transaction into reference currency
					.reduce(0, Integer::sum);																			// Sum up the amount into total bought in reference currency

			// Calculate the total profit from the sold amount and the bought amount based on profit margin
			Integer profit = (int) Math.round((sumAmount * PROFIT_MARGIN_RATE));

			resultMap.putIfAbsent(code, profit);
		}
    
		return resultMap;
	}

	
	/**
	 * Method for calculating the average amount for buy transactions
	 * @return a resulting map holding the calculated average for each currency for BUY-transactions
	 */
	public Map<String, Integer> getAverageAmountBuy() {
		
		logger.info("Entering getAverageAmountBuy method -->");
		
		Map<String, Integer> transactionMap = getTotalTransactionsBuy();
		Map<String, Integer> resultMap = new HashMap<>();
		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			
			Integer totalAmountBuy = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.BUY)) 		// Filter on TransactionMode.BUY
					.map(t -> t.getAmount()) 									// Get the amount in the transaction
					.reduce(0, Integer::sum);									// Sum up the amount into total buyAmount
			
			// If the summed up amount is 0 set the average to 0
			if(totalAmountBuy == 0) {
				resultMap.putIfAbsent(code, 0);
			}
			else {
				// Calculate average by total transaction amount divided on the total amount of transactions
				resultMap.putIfAbsent(code, totalAmountBuy / transactionMap.get(code));				
			}
		}		
		logger.info("Avarage buy amount from all currency: " + resultMap);
		logger.info("Exiting getAverageAmountBuy method <--");
		return resultMap;
	}

	/**
	 * Method for calculating the average amount for sell transactions
	 * @return a resulting map holding the calculated average for each currency for SELL-transactions
	 */
	public Map<String, Integer> getAverageAmountSell() {
			
		logger.info("Entering getAverageAmountSell method -->");
		
		Map<String, Integer> transactionMap = getTotalTransactionsSell();
		Map<String, Integer> resultMap = new HashMap<>();
		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			if(code.isEmpty()) {
				logger.log(Level.WARNING, "currencyCode is empty! " + code);
			}
			
			Integer totalAmountBuy = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.SELL)) 		// Filter on TransactionMode.SELL
					.map(t -> t.getAmount()) 									// Get the amount in the transaction
					.reduce(0, Integer::sum);									// Sum up the amount into total sellAmount
			
			// If the summed up amount is 0 set the average to 0
			if(totalAmountBuy == 0) {
				resultMap.putIfAbsent(code, 0);
			}
			else {
				// Calculate average by total transaction amount divided on the total amount of transactions
				resultMap.putIfAbsent(code, totalAmountBuy / transactionMap.get(code));				
			}
		}		
		logger.info("Avarage sell amount from all currency: " + resultMap);
		logger.info("Exiting getAverageAmountSell method <--");
		return resultMap;
	}
	
	/**
	 * Initialize statistics for each site, and fetch transactions.
	 * 
	 * 
	 * @param sites - Site which to initialize new statistics from.
	 * @param startDay - The start day.
	 * @param endDay - The end day.
	 * @param currencies - Decides which transactions for currencies to use.
	 * @return the statistics for each site.
	 */
	public static List<Statistic> initializeFromSites(Set<Site> sites, LocalDate startDay, LocalDate endDay, List<String> currencies) {
		List<Statistic> statistics = new ArrayList<>();
		for (Site s : sites) {
			try {
				s.readTransactions(startDay, endDay);
			} catch (ClassNotFoundException e1) {
				logger.log(Level.SEVERE, "Site exception! " + e1);
				System.out.println("Something went wrong!");
			}
			
			if(s.getCompletedTransactions().isEmpty()) {
				System.out.println(String.format("There was no transactions to read from %s.", s.getSiteName()));
				continue;
			}

			try {
				statistics.add(new Statistic(s.getCompletedTransactions(), currencies, s.getSiteName()));
			} catch (IllegalArgumentException e) {
				logger.log(Level.WARNING, "Statistics exception! " + e);
				System.out.println(
						String.format("%s does not have any transactions and won't be included", s.getSiteName()));
			}
		}
		return statistics;
	}
}
