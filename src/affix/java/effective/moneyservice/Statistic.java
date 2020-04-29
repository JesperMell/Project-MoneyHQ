package affix.java.effective.moneyservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Statistic {


	private static final double BUY_RATE = 0.995;
	private static final double SELL_RATE = 1.005;
	private static final double PROFIT_MARGIN_RATE = 0.005;

	private List<Transaction> transactions = new ArrayList<>();
	private List<String> currencyCodes = new ArrayList<>();
	private String siteName;
	
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");

	/**
	 * @param transactions
	 * @param currencyCodes
	 * @param siteName
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
	 * @param A string holding a date in the format of YYYY-MM-DD
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
	 * @param A string holding a date in the format of YYYY-MM-DD
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
	 * @param A string holding a date in the format of YYYY-MM-DD
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
	 * @param none
	 * @return Map holding the result of calculation
	 */
	public Map<String, Integer> getTotalTransactions() {
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code)) 	// Filter based on currency code
					.count(); 													// Calculate the number of transactions in filtered currency

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		return resultMap;
	}

	/**
	 * Method for calculating number of completed buy transactions done of each currency
	 * @param none
	 * @return Map holding the result of calculation
	 */
	public Map<String, Integer> getTotalTransactionsBuy() {
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code)) 	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.BUY))       // Filter on TransactionMode.BUY
					.count(); 													// Calculate the number of transactions in filtered currency

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		return resultMap;
	}

	/**
	 * Method for calculating number of completed sell transactions done of each currency
	 * @param none
	 * @return Map holding the result of calculation
	 */
	public Map<String, Integer> getTotalTransactionsSell() {
		Map<String, Integer> resultMap = new HashMap<>();

		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code)) 	// Filter based on currency code
					.filter(t -> t.getMode().equals(TransactionMode.SELL))       // Filter on TransactionMode.SELL
					.count(); 													// Calculate the number of transactions in filtered currency

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		return resultMap;
	}

	/**
	 * Method for calculating the difference of between sold and bought amount 
	 * in each currency
	 * @param none
	 * @return Map holding the result of calculation in each currency
	 */
	public Map<String, Integer> getDiffCurrency() {
		Map<String, Integer> resultMap = new HashMap<>();
		int difference = 0;
		// Create iterator for the existing currency codes and iterate
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();

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
		return resultMap;
	}

	/**
	 * Method for calculating the profit in the reference currency for each currency per day
	 * Reading the exchange rates from file based on date.
	 * @param A string holding a date in the format of YYYY-MM-DD
	 * @return Map holding the result of calculation with profit in each currency per day in List<Transaction>
	 */
	public Map<String, Integer> getProfit(String filteredDate) {

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
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate()))									// Convert transaction into reference currency
					.reduce(0, Integer::sum);																			// Sum up the amount into total bought in reference currency

			Integer sumSellAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))	// Filter the transactions current input day
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))											// Filter on currencyCode
					.filter(t -> t.getMode().equals(TransactionMode.SELL))												// Filter on TransactionMode
					.map(t -> (int) Math.round(t.getAmount() * temp.getExchangeRate()))									// Convert transaction into reference currency
					.reduce(0, Integer::sum);																			// Sum up the amount into total sold in reference currency

			// Calculate the total profit from the sold amount and the bought amount based on profit margin
			Integer profit = (int) Math.round(((sumBuyAmount + sumSellAmount) * PROFIT_MARGIN_RATE));

			resultMap.putIfAbsent(code, profit);
		}
		return resultMap;
	}


	//	public Map<String, Integer> getTransactionCountPerCurrency() {
	//		
	//	}

	//		public Map<String, Integer> getAverageAmount() {
	//			
	//			Map<String, Integer> hmAmount = getTotalAmount(filteredDate);
	//			Map<String, Integer> hmNo = getTotalTransactions();
	//		}

	
	
	/**
	 * Get the average amount for each currency in the chosen reference currency
	 * @param A string holding a date in the format of YYYY-MM-DD
	 * @return Map with an average amount for each currency in reference currency
	 */
	public Map<String, Integer> getAverageAmount(String filteredDate) {
			
		Map<String, Integer> hmAmount = getTotalAmount(filteredDate);
		Map<String, Integer> hmNo = getTotalTransactions();
		Map<String, Integer> resultMap = new HashMap<String, Integer>(); 
			
		Set<Map.Entry<String, Integer>> esetAmount = hmAmount.entrySet();
		Set<Map.Entry<String, Integer>> esetNo = hmNo.entrySet();
			
		for (Map.Entry<String, Integer> meAmount : esetAmount)
			for (Map.Entry<String, Integer> meNo : esetNo)
				resultMap.put(meAmount.getKey(), meAmount.getValue()/meNo.getValue());
			
		return resultMap;
	}
	
	/**
	 * The same as method "getAverageAmount" but only for BUY-transactions
	 * @param A string holding a date in the format of YYYY-MM-DD
	 * @return The same as method "getAverageAmount" but only for BUY-transactions
	 */
	public Map<String, Integer> getAverageAmountBuy(String filteredDate) {
			
		Map<String, Integer> hmAmount = getTotalAmountBuy(filteredDate);
		Map<String, Integer> hmNo = getTotalTransactionsBuy();
		Map<String, Integer> resultMap = new HashMap<String, Integer>(); 
			
		Set<Map.Entry<String, Integer>> esetAmount = hmAmount.entrySet();
		Set<Map.Entry<String, Integer>> esetNo = hmNo.entrySet();
			
		for (Map.Entry<String, Integer> meAmount : esetAmount)
			for (Map.Entry<String, Integer> meNo : esetNo)
				resultMap.put(meAmount.getKey(), meAmount.getValue()/meNo.getValue());
			
		return resultMap;
	}

	/**
	 * The same as method "getAverageAmount" but only for SELL-transactions
	 * @param A string holding a date in the format of YYYY-MM-DD
	 * @return The same as method "getAverageAmount" but only for SELL-transactions
	 */
	public Map<String, Integer> getAverageAmountSell(String filteredDate) {
			
		Map<String, Integer> hmAmount = getTotalAmountSell(filteredDate);
		Map<String, Integer> hmNo = getTotalTransactionsSell();
		Map<String, Integer> resultMap = new HashMap<String, Integer>(); 
			
		Set<Map.Entry<String, Integer>> esetAmount = hmAmount.entrySet();
		Set<Map.Entry<String, Integer>> esetNo = hmNo.entrySet();
			
		for (Map.Entry<String, Integer> meAmount : esetAmount)
			for (Map.Entry<String, Integer> meNo : esetNo)
				resultMap.put(meAmount.getKey(), meAmount.getValue()/meNo.getValue());
			
		return resultMap;
	}
}
