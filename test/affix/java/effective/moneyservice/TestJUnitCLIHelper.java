package affix.java.effective.moneyservice;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class TestJUnitCLIHelper {

	/**
	 * Should return a row with correct amount of padding.
	 */
	@Test
	public void testHeadDisplay1() {
		String row = CLIHelper.headDisplayer(new String[] {"Test1", "Test2"});
		assertEquals(row, "Test1               |Test2               |");
	}
	
	/**
	 * Should return a table with correct amount of padding and rows.
	 */
	@Deprecated
	public void testRowDisplay1() {
		StatisticData data = new StatisticData();
		Map<String, Integer> d1 = new HashMap<>();
		Map<String, Integer> d2 = new HashMap<>();
		
		d1.put("A", 1);
		d1.put("B", 2);
		
		d2.put("A", 3);
		d2.put("B", 4);
		
		data.putToData("A", d1);
		data.putToData("B", d2);
		
		String row = CLIHelper.rowDisplayer(data, "", Arrays.asList("A", "B"));
		assertEquals(row, "A: 1                |A: 3                |\nB: 2                |B: 4                |\n");
	}

}
