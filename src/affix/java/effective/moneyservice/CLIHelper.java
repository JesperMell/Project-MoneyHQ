package affix.java.effective.moneyservice;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class CLIHelper {

	static Scanner input = new Scanner(System.in);

	enum Period {
		DAY, WEEK, MONTH
	};

	/**
	 * menuInput.
	 * 
	 * Main method for user to enter values.
	 * 
	 */
	public static int menuInput() {
		Set<String> sites;
		Optional<LocalDate> startDay;
		Optional<Period> periodOption;
		Optional<String> currency;

		// Choose Site.
		do {
			sites = readSites();
		} while (sites.isEmpty());

		// Choose Period
		do {
			periodOption = readPeriod();
		} while (periodOption.isEmpty());

		// Choose Start Day.
		do {
			startDay = readStartDay();
		} while (startDay.isEmpty());

		// Choose Currency.
		do {
			currency = readCurrency();
		} while (currency.isEmpty());

		return 0;
	}

	/**
	 * readSites.
	 * 
	 * Display menu for selecting sites.
	 * The user can select 1 to n sites, choose 'ALL'
	 * option to select all sites.
	 * 
	 * Returns a list with the selected site names.
	 * 
	 * @return Set<String>
	 */
	private static Set<String> readSites() {
		System.out.println("Choose a Site (For multiple choices use comma seperation)");

		Set<String> sites = new TreeSet<String>();
		sites.addAll(HQApp.sites.keySet());

		int i = 0;
		for (String name : sites) {
			System.out.println(String.format("%d: %s", ++i, name));
		}
		System.out.println(String.format("%d: %s", ++i, "ALL"));

		String[] siteArray = new String[sites.size()];
		sites.toArray(siteArray);

		Set<String> result = new TreeSet<>();

		for (String data : input.next().split(",")) {
			int index = Integer.parseInt(data.trim());
			if (i == index)
				return sites;

			result.add(siteArray[index - 1]);
		}

		System.out.println("Site selected: ");
		result.forEach(System.out::print);

		return result;
	}

	/**
	 * readStartDate.
	 * 
	 * Display menu for entering start date.
	 * 
	 * @return Optional<LocalDate>
	 */
	 static Optional<LocalDate> readStartDay() {
		System.out.println("Enter start day of Period");
		try {
			return Optional.of(LocalDate.parse(input.next()));
		} catch (DateTimeParseException e) {
			System.out.println("Invalid format, try again");
			return null;
		}
	}

	/**
	 * readPeriod.
	 * 
	 * Display menu for entering Period.
	 * 
	 * @return Optional<Period>
	 */
	private static Optional<Period> readPeriod() {
		int i = 0;
		System.out.println("Choose a Period");
		for (Period p : Period.values()) {
			System.out.println(String.format("%d: %s", i++, p));
		}

		System.out.print("Enter your choice:");
		int data = input.nextInt();
		try {
			return Optional.of(Period.values()[data]);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Wrong input.");
			return null;
		}
	}

	/**
	 * readCurrency.
	 * 
	 * Display menu for selecting currency.
	 * 
	 * @return Optional<String>
	 */
	private static Optional<String> readCurrency() {
		System.out.println("Choose a Currency");
		HQApp.currencyMap.keySet().forEach((x) -> System.out.print(x + " "));
		String data = input.next();

		if (!HQApp.currencyMap.keySet().contains(data)) {
			System.out.println("Unvalid Currency");
			return null;
		}
		return Optional.of(data);

	}

	/**
	 * createEndDate.
	 * 
	 * Calculates the endDate for startDate and Period.
	 * 
	 * @return LocalDate
	 */
	private static LocalDate createEndDate(Period period, LocalDate startDate) {
		switch(period) {
		case DAY: return startDate.plusDays(1);
		case WEEK: return startDate.plusWeeks(1);
		case MONTH: return startDate.plusMonths(1);
		}
		return startDate;
	}

}
