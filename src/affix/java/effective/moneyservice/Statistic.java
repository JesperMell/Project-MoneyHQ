package affix.java.effective.moneyservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, Integer> getTotalAmount() {
		
	}
	
	public Map<String, Integer> getDiffCurrency() {
		
	}
	
	public Map<String, Integer> getTotalBuy() {
		
	}
	
	public Map<String, Integer> getTotalSell() {
		
	}
	
	public Map<String, Integer> getProfit() {
		
	}
	
	public Map<String, Integer> getTransactionCountPerCurrency() {
		
	}
	
}
