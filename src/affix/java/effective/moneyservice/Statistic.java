package affix.java.effective.moneyservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	
	

	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}



	/**
	 * @return the currencyCodes
	 */
	public List<String> getCurrencyCodes() {
		return currencyCodes;
	}



	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
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

	//	public Map<String, Integer> getAverageAmount() {
	//		
	//	}
	//	
	//	public Map<String, Integer> getTotalAmount() {
	//		
	//	}
	//	
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
	//	
	//	public Map<String, Integer> getTotalBuy() {
	//		
	//	}
	//	
	//	public Map<String, Integer> getTotalSell() {
	//		
	//	}
	//	
		
//		private Map<String, Currency> updateCurrencyMap() {
//			Map<String, Currency> newCurrencyMap = new HashMap<>();
//			String filename = null;
//			
//			LocalDate date = transactions.stream()
//					.
//			
//			
//			newCurrencyMap = HQApp.readCurrencyConfigFile(filename);
//			
//			
//			return newCurrencyMap;
//		}
		
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

}
