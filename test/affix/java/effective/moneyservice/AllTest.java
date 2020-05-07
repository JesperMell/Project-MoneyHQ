package affix.java.effective.moneyservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{
			TestJUnitSite.class,
			TestJunitTransaction.class,
			TestJUnitCurrency.class,
			TestJUnitStatistic.class,
			TestJUnitStatisticData.class,
			TestJUnitCLIHelper.class
		}
		)
public class AllTest {;}