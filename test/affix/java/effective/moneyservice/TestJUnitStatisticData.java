package affix.java.effective.moneyservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;


public class TestJUnitStatisticData {

	private static StatisticData s1 = null;
	
	@BeforeClass
	public static void setUp() {
		s1 = new StatisticData();
		s1.setSite("TestSite");
		s1.setDate(LocalDate.of(2020, 04, 01));
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
	
	@Test
	public void initializeDataFromStatistics() {
		Set<Site> sites = new HashSet<>();
		sites.add(new Site("CENTER"));
		
		LocalDate sDay = LocalDate.of(2020, 04, 20);
		LocalDate endDay = sDay.plusDays(1);
		List<String> currencies = new ArrayList<>();
		currencies.add("EUR");
		
		List<Statistic> statistics = Statistic.initializeFromSites(sites, sDay, endDay, currencies);

		List<StatisticData> result = StatisticData.initializeDataFromStatistics(statistics, sDay, endDay);

		int expected = result.get(0).getData().get("Total Buy").get("EUR");
		assertEquals(expected, 8112);
	}
}
