package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;


public class TestJUnitStatDay {

	private static StatDay s1 = null;
	
	@BeforeClass
	public static void setUp() {
		s1 = new StatDay("TestSite", LocalDate.of(2020, 04, 01));
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(s1);
	}
	
	@Test
	public void testSetGetProfit() {
		
		Map<String, Integer> profitMap = new HashMap<>();
		profitMap.putIfAbsent("EUR", 100);
		profitMap.putIfAbsent("USD", 200);
		
		s1.setProfit(profitMap);
		assertTrue(s1.getProfit().get("EUR") == 100);
	}
	
	@Test
	public void testSetGetAmountBuy() {
		
		Map<String, Integer> profitMap = new HashMap<>();
		profitMap.putIfAbsent("EUR", 100);
		profitMap.putIfAbsent("USD", 200);
		
		s1.setAmountBuy(profitMap);
		assertTrue(s1.getAmountBuy().get("USD") == 200);
	}
	
	@Test
	public void testSetGetAmountSell() {
		
		Map<String, Integer> profitMap = new HashMap<>();
		profitMap.putIfAbsent("JPY", 400);
		profitMap.putIfAbsent("USD", 200);
		
		s1.setAmountSell(profitMap);
		assertTrue(s1.getAmountSell().get("JPY") == 400);
	}
	
	@Test
	public void testGetSetTotal() {
		
		Map<String, Integer> profitMap = new HashMap<>();
		profitMap.putIfAbsent("JPY", 400);
		profitMap.putIfAbsent("USD", 300);
		
		s1.setTotal(profitMap);
		assertTrue(s1.getTotal().get("USD") == 300);
	}
	
	@Test
	public void testGetSite() {
		assertEquals("TestSite", s1.getSite());
	}
	
	@Test
	public void testSetSite() {
		
		s1.setSite("setSite");
		assertEquals("setSite", s1.getSite());
	}
	
	@Test
	public void testGetDate() {
		assertEquals(LocalDate.of(2020, 04, 01), s1.getDate());
	}
	
	
	@Test
	public void testSetDate() {
		s1.setDate(LocalDate.of(2020, 05, 30));
		assertEquals(LocalDate.of(2020, 05, 30), s1.getDate());
	}
}
