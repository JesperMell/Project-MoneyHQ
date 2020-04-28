package affix.java.effective.moneyservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Statistic {


	private static final double BUY_RATE = 0.995;
	private static final double SELL_RATE = 1.005;
	
	private List<Transaction> transactions = new ArrayList<>();
	private List<String> currencyCodes = new ArrayList<>();
	private String siteName;

	/**
	 * @param transactions
	 * @param currencyCodes
	 * @param siteName
	 */
	public Statistic(List<Transaction> transactions, List<String> currencyCodes, String siteName) {
		this.transactions = transactions;
		this.currencyCodes = currencyCodes;
		this.siteName = siteName;
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

		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> hm = new HashMap<String, Integer>();

		for(Transaction transaction : transactions) {
			
			if (transaction.getMode().equals(TransactionMode.BUY))
			hm.put( transaction.getCurrencyCode(), 
					-(int) Math.round((double) transaction.getAmount() * BUY_RATE * HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );

			if (transaction.getMode().equals(TransactionMode.SELL))
			hm.put( transaction.getCurrencyCode(), 
					(int) Math.round((double) transaction.getAmount() * SELL_RATE * HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );

		}

		Set<Map.Entry<String, Integer>> eset = hm.entrySet();

		Map<String, Integer> resultMap = eset.stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

		return resultMap;
	}

	/**
	 * The same as method "getTotalAmount" filtered for only BUY-transactions
	 * @param A string holding a date in the format of YYYY-MM-DD
	 * @return The same as method "getTotalAmount" filtered for BUY-transactions
	 */
	public Map<String, Integer> getTotalBuy(String filteredDate) {

		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> hm = new HashMap<String, Integer>();

		for(Transaction transaction : transactions) {
			if (transaction.getMode().equals(TransactionMode.BUY))
				hm.put( transaction.getCurrencyCode(), 
						(int) Math.round((double) transaction.getAmount() * BUY_RATE * HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );
		}

		Set<Map.Entry<String, Integer>> eset = hm.entrySet();

		Map<String, Integer> resultMap = eset.stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

		return resultMap;
	}

	/**
	 * The same as method "getTotalAmount" filtered for only SELL-transactions
	 * @param A string holding a date in the format of YYYY-MM-DD
	 * @return The same as method "getTotalAmount" filtered for SELL-transactions
	 */
	public Map<String, Integer> getTotalSell(String filteredDate) {

		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> hm = new HashMap<String, Integer>();

		for(Transaction transaction : transactions) {
			if (transaction.getMode().equals(TransactionMode.SELL))
				hm.put( transaction.getCurrencyCode(), 
						(int) Math.round((double) transaction.getAmount() * SELL_RATE * HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );
		}

		Set<Map.Entry<String, Integer>> eset = hm.entrySet();

		Map<String, Integer> resultMap = eset.stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
		
		return resultMap;
	}	

	public Map<String, Integer> getTotalTransactions() {
		Map<String, Integer> resultMap = new HashMap<>();

		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();
			long count = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))
					.count();

			Integer result = Math.toIntExact(count);

			resultMap.putIfAbsent(code, result);
		}
		return resultMap;
	}


	public Map<String, Integer> getDiffCurrency() {
		Map<String, Integer> resultMap = new HashMap<>();
		int difference = 0;
		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {
			String code = iter.next();

			Integer buyAmount = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))
					.filter(t -> t.getMode().equals(TransactionMode.BUY))
					.map(t -> t.getAmount())
					.reduce(0, Integer::sum);

			Integer sellAmount = transactions.stream()
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))
					.filter(t -> t.getMode().equals(TransactionMode.SELL))
					.map(t -> t.getAmount())
					.reduce(0, Integer::sum);

			difference = buyAmount - sellAmount;
			resultMap.putIfAbsent(code, difference);
		}
		return resultMap;
	}

	public Map<String, Integer> getProfit(String filteredDate) {

		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> resultMap = new HashMap<>();

		for (Iterator<String> iter = currencyCodes.iterator(); iter.hasNext(); ) {

			String code = iter.next();
			Currency temp = HQApp.currencyMap.get(code);

			Integer sumBuyAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))
					.filter(t -> t.getMode().equals(TransactionMode.BUY))
					.map(t -> t.getAmount())
					.reduce(0, Integer::sum);

			Integer sumSellAmount = transactions.stream()
					.filter(t -> filteredDate.equalsIgnoreCase(String.format("%s", t.getTimeStamp().toLocalDate())))
					.filter(t -> t.getCurrencyCode().equalsIgnoreCase(code))
					.filter(t -> t.getMode().equals(TransactionMode.SELL))
					.map(t -> t.getAmount())
					.reduce(0, Integer::sum);

			Double valueInventory = (sumBuyAmount - sumSellAmount) * temp.getExchangeRate();
			Double sumOutInSEK = sumBuyAmount * (temp.getExchangeRate()* 0.995);
			Double sumInInSEK = sumSellAmount * (temp.getExchangeRate()*1.005);

			int profit = (int) Math.round((sumInInSEK - (sumOutInSEK)) + (valueInventory));

			resultMap.putIfAbsent(code, profit);
		}
		return resultMap;
	}

	//	
	//	public Map<String, Integer> getTransactionCountPerCurrency() {
	//		
	//	}

	//	public Map<String, Integer> getAverageAmount() {
	//		
	//	}
}
