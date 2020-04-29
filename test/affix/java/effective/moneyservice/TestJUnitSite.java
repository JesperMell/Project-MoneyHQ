package affix.java.effective.moneyservice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class TestJUnitSite {
	
	@Test
	public void testConstructor_1() {
		Site s1 = new Site("TestName");
		assertNotNull(s1);
	}
	
	@SuppressWarnings("unused")
	@Test (expected = IllegalArgumentException.class)
	public void testContstructor_2() {
		Site s1 = new Site("");
	}
	
	@Test
	public void testGetSiteName() {
		Site s1 = new Site("TestName");
		assertTrue("TestName".equals(s1.getSiteName()));
	}
	
	@Test
	public void testReadTransactions_1() {
		Site s1 = new Site("CENTER");
		
		LocalDate startDate = LocalDate.of(2020, 04, 01);
		LocalDate endDate = LocalDate.of(2020, 04, 02);
		
		try {
			s1.readTransactions(startDate, endDate);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		List<Transaction> testTrans = s1.getCompletedTransactions();
		
		assertTrue(testTrans.size() == 17);
	}
	
	@Test
	public void testReadTransactions_2() {
		Site s1 = new Site("CENTER");
		
		LocalDate startDate = LocalDate.of(2020, 04, 01);
		LocalDate endDate = LocalDate.of(2020, 04, 06);
		
		try {
			s1.readTransactions(startDate, endDate);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		List<Transaction> testTrans = s1.getCompletedTransactions();
		
		assertTrue(testTrans.size() == 55);
	}	
}
