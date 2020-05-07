package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;


public class TestJUnitStatisticData {

	private static StatisticData s1 = null;
	
	@BeforeClass
	public static void setUp() {
		s1 = new StatisticData();
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(s1);
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
