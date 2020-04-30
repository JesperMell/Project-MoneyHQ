package affix.java.effective.moneyservice;

import java.time.LocalDateTime;

/**
 * Transaction is a class for storing a completed transaction and what day and time it was done
 * 
 * @author Group Center
 *
 */

public class Transaction implements java.io.Serializable{
	
	/**
	 * long for marking serialization version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A unique identification value
	 */
	private final int id;
	/**
	 * currencyCode - String holding which currency the Transaction was done in
	 */
	private final String currencyCode;
	/**
	 * amount - a int holding information of the amount in the completed transaction
	 */
	private final int amount;
	
	/**
	 * mode - a TransactionMode defining if its BUY or SELL order
	 */
	private final TransactionMode mode;
	
	/**
	 * timeStamp - LocalDateTime information set when the transaction were completed
	 */
	private LocalDateTime timeStamp;
	
	/**
	 * uniqueId - a int for generation of unique orderNr
	 */
	private static int uniqueId = 0;
	
	/**
	 * Constructor
	 * @param currencyCode - String specifying the currency the transaction was done in
	 * @param amount - an Int specifying the amount that was ordered
	 * @param mode - a TransactionMode holding the mode of the order
	 * @throws IllegalArgumentException if currency code is missing,
	 * or if the amount is'nt complying with the configured min amount
	 * or if the ordered amount is 0 or negative
	 */
	
	public Transaction(String currencyCode, int amount, TransactionMode mode) {
		if(currencyCode == null || currencyCode.isEmpty()) {
			throw new IllegalArgumentException("currencyCode missing!");
		}
		else{
			if(!(amount%HQApp.amountLimit == 0)) {
				throw new IllegalArgumentException("Amount could not be accepted, has to be muplicit of configured amount");
			}
			else {
				if(amount <= 0) {
					throw new IllegalArgumentException("Ordered amount cant be negative or 0");
				}
			}
		}

		this.currencyCode = currencyCode;
		this.amount = amount;

		this.mode = mode;
		timeStamp = LocalDateTime.now();
		
		this.id = ++uniqueId;
	}


	/**
	 * @return the timeStamp
	 */
	public LocalDateTime getTimeStamp() {
		return timeStamp;

	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @return the mode
	 */
	public TransactionMode getMode() {
		return mode;
	}
	
	@Override
	public String toString() {
		return String.format("Transaction [id=%s, currencyCode=%s, amount=%s, mode=%s, timeStamp=%s]", id, currencyCode,
				amount, mode, timeStamp);
	}
}
