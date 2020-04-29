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
		if(siteName == null || siteName.isEmpty()) {
			throw new IllegalArgumentException("siteName missing!");
		}
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
	public void readTransactions(LocalDate startDate, LocalDate endDate) throws ClassNotFoundException {
		logger.info("Entering readTransactions method -->");
		do {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(String.format("Reports/Report_%s_%s.ser", siteName, startDate)))) {
						((List<Transaction>) ois.readObject())
							.forEach((o) -> { completedTransactions.add(o); });
        
			} catch (IOException ioe) {
				System.out.format("No report for %s\n", startDate);
				logger.log(Level.WARNING, "Could not read file! " + ioe);
			} catch (ClassNotFoundException ioe) {
				logger.log(Level.SEVERE, "Class missmatch exception! " + ioe);
				throw new ClassNotFoundException("Reading error, class missmatch" + ioe);
			}
			startDate = startDate.plusDays(1);
		} while (!startDate.equals(endDate));
		logger.info("Exiting readTransaction method <--");
	}
}
