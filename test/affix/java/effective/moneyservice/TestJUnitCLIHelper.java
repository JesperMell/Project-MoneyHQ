package affix.java.effective.moneyservice;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestJUnitCLIHelper {

	/**
	 * Should return a row with correct amount of padding.
	 */
	@Test
	public void testHeadDisplay1() {
		String row = CLIHelper.headDisplayer(Arrays.asList("Test1", "Test2"));
		assertEquals(row, "Test1               |Test2               |");
	}
	
	/**
	 * Should return a table with correct amount of padding and rows.
	 */
	@Test
	public void testRowDisplay1() {
		Map<String, Integer> l1 = new HashMap<>();
		Map<String, Integer> l2 = new HashMap<>();
		l1.put("A", 1);
		l1.put("B", 2);
		
		l2.put("A", 3);
		l2.put("B", 4);
			
		String row = CLIHelper.rowDisplayer(Arrays.asList(l1, l2), "");
		assertEquals(row, "A: 1                |A: 3                |\nB: 2                |B: 4                |\n");
	}

}
