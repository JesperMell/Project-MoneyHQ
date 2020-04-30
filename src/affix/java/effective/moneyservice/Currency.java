package affix.java.effective.moneyservice;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the currency class
 * 
 * @author Group Center
 *
 */
public class Currency {
	
	/**
	 * String holding the name for the currency
	 */
	private final String currencyCode;
	/**
	 * The exchange rate for the specific currency
	 */
	private final double exchangeRate;
	
	/**
	 * A logger object
	 */
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");
	
	
	/**
	 * @param currencyCode String holding the codename of a Currency
	 * @param rate double holding the value of the exchange rate to local currency
	 * @throws java.lang.IllegalArgumentException if 
	 *         * currency code is missing
	 *         * Exchange rate is negative
	 */
	public Currency(String currencyCode, double rate) {
		
		if(currencyCode == null || currencyCode.isEmpty()) {
			logger.log(Level.SEVERE, "CurrencyCode is empty or null! ");
			throw new IllegalArgumentException("Currency Code missing!");
		}
		else {
			if(rate < 0) {
				logger.log(Level.SEVERE, "Negative exchange rate! ");
				throw new IllegalArgumentException("Exchange rate cant be negative");
			}
		}
		
		this.currencyCode = currencyCode;
		this.exchangeRate = rate;
	}


	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}


	/**
	 * @return the rate
	 */
	public double getExchangeRate() {
		return exchangeRate;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		long temp;
		temp = Double.doubleToLongBits(exchangeRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Currency other = (Currency) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		if (Double.doubleToLongBits(exchangeRate) != Double.doubleToLongBits(other.exchangeRate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Currency [currencyCode=%s, exchange rate=%s]", currencyCode, exchangeRate);
	}
	
}
