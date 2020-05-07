package affix.java.effective.moneyservice;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is a support class for user interaction using CLI
 * 
 * @author Group Center
 *
 */
public class CLIHelper {
	
	static String[] displayTitles = {"Total Buy", "Total Sell", "Total Sell & Buy", "Profit"};

	/**
	 * Variable for keyboard input
	 */
	static Scanner input = new Scanner(System.in);

	/**
	 * The width of column when rendering output.
	 */
	private static int DISPLAY_COLUMN_WIDTH = 20;

	/**
	 * The main logger object.
	 */
	private final static Logger logger = Logger.getLogger("affix.java.effective.moneyservice");

	/**
	 * Alternatives for which period to use for calculating end date. In the menu
	 * input we choose a period type
	 * 
	 * @author jesper
	 *
	 */
	enum Period {
		/**
		 * Represents a single day.
		 */
		DAY,
		/**
		 * Represents a week.
		 */
		WEEK,
		/**
		 * Represents a month.
		 */
		MONTH
	};

	/**
	 * Alternatives for render output.
	 * 
	 * @author jesper
	 *
	 */
	enum DisplayOption {
		/**
		 * Limit to only render statistics.
		 */
		STATISTICS,
		/**
		 * Limit to only render transactions.
		 */
		TRANSACTIONS,
		/**
		 * Exit the option list.
		 */
		BACK
	};

	/**
	 * menuInput.
	 * 
	 * Main method for user to enter values.
	 * 
	 */
	static void menuInput() {
		logger.info("Entering menuInput method -->");
		// Set with the selected sites by user.
		Set<Site> sites;

		// The input for start of the period. (YYYY-MM-DD).
		Optional<LocalDate> startDay;

		// Selected period to use for computing the endDay.
		Optional<Period> periodOption;

		// The selected currencies
		List<String> currencies;

		// The selected alternative on what to render.
		Optional<DisplayOption> display_option;

		// Choose Site.
		do {
			sites = readSites();
		} while (sites.isEmpty());
		System.out.println("---");

		// Choose Period
		do {
			periodOption = readPeriod();
			logger.info("Exiting readPeriod method <--");
		} while (periodOption.isEmpty());
		System.out.println("---");

		// Choose Start Day.
		do {
			startDay = readStartDay();
			logger.info("Exiting readStartDay <--");
		} while (startDay.isEmpty());
		System.out.println("---");

		// Choose Currency.
		do {
			currencies = readCurrencyCodes();
		} while (currencies.isEmpty());
		System.out.println("---");

		// Choose Display Option.
		do {
			display_option = readDisplayOption();
			logger.info("Exiting readDisplayOption <--");
			if(display_option.isPresent() && display_option.get() != DisplayOption.BACK) {
				displayTable(sites, startDay.get(), periodOption.get(), currencies, display_option.get());
			}
		} while (display_option.get() != DisplayOption.BACK);
		System.out.println("---");


		// Read Currency map again.
		HQApp.currencyMap = HQApp.readCurrencyConfigFile("ExchangeRates/CurrencyConfig_Default_Accepted.txt");
		
	}
	
