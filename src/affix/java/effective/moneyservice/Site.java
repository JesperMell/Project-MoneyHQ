package affix.java.effective.moneyservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Site {
	
	private final String siteName;
	private List<Transaction> completedTransactions = new ArrayList<>();
	
	
	/**
	 * @param siteName
	 * @param completedTransactions
	 */
	public Site(String siteName) {
		this.siteName = siteName;
	}


	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}


	/**
	 * @return the completedTransactions
	 */
	public List<Transaction> getCompletedTransactions() {
		return completedTransactions;
	}

	
	public void readTransactions(LocalDate startDate, LocalDate endDate) {
		
	}
	
}
