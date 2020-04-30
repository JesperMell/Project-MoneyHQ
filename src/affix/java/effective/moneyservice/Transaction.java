package affix.java.effective.moneyservice;

import java.time.LocalDateTime;

/**
 * Transaction is a value type to be used in the class Statistic
 * 
 * @author Group Center
 */
public class Transaction implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private final int id;
	private final String currencyCode;
	private final int amount;
	private final TransactionMode mode;
	private LocalDateTime timeStamp;
	
	private static int uniqueId = 0;
	
	/**
	 * Constructor
	 * @param currencyCode - a list of currency codes
	 * @param amount - an int holding the validated order amount
	 * @param mode - a TransactionMode holding the type of the validated order
	 * @throws java.lang.IllegalArgumentException if currency code is missing or the amount is too low
	 */
	public Transaction(String currencyCode, int amount, TransactionMode mode) {
		this(currencyCode, amount, mode, ++uniqueId);
	}

	public Transaction(String currencyCode, int amount, TransactionMode mode, int id) {
		if(currencyCode == null || currencyCode.isEmpty()) {
			throw new IllegalArgumentException("currencyCode missing!");
		}
		else{
			if(amount < 50) {
				throw new IllegalArgumentException("Amount too low!");
			}
		}

		this.currencyCode = currencyCode;
		this.amount = amount;

		this.mode = mode;
		timeStamp = LocalDateTime.now();
		
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return timeStamp;
	}
	/**
	 * @return the timeStamp
	 */
	public LocalDateTime getTimeStamp() {
		return timeStamp;

	}

	public int getId() {
		return id;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public int getAmount() {
		return amount;
	}

	public TransactionMode getMode() {
		return mode;
	}

	@Override
	public String toString() {
		return String.format("Transaction [id=%s, currencyCode=%s, amount=%s, mode=%s, timeStamp=%s]", id, currencyCode,
				amount, mode, timeStamp);
	}
	
	
}
