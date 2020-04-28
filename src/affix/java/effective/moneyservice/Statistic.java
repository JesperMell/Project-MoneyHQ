package affix.java.effective.moneyservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Statistic {

	
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
	
	public Map<String, Integer> getTotalTransactions() {
		
	}
	
	public Map<String, Integer> getAverageAmount() {
		
	}
	
	/**
	 * Get the total amount for each currency in the chosen reference currency
	 * @param filteredDate
	 * @return a map with an amount for each currency in reference currency
	 */
	public Map<String, Integer> getTotalAmount(String filteredDate) {
		
		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> hm = new HashMap<String, Integer>();
				
		for(Transaction transaction : transactions) {
			hm.put( transaction.getCurrencyCode(), (int) Math.round((double) transaction.getAmount() / HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );
		}
		
		Set<Map.Entry<String, Integer>> eset = hm.entrySet();
		
		Map<String, Integer> resultMap = eset.stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
		
		return resultMap;
	}
	
	public Map<String, Integer> getDiffCurrency() {
		
	}
	
	/**
	 * The same as method "getTotalAmount" filtered for only BUY-transactions
	 * @param filteredDate
	 * @return The same as method "getTotalAmount" filtered for BUY-transactions
	 */
	public Map<String, Integer> getTotalBuy(String filteredDate) {

		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> hm = new HashMap<String, Integer>();
				
		for(Transaction transaction : transactions) {
			if (transaction.getMode().equals(TransactionMode.BUY))
			hm.put( transaction.getCurrencyCode(), (int) Math.round((double) transaction.getAmount() / HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );
		}
		
		Set<Map.Entry<String, Integer>> eset = hm.entrySet();
		
		Map<String, Integer> resultMap = eset.stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
		
		return resultMap;
	}

	/**
	 * The same as method "getTotalAmount" filtered for only SELL-transactions
	 * @param filteredDate
	 * @return The same as method "getTotalAmount" filtered for SELL-transactions
	 */
	public Map<String, Integer> getTotalSell(String filteredDate) {

		HQApp.currencyMap = HQApp.readCurrencyConfigFile(String.format("ExchangeRates/CurrencyConfig_%s.txt", filteredDate));
		Map<String, Integer> hm = new HashMap<String, Integer>();
				
		for(Transaction transaction : transactions) {
			if (transaction.getMode().equals(TransactionMode.SELL))
			hm.put( transaction.getCurrencyCode(), (int) Math.round((double) transaction.getAmount() / HQApp.currencyMap.get(transaction.getCurrencyCode()).getExchangeRate()) );
		}
		
		Set<Map.Entry<String, Integer>> eset = hm.entrySet();
		
		Map<String, Integer> resultMap = eset.stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
		
		return resultMap;
	}
	
	public Map<String, Integer> getProfit() {
		
	}
	
	public Map<String, Integer> getTransactionCountPerCurrency() {
		
	}
	
}
