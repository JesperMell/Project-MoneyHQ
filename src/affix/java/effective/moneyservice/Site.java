package affix.java.effective.moneyservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Site creates objects with a site name and their transactions
 * 
 * @author Group Center
 */
public class Site {
	
	private final String siteName;
	private List<Transaction> completedTransactions = new ArrayList<>();
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");
	
	
	/**
	 * @param siteName - a String holding the site name, like "NORTH", "CENTER", "SOUTH" etc..
	 * @throws java.lang.IllegalArgumentException - if a site name is missing
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

	/**
	 * Read all transactions between two dates.
	 * @param startDate - a start date in the format YYYY-MM-DD
	 * @param endDate - an end date in the format YYYY-MM-DD
	 * @throws ClassNotFoundException - if the startDate and EndDate is missing or not on the format YYYY-MM-DD  
	 */
	@SuppressWarnings("unchecked")
	public void readTransactions(LocalDate startDate, LocalDate endDate) throws ClassNotFoundException {
		logger.info("Entering readTransactions method -->");
		do {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(String.format("Reports/Report_CENTER_%s.ser", startDate)))) {
						((List<Transaction>) ois.readObject())
							.forEach((o) -> { completedTransactions.add(o); });
        
			} catch (IOException ioe) {
				System.out.format("No report for %s\n", startDate);
        logger.log(Level.WARNING, "Could not read file! " + ioe);
			} catch (ClassNotFoundException ioe) {
				throw new ClassNotFoundException("Reading error, class missmatch" + ioe);
			}
			startDate = startDate.plusDays(1);
		} while (!startDate.equals(endDate));
		logger.info("Exiting readTransaction method <--");
	}
}
