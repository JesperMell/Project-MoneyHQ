package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJUnitCurrency {
	
	private static Currency c1 = null;
	
	@BeforeClass
	public static void setUp() {
		c1 = new Currency("JPY", 9.2693);
	}
	
	
	@Test
	public void testConstructor_1() {
		Currency c2 = new Currency("USD", 10.237);
		assertNotNull(c2);
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testMissingCurrencyCode1() {
		Currency c3 = new Currency("", 10.237);
	}
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testMissingCurrencyCode2() {
		Currency c3 = new Currency(null, 10.237);
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testNegaiveExchangeRate() {
		Currency c3 = new Currency("USD", -10.237);
	}
	
	@Test
	public void testToString_1() {
		Currency c2 = new Currency("USD", 10.237);
		assertEquals("Currency [currencyCode=USD, exchange rate=10.237]", c2.toString());
	}
	
	@Test
	public void testGetCurrencyCode() {
		assertEquals("JPY", c1.getCurrencyCode());
	}
	
	@Test
	public void testGetExchangeRate() {
		assertEquals(9.2693, c1.getExchangeRate(), 0.0001);
	}
	
	@Test
	public void testEquals_1() {
		Currency c3 = new Currency("USD", 10.237);
		Currency c4 = new Currency("USD", 10.237);
		
		assertTrue(c3.equals(c4));
	}
	
	@Test
	public void testEquals_2() {
		Currency c3 = new Currency("USD", 10.237);
		Currency c4 = new Currency("JPY", 10.237);
		
		assertFalse(c3.equals(c4));
	}
	
	@Test
	public void testEquals_3() {
		Currency c3 = new Currency("USD", 10.237);
		Currency c4 = new Currency("USD", 9.237);
		
		assertFalse(c3.equals(c4));
	}
	
	@Test
	public void testHascode_1() {
		Currency c2 = new Currency("JPY", 9.2693);
		
		assertTrue(c1.hashCode() == c2.hashCode());
	}
	
	@Test
	public void testHashCodeAndEquals() {
		Currency c2 = new Currency("JPY", 9.2693);
		
		assertTrue(c1.hashCode() == c2.hashCode() && c1.equals(c2));
	}

}
