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
	
	/**
	 * A string holding the name of the specific site
	 */
	private final String siteName;
	/**
	 * A list holding a number of transaction objects
	 */
	private List<Transaction> completedTransactions = new ArrayList<>();
	/**
	 * A logger object
	 */
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");
	
	
	/**
	 * @param siteName - a String holding the site name, like "NORTH", "CENTER", "SOUTH" etc..
	 * @throws java.lang.IllegalArgumentException - if a site name is missing
	 */
	public Site(String siteName) {
		if(siteName == null || siteName.isEmpty()) {
			logger.log(Level.SEVERE, "Site name is null or empty! ");
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
		completedTransactions = new ArrayList<Transaction>();
		do {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(String.format("Reports/%s/Report_%s_%s.ser", siteName, siteName, startDate)))) {
						((List<Transaction>) ois.readObject())
							.forEach((o) -> { completedTransactions.add(o); });
        
			} catch (IOException ioe) {
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
