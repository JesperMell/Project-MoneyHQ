package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJUnitStatistic {

	private List<Transaction> testTransactionList = new ArrayList<>();
	private List<String> testCurrencyCodes = new ArrayList<>();
	
	@BeforeClass
	public static void setUp() {
		HQApp.currencyMap = HQApp.readCurrencyConfigFile("ExchangeRates/CurrencyConfig_2020-04-01.txt");
	}
	
	@Before
	public void setUpLists() {
		
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
		
		testCurrencyCodes.add("AUD");
		testCurrencyCodes.add("CHF");
		testCurrencyCodes.add("CNY");
		testCurrencyCodes.add("DKK");
		testCurrencyCodes.add("EUR");
		testCurrencyCodes.add("GBP");
		testCurrencyCodes.add("INR");
		testCurrencyCodes.add("JPY");
		testCurrencyCodes.add("NOK");
		testCurrencyCodes.add("RUB");
		testCurrencyCodes.add("USD");
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
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptySiteName1() {
		
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testTransactionList, currencyList, "");
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptySiteName2() {
		
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testTransactionList, currencyList, null);
	}
	
	@Test
	public void testGetTransaction() {
		
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testTransactionList, currencyList, "TestSite");
		
		List<Transaction> transactions = testStats.getTransactions();
		
		assertEquals(10, transactions.size());
	}
	
	@Test
	public void testGetCurrencyCodes() {
		
		Statistic testStats = new Statistic(testTransactionList, testCurrencyCodes, "TestSite");
		List<String> temp = testStats.getCurrencyCodes();
		
		assertEquals("USD", temp.get(10));
	}
	
	@Test
	public void testGetSiteName() {
		
		Statistic testStats = new Statistic(testTransactionList, testCurrencyCodes, "TestSite");
		
		assertEquals("TestSite", testStats.getSiteName());
	}
	
	
	
}