	private static void displayTable(Set<Site> sites, LocalDate sDay, Period period, List<String> currencies, DisplayOption displayOpt) {
		// Compute the endDay.
		LocalDate endDay = createEndDay(period, sDay);

		// Create Statistics
		List<Statistic> statistics = new ArrayList<>();

		for (Site s : sites) {
			try {
				s.readTransactions(sDay, endDay);
			} catch (ClassNotFoundException e1) {
				logger.log(Level.SEVERE, "Site exception! " + e1);
				System.out.println("Something went wrong!");
			}
			
			if(s.getCompletedTransactions().isEmpty()) {
				System.out.println(String.format("There was no transactions to read from %s.", s.getSiteName()));
				continue;
			}

			try {
				statistics.add(new Statistic(s.getCompletedTransactions(), currencies, s.getSiteName()));
			} catch (IllegalArgumentException e) {
				logger.log(Level.WARNING, "Statistics exception! " + e);
				System.out.println(
						String.format("%s does not have any transactions and won't be included", s.getSiteName()));
			}
		}

		if (statistics.isEmpty()) {
			System.out.println("No statistics were generated");
			return;
		}

		// Create and set StatDay for each day.
		// Fill them with corresponding values from statistics list.
		List<StatisticData> result = new ArrayList<>();

		for (Statistic s : statistics) {
			for (LocalDate ld = sDay; !ld.equals(endDay); ld = ld.plusDays(1)) {
				if(ld.getDayOfWeek() == DayOfWeek.SATURDAY || ld.getDayOfWeek() == DayOfWeek.SUNDAY) {
					continue;
				}
				try {
				StatisticData temp = new StatisticData();
				temp.setSite(s.getSiteName());
				temp.putToData("Profit", s.getProfit(ld.toString()));
				temp.putToData("Total Buy", s.getTotalAmountBuy(ld.toString()));
				temp.putToData("Total Sell", s.getTotalAmountSell(ld.toString()));
				temp.putToData("Total Sell & Buy", s.getTotalAmount(ld.toString()));

				result.add(temp);
				} catch(NullPointerException e) {
					
				}
			}

			// Show Transactions.
			if (displayOpt.equals(DisplayOption.TRANSACTIONS)) {
				System.out.println(String.format("----- Transactions for %s -----", s.getSiteName()));
				System.out.println(headDisplayer(new String[] {"ID", "TYPE", "AMOUNT", "CURRENCY", "TIMESTAMP"}));
				s.getTransactions().stream().filter(currencyFilter(currencies)).forEach(t -> {
					System.out.println(headDisplayer(new String[] {String.valueOf(t.getId()) + "",
							t.getMode().toString(), String.valueOf(t.getAmount()), t.getCurrencyCode(),
							DateTimeFormatter.ofPattern("YYYY-MM-dd h:m:s").format(t.getTimeStamp())}));
				});
			}
		}

		// Show Statistics.
		// For each day
		if (displayOpt.equals(DisplayOption.STATISTICS)) {
			
			// For total days
			result.stream().collect(Collectors.groupingBy(StatisticData::getSite)).forEach(showAllSites(currencies));
			
			// Display Statistics for all sites.
			if (sites.size() > 1) {
				System.out.println("\n----- ALL -----");
				
				StatisticData total = result.stream().reduce(new StatisticData(), (t, elem) -> t.mergeData(elem));
				System.out.println(headDisplayer(displayTitles));
				System.out.println(rowDisplayer(total, "SEK", currencies));
			}
		}
		System.out.println("\n----- END -----");
				
	}
	
	private static BiConsumer<String, List<StatisticData>> showAllSites(List<String> currencies) {
		return (k1, v1) -> {
			System.out.println(String.format("\n----- %s -----", k1));
			StatisticData total = v1.stream().reduce(new StatisticData(), (t, elem) -> t.mergeData(elem));
			System.out.println(headDisplayer(displayTitles));
			System.out.println(rowDisplayer(total, "SEK", currencies));
		};
	}

	/**
	 * Predicate for filtering transaction by list of currencies.
	 * 
	 * @param currencies - A list holding currency codes
	 * @return lambda predicate.
	 */
	private static Predicate<Transaction> currencyFilter(List<String> currencies) {
		return t -> currencies.contains(t.getCurrencyCode());
	}

