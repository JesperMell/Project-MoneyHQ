package affix.java.effective.moneyservice;

public class Currency {
	
	private final String currencyCode;
	private final double exchangeRate;
	
	
	/**
	 * @param currencyCode String holding the codename of a Currency
	 * @param rate double holding the value of the exchange rate to local currency
	 */
	public Currency(String currencyCode, double rate) {
		
		if(currencyCode == null || currencyCode.isEmpty()) {
			throw new IllegalArgumentException("Currency Code missing!");
		}
		else {
			if(rate <= 0) {
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
