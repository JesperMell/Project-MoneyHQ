package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJUnitStatistic {

	private List<Transaction> testTransactionList = new ArrayList<>();
	private List<String> testCurrencyCodes = new ArrayList<>();
	private Map<String, Currency> testCurrencyMap = new HashMap<>();
	private static List<Transaction> testTrans = new ArrayList<>();

	@BeforeClass
	public static void setUp() {
		HQApp.currencyMap = HQApp.readCurrencyConfigFile("ExchangeRates/CurrencyConfig_2020-04-01.txt");
		LocalDate startDate = LocalDate.of(2020, 04, 01);
		LocalDate endDate = LocalDate.of(2020, 04, 03);
		
		Site testSite = new Site("TestSite");
		
		testSite.readTransactions(startDate, endDate);
		testTrans = testSite.getCompletedTransactions();
		
		testTrans.forEach(System.out::println);
	}

	@Before
	public void setUpLists() {


		testTransactionList.add(new Transaction("USD", 300, TransactionMode.BUY));
		testTransactionList.add(new Transaction("USD", 150, TransactionMode.SELL));
		testTransactionList.add(new Transaction("EUR", 150, TransactionMode.SELL));
		testTransactionList.add(new Transaction("EUR", 300, TransactionMode.SELL));
		testTransactionList.add(new Transaction("EUR", 250, TransactionMode.SELL));
		testTransactionList.add(new Transaction("NOK", 500, TransactionMode.BUY));
		testTransactionList.add(new Transaction("GBP", 800, TransactionMode.SELL));
		testTransactionList.add(new Transaction("GBP", 150, TransactionMode.BUY));
		testTransactionList.add(new Transaction("RUB", 150, TransactionMode.SELL));
		testTransactionList.add(new Transaction("JPY", 400, TransactionMode.SELL));

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

		testCurrencyMap.putIfAbsent("AUD", new Currency("AUD", 6.0));
		testCurrencyMap.putIfAbsent("CHF", new Currency("CHF", 10.0));
		testCurrencyMap.putIfAbsent("CNY", new Currency("CNY", 1.0));
		testCurrencyMap.putIfAbsent("DKK", new Currency("DKK", 2.0));
		testCurrencyMap.putIfAbsent("EUR", new Currency("EUR", 11.0));
		testCurrencyMap.putIfAbsent("GBP", new Currency("GBP", 12.0));
		testCurrencyMap.putIfAbsent("INR", new Currency("INR", 0.2));
		testCurrencyMap.putIfAbsent("JPY", new Currency("JPY", 0.1));
		testCurrencyMap.putIfAbsent("NOK", new Currency("NOK", 1.0));
		testCurrencyMap.putIfAbsent("RUB", new Currency("RUB", 0.1));
		testCurrencyMap.putIfAbsent("USD", new Currency("USD", 10.0));
	}

	@Test
	public void testConstructor_1() {

		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testTransactionList, currencyList, "TestSite");

		assertNotNull(testStats);
	}

	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptyTransList1() {

		List<Transaction> testEmptyList = new ArrayList<>();
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(testEmptyList, currencyList, "TestSite");
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptyTransList2() {

		List<Transaction> testEmptyList = new ArrayList<>();
		ArrayList<String> currencyList = new ArrayList<String>(HQApp.currencyMap.keySet());
		Statistic testStats = new Statistic(null, currencyList, "TestSite");
	}

	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptyCurrencyList1() {

		List<Transaction> testEmptyList = new ArrayList<>();
		ArrayList<String> currencyList = new ArrayList<>();
		Statistic testStats = new Statistic(testTransactionList, currencyList, "TestSite");
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorEmptyCurrencyList2() {

		List<Transaction> testEmptyList = new ArrayList<>();
		ArrayList<String> currencyList = new ArrayList<>();
		Statistic testStats = new Statistic(testTransactionList, null, "TestSite");
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

	@Test
	public void testGetTotalAmount1() {
		Statistic testStats = new Statistic(testTrans, testCurrencyCodes, "TestSite");

		Map<String, Integer> resultMap = testStats.getTotalAmount("2020-04-01");

		assertTrue(-4778 == resultMap.get("GBP"));
	}
}
