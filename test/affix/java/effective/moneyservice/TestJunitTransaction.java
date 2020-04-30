package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJunitTransaction {

	private static Transaction t1 = null;
	
	@BeforeClass
	public static void setUp() {
		t1 = new Transaction("USD", 500, TransactionMode.SELL);
	}
	
	@Test
	public void testConstructor_1() {
		assertNotNull(t1);
	}
	
	@Test
	public void testAA1UniqueId_1() {
		Transaction t2 = new Transaction("JPY", 1000, TransactionMode.BUY);
		assertEquals(2, t2.getId());
	}
	
	
	@Test
	public void testGetId() {
		assertEquals(1, t1.getId());
	}
	
	@Test
	public void testGetAmount() {
		assertEquals(500, t1.getAmount());
	}
	
	@Test
	public void testGetCurrencyCode() {
		assertEquals("USD", t1.getCurrencyCode());
	}
	
	@Test
	public void testGetTransactionMode() {
		assertEquals(TransactionMode.SELL, t1.getMode());
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testMissingCurrencyCode_1() {
		Transaction t3 = new Transaction("", 1000, TransactionMode.SELL);
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testMissingCurrencyCode_2() {
		Transaction t3 = new Transaction(null, 1000, TransactionMode.SELL);
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testNegativeAmount() {
		Transaction t4 = new Transaction("USD", -100, TransactionMode.BUY);
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testZeroAmount() {
		Transaction t4 = new Transaction("USD", 0, TransactionMode.SELL);
	}
	
	@Test
	public void testEquals_1() {
		Transaction t5 = new Transaction("USD", 100, TransactionMode.SELL);
		Transaction t6 = new Transaction("USD", 100, TransactionMode.SELL);
		
		assertFalse(t5.equals(t6));
	}
	
	@Test
	public void testEquals_2() {
		Transaction t5 = new Transaction("USD", 100, TransactionMode.SELL);
		Transaction t6 = new Transaction("JPY", 100, TransactionMode.SELL);
		
		assertFalse(t5.equals(t6));
	}
	
	@Test
	public void testEquals_3() {
		Transaction t5 = new Transaction("USD", 100, TransactionMode.SELL);
		Transaction t6 = new Transaction("USD", 50, TransactionMode.SELL);
		
		assertFalse(t5.equals(t6));
	}
	
	@Test
	public void testEquals_4() {
		Transaction t5 = new Transaction("USD", 100, TransactionMode.SELL);
		Transaction t6 = new Transaction("USD", 100, TransactionMode.BUY);
		
		assertFalse(t5.equals(t6));
	}
	
	@Test
	public void testHashCode_1() {
		Transaction t5 = new Transaction("USD", 100, TransactionMode.SELL);
		Transaction t6 = new Transaction("USD", 100, TransactionMode.SELL);
		
		assertFalse(t5.hashCode() == t6.hashCode());
	}
	
	@Test
	public void testHashCode_2() {
		Transaction t5 = new Transaction("USD", 100, TransactionMode.SELL);
		Transaction t6 = new Transaction("USD", 100, TransactionMode.SELL);
		
		assertFalse(t5.hashCode() == t6.hashCode() && t5.equals(t6));
	}
	
	@Test
	public void testGetTimeStamp() {
		Transaction t1 = new Transaction("EUR", 300, TransactionMode.BUY);
		
		LocalDateTime temp = t1.getTimeStamp();
		LocalDate nowDate = LocalDate.now();
		
		assertTrue(nowDate.equals(temp.toLocalDate()));
	}
}
