package affix.java.effective.moneyservice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
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
		 * Render both statistics and transactions.
		 */
		BOTH
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
		} while (display_option.isEmpty());
		System.out.println("---");

		// Compute the endDay.
		LocalDate endDay = createEndDay(periodOption, startDay);

		// Create Statistics
		List<Statistic> statistics = new ArrayList<>();

		for (Site s : sites) {
			try {
				s.readTransactions(startDay.get(), endDay);
			} catch (ClassNotFoundException e1) {
				logger.log(Level.SEVERE, "Site exception! " + e1);
				System.out.println("Something went wrong!");
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
		List<StatDay> result = new ArrayList<>();

		for (Statistic s : statistics) {
			for (LocalDate l = startDay.get(); !l.equals(endDay); l = l.plusDays(1)) {
				StatDay stat = new StatDay(s.getSiteName(), l);
				stat.setProfit(s.getProfit(l.toString()));
				stat.setAmountBuy(s.getTotalAmountBuy(l.toString()));
				stat.setAmountSell(s.getTotalAmountSell(l.toString()));
				stat.setTotal(s.getTotalAmount(l.toString()));

				result.add(stat);
			}

			// Show Transactions.
			if (display_option.get().equals(DisplayOption.TRANSACTIONS)
					|| display_option.get().equals(DisplayOption.BOTH)) {
				System.out.println(String.format("----- Transactions for %s -----", s.getSiteName()));
				System.out.println(headDisplayer(Arrays.asList("ID", "TYPE", "AMOUNT", "CURRENCY", "TIMESTAMP")));
				s.getTransactions().stream().filter(currencyFilter(currencies)).forEach(t -> {
					System.out.println(headDisplayer(Arrays.asList(String.valueOf(t.getId()) + "",
							t.getMode().toString(), String.valueOf(t.getAmount()), t.getCurrencyCode(),
							DateTimeFormatter.ofPattern("YYYY-MM-dd h:m:s").format(t.getTimeStamp()))));
				});
			}
		}

		// Show Statistics.
		if (display_option.get().equals(DisplayOption.STATISTICS) || display_option.get().equals(DisplayOption.BOTH)) {
			result.stream().collect(Collectors.groupingBy(StatDay::getSite)).forEach((k, v) -> {
				// Maps for calculating "total for all days" row.
				Map<String, Integer> profit = new HashMap<>();
				Map<String, Integer> amountBuy = new HashMap<>();
				Map<String, Integer> amountSell = new HashMap<>();
				Map<String, Integer> total = new HashMap<>();

				System.out.println(String.format("\n----- %s -----", k));
				v.forEach((s) -> {

					System.out.println("\n" + s.getDate());
					System.out.println(
							headDisplayer(Arrays.asList("Profit", "Total Buy", "Total Sell", "Total Buy & Sell")));
					System.out.println(rowDisplayer(
							Arrays.asList(s.getProfit(), s.getAmountBuy(), s.getAmountSell(), s.getTotal()),"SEK"));

					s.getProfit().forEach((a, b) -> profit.merge(a, b, Integer::sum));
					s.getAmountBuy().forEach((a, b) -> amountBuy.merge(a, b, Integer::sum));
					s.getAmountSell().forEach((a, b) -> amountSell.merge(a, b, Integer::sum));
					s.getTotal().forEach((a, b) -> total.merge(a, b, Integer::sum));
				});

				// Total for all days row.
				if (v.size() > 1) {
					System.out.println("\nTOTAL");
					System.out.println(
							headDisplayer(Arrays.asList("Profit", "Total Buy", "Total Sell", "Total Buy & Sell")));
					System.out.println(rowDisplayer(Arrays.asList(profit, amountBuy, amountSell, total), "SEK"));
				}
			});

			// Display Statistics for all sites.
			if (sites.size() > 1) {
				System.out.println("\n----- ALL -----");

				// Display Total Profit for all sites combined for each currency.
				Map<String, Integer> l1 = result.stream()
						.collect(Collectors.toMap(e -> "ALL", StatDay::getProfit, (s1, s2) -> {
							s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
							return s2;
						})).get("ALL");

				// Display Total Buy amount for all sites combined for each currency.
				Map<String, Integer> l2 = result.stream()
						.collect(Collectors.toMap(e -> "ALL", StatDay::getAmountBuy, (s1, s2) -> {
							s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
							return s2;
						})).get("ALL");

				// Display Total Sell amount for all sites combined for each currency.
				Map<String, Integer> l3 = result.stream()
						.collect(Collectors.toMap(e -> "ALL", StatDay::getAmountSell, (s1, s2) -> {
							s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
							return s2;
						})).get("ALL");

				// Display Total amount for all sites combined for each currency.
				Map<String, Integer> l4 = result.stream()
						.collect(Collectors.toMap(e -> "ALL", StatDay::getTotal, (s1, s2) -> {
							s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
							return s2;
						})).get("ALL");

				System.out
						.println(headDisplayer(Arrays.asList("Profit", "Total Buy", "Total Sell", "Total Buy & Sell")));
				System.out.println(rowDisplayer(Arrays.asList(l1, l2, l3, l4), "SEK"));
			}
		}
		System.out.println("\n----- END -----");
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
	static LocalDate createEndDay(Optional<Period> periodOption, Optional<LocalDate> startDay) {
		switch (periodOption.get()) {
		case DAY:
			return startDay.get().plusDays(1);
		case WEEK:
			return startDay.get().plusWeeks(1);
		case MONTH:
			return startDay.get().plusMonths(1);
		}
		return startDay.get();
	}

	/**
	 * Generates a string representing a row. Adds spaces as padding and a '|' as
	 * separator.
	 * 
	 * @param titles - A list of string to fill the column with.
	 * @return String
	 */

	static String headDisplayer(List<String> titles) {
		StringBuilder row = new StringBuilder();
		titles.forEach(s -> {
			StringBuilder sb = new StringBuilder();
			sb.append(s);
			IntStream.range(0, DISPLAY_COLUMN_WIDTH - sb.length()).forEachOrdered(n -> {
				sb.append(" ");
			});
			sb.append("|");
			row.append(sb);
		});
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
	static String rowDisplayer(List<Map<String, Integer>> list, String prefix) {
		StringBuilder table = new StringBuilder();
		for (String c : list.get(0).keySet()) {
			StringBuilder row = new StringBuilder();
			for (Map<String, Integer> map : list) {
				StringBuilder column = new StringBuilder();
				column.append(String.format("%s: %d %s", c, map.get(c), prefix));
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
class StatDay {
	/**
	 * The sum of profit for a day for each currency.
	 */
	private Map<String, Integer> profit;

	/**
	 * The sum of BUY amount for a day for each currency.
	 */
	private Map<String, Integer> amountBuy;

	/**
	 * The sum of SELL amount for a day for each currency.
	 */
	private Map<String, Integer> amountSell;

	/**
	 * The result of SELL - BUY for a day for each currency.
	 */
	private Map<String, Integer> total;

	/**
	 * The site which the transactions belongs to.
	 */
	private String site;

	/**
	 * The date the transactions occurred (Transaction.getTimeStamp).
	 */
	private LocalDate date;

	/**
	 * Initialize a new statDay.
	 * 
	 * @param site - The site which the transactions belongs to.
	 * @param date - The date the transactions occurred.
	 */
	public StatDay(String site, LocalDate date) {
		this.site = site;
		this.date = date;
	}

	/**
	 * @return the profit
	 */
	public Map<String, Integer> getProfit() {
		return profit;
	}

	/**
	 * @param profit the profit to set
	 */
	public void setProfit(Map<String, Integer> profit) {
		this.profit = profit;
	}

	/**
	 * @return the amountBuy
	 */
	public Map<String, Integer> getAmountBuy() {
		return amountBuy;
	}

	/**
	 * @param amountBuy the amountBuy to set
	 */
	public void setAmountBuy(Map<String, Integer> amountBuy) {
		this.amountBuy = amountBuy;
	}

	/**
	 * @return the amountSell
	 */
	public Map<String, Integer> getAmountSell() {
		return amountSell;
	}

	/**
	 * @param amountSell the amountSell to set
	 */
	public void setAmountSell(Map<String, Integer> amountSell) {
		this.amountSell = amountSell;
	}

	/**
	 * @return the total
	 */
	public Map<String, Integer> getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Map<String, Integer> total) {
		this.total = total;
	}

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

}