	/**
	 * readSites.
	 * 
	 * Display menu for selecting sites. The user can select 1 to n sites, choose
	 * 'ALL' option to select all sites.
	 * 
	 * Returns a set with the selected site names.
	 * 
	 * @return Set{@code <Site>} - a Set with the selected site names
	 */
	private static Set<Site> readSites() {
		logger.info("Entering readSites method -->");
		System.out.println("Choose a Site (For multiple choices use comma seperation)");

		List<Site> sites = new ArrayList<>();
		sites.addAll(HQApp.sites.values());

		// Print the options.
		int i = 0;
		for (Site site : sites) {
			System.out.println(String.format("%d: %s", ++i, site.getSiteName()));
		}
		System.out.println(String.format("%d: %s", ++i, "ALL"));
		System.out.print("Enter your choice: ");

		// The TreeList where the selected sites
		// should be appended to.
		Set<Site> result = new HashSet<>();
		try {
			for (String data : input.next().split(",")) {
				int index = Integer.parseInt(data.trim());
				// if i == index, then the 'ALL' option is selected.
				// All sites should then be returned, else append selected
				// site to the result TreeSet.
				if (i == index)
					return new HashSet<>(HQApp.sites.values());

				result.add(sites.get(index - 1));
			}
		} catch (IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE, "input exception! " + e);
			System.out.println("Wrong input.");
			return new HashSet<>();
		} catch (InputMismatchException e) {
			logger.log(Level.SEVERE, "input exception! " + e);
			System.out.println("Wrong input.");
			return new HashSet<>();
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "input exception! " + e);
			System.out.println("Wrong input.");
			return new HashSet<>();
		}

		System.out.println("Site selected: ");
		result.forEach((s) -> System.out.println(s.getSiteName()));

		logger.info("Exiting readSites method <--");
		return result;
	}

	/**
	 * readDisplayOption.
	 * 
	 * Display menu for entering what the output should present.
	 * 
	 * @return Optional {@code <DisplayOption>} 
	 */
	private static Optional<DisplayOption> readDisplayOption() {
		logger.info("Entering readDisplayOption -->");
		System.out.println("Enter what to present:");
		int i = 0;
		for (DisplayOption d : DisplayOption.values()) {
			System.out.println(String.format("%d: %s", ++i, d));
		}
		System.out.print("Enter your choice: ");

		try {
			return Optional.of(DisplayOption.values()[input.nextInt() - 1]);
		} catch (IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE, "Display Option exception! " + e);
			System.out.println("Invalid option, try again!");
			return Optional.empty();
		}
	}

	/**
	 * readStartDate.
	 * 
	 * Display menu for entering start date.
	 * 
	 * @return Optional {@code <LocalDate>} - a LocalDate in the format YYYY-MM-DD if
	 *         available
	 */
	private static Optional<LocalDate> readStartDay() {
		logger.info("Entering readStartDay -->");
		System.out.println("Enter start day of Period");
		System.out.print("Enter (YYYY-MM-DD): ");
		try {
			return Optional.of(LocalDate.parse(input.next()));
		} catch (DateTimeParseException e) {
			logger.log(Level.SEVERE, "time input exception! " + e);
			System.out.println("Invalid format, try again");
			return Optional.empty();
		}
	}

	/**
	 * readPeriod.
	 * 
	 * Display menu for entering Period.
	 * 
	 * @return Optional {@code<Period>} - a Period if available
	 */
	private static Optional<Period> readPeriod() {
		logger.info("Entering readPeriod method -->");
		int i = 0;
		System.out.println("Choose a Period");
		for (Period p : Period.values()) {
			System.out.println(String.format("%d: %s", ++i, p));
		}

		System.out.print("Enter your choice:");
		try {
			int data = input.nextInt();
			return Optional.of(Period.values()[data - 1]);
		} catch (IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE, "input exception! " + e);
			System.out.println("Wrong input.");
			return Optional.empty();
		} catch (InputMismatchException e) {
			input.next();
			logger.log(Level.SEVERE, "input exception! " + e);
			System.out.println("Wrong input.");
			return Optional.empty();
		}
	}

	/**
	 * readCurrencyCodes.
	 * 
	 * Display menu for selecting currencies.
	 * 
	 * @return Optional {@code<String>} - a String with currency code if available
	 */
	private static List<String> readCurrencyCodes() {
		logger.info("Entering readCurrencyCodes method -->");
		System.out.println("Choose currencies (Use comma as separator)");
		HQApp.currencyMap.keySet().forEach((x) -> System.out.print(x + " "));
		System.out.println("ALL");
		System.out.print("Enter your choice (E.g. EUR,SEK): ");
		String data = input.next();

		List<String> currencies = new ArrayList<>();

		if (data.equals("ALL")) {
			return new ArrayList<String>(HQApp.currencyMap.keySet());
		}

		for (String code : data.split(",")) {
			if (HQApp.currencyMap.get(code) != null)
				currencies.add(code);
		}

		logger.info("Exiting readCurrencyCodes method <--");
		return currencies;

	}

	/**
	 * createEndDay.
	 * 
	 * Calculates the endDate for startDate and Period.
	 * 
	 * @param periodOption - an enum type
	 * @param startDay     - a start date in the format YYYY-MM-DD
	 * @return LocalDate - an end date in the format YYYY-MM-DD
	 */
	static LocalDate createEndDay(Period periodOption, LocalDate startDay) {
		switch (periodOption) {
		case DAY:
			return startDay.plusDays(1);
		case WEEK:
			return startDay.plusWeeks(1);
		case MONTH:
			return startDay.plusMonths(1);
		}
		return startDay;
	}

	/**
	 * Generates a string representing a row. Adds spaces as padding and a '|' as
	 * separator.
	 * 
	 * @param titles - A list of string to fill the column with.
	 * @return String
	 */

	static String headDisplayer(String[] titles) {
		StringBuilder row = new StringBuilder();
		for(String s : titles) {
			StringBuilder sb = new StringBuilder();
			sb.append(s);
			IntStream.range(0, DISPLAY_COLUMN_WIDTH - sb.length()).forEachOrdered(n -> {
				sb.append(" ");
			});
			sb.append("|");
			row.append(sb);
	}
		return row.toString();
	}

	/**
	 * Generates a string representing a table. Adds spaces as padding, '|' as
	 * separator and NewLine character for new line (\n).
	 * 
	 * @param list - List of Maps. The First key of each map will render as first in
	 *             each column, which becomes a row. The second key will be the
	 *             second line in each column, which is the second row, and so on.
	 * @param prefix - String to prepend to column value.
	 * 
	 * @return String
	 */
	static String rowDisplayer(StatisticData data, String prefix, List<String> currencies) {
		StringBuilder table = new StringBuilder();
		Integer value;
		// Get the currency list.
		for(String currency : currencies) {
			StringBuilder row = new StringBuilder();
			for(String title : displayTitles) {
				StringBuilder column = new StringBuilder();
				value = data.getData().get(title).get(currency);
				column.append(String.format("%s: %d %s", currency, value, prefix));
				IntStream.range(0, DISPLAY_COLUMN_WIDTH - column.length()).forEachOrdered(n -> {
					column.append(" ");
				});
				column.append("|");
				row.append(column);
			}
			table.append(row + "\n");
		}

		return table.toString();
	}
}

/**
 * Container for data of transactions for a day.
 * 
 * @author jesper
 *
 */
class StatisticData {
	private Map<String, Map<String, Integer>> data = new HashMap<>();
	
	private String site;

	/**
	 * The date the transactions occurred (Transaction.getTimeStamp).
	 */
	private LocalDate date;

	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public Map<String, Map<String, Integer>> getData() {
		return data;
	}
	
	public StatisticData mergeData(StatisticData other) {
		other.getData().forEach((k1, v1) -> {
			v1.forEach((k2, v2) -> {
					this.data.putIfAbsent(k1, new HashMap<String, Integer>());
					this.data.get(k1).merge(k2, v2, Integer::sum);
			});
		});
		return this;
	}
	
	public void putToData(String key, Map<String, Integer> value) {
		this.data.put(key, value);
	}

}
