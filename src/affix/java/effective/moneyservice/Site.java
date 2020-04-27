package affix.java.effective.moneyservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

	@SuppressWarnings("unchecked")
	public void readTransactions(LocalDate startDate, LocalDate endDate) {
		do {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(String.format("Reports/Report_CENTER_%s.ser", startDate)))) {
						((List<Transaction>) ois.readObject())
							.forEach((o) -> { completedTransactions.add(o); });
			} catch (IOException | ClassNotFoundException ioe) {
				System.out.println("Sorry, could read from file.");
			}
			startDate = startDate.plusDays(1);
		} while (!startDate.equals(endDate));
	}

}
