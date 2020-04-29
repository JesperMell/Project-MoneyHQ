package affix.java.effective.moneyservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Site {
	
	private final String siteName;
	private List<Transaction> completedTransactions = new ArrayList<>();
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");
	
	
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
		logger.info("Entering readTransactions method -->");
		do {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(String.format("Reports/Report_CENTER_%s.ser", startDate)))) {
						((List<Transaction>) ois.readObject())
							.forEach((o) -> { completedTransactions.add(o); });
			} catch (IOException | ClassNotFoundException ioe) {
				logger.log(Level.WARNING, "Could not read file! " + ioe);
				System.out.println("Sorry, could read from file.");
			}
			startDate = startDate.plusDays(1);
		} while (!startDate.equals(endDate));
		logger.info("Exiting readTransaction method <--");
	}

}
