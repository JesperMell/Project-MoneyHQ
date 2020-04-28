package affix.java.effective.moneyservice;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestJUnitStatistic {

	private List<Transaction> testTransactionList = new ArrayList<>();
	
	@Before
	public void setUpTransactions() {
		
		testTransactionList.add(new Transaction("USD", 300, TransactionMode.BUY));
		testTransactionList.add(new Transaction("EUR", 150, TransactionMode.SELL));
		testTransactionList.add(new Transaction("EUR", 300, TransactionMode.SELL));
		testTransactionList.add(new Transaction("USD", 150, TransactionMode.BUY));
		testTransactionList.add(new Transaction("NOK", 500, TransactionMode.BUY));
		testTransactionList.add(new Transaction("GBP", 800, TransactionMode.SELL));
		testTransactionList.add(new Transaction("GBP", 150, TransactionMode.BUY));
		testTransactionList.add(new Transaction("RUB", 150, TransactionMode.SELL));
		testTransactionList.add(new Transaction("JPY", 400, TransactionMode.SELL));
		testTransactionList.add(new Transaction("EUR", 250, TransactionMode.SELL));
	}
	
	@Test
	public void testConstructor_1() {
		
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testTransactionList, currencyList, "TestSite");
		
		assertNotNull(testStats);
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptyTransList() {
		
		List<Transaction> testEmptyList = new ArrayList<>();
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testEmptyList, currencyList, "TestSite");
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptyCurrencyList() {
		
		List<Transaction> testEmptyList = new ArrayList<>();
		ArrayList<String> currencyList = new ArrayList<>();
		Statistic testStats = new Statistic(testTransactionList, currencyList, "TestSite");
	}
}
