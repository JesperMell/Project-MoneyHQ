package affix.java.effective.moneyservice;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class CLIHelper {

	private static Scanner input = new Scanner(System.in);

	enum Period {
		DAY, WEEK, MONTH
	};

	/**
	 * menuInput.
	 * 
	 * Main method for user to enter values.
	 * 
	 */
	static void menuInput() {
		Set<Site> sites;
		Optional<LocalDate> startDay;
		Optional<Period> periodOption;
		List<String> currencies;

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
			currencies = readCurrencyCodes();
		} while (currencies.isEmpty());

		LocalDate endDay = createEndDay(periodOption, startDay);

		// Create Statistics
		List<Statistic> statistics = new ArrayList<>();

		for (Site s : sites) {
			s.readTransactions(startDay.get(), endDay);
			statistics.add(new Statistic(s.getCompletedTransactions(), currencies, s.getSiteName()));
		}

		// Display Statistics for each site
		List<StatDay> result = new ArrayList<>();

		for (Statistic s : statistics) {
			System.out.println(s.getSiteName());
			System.out.println("---");
			System.out.println(s.getDiffCurrency());
			System.out.println("---");
			for (LocalDate l = startDay.get(); !l.equals(endDay); l = l.plusDays(1)) {
				StatDay stat = new StatDay(s.getSiteName(), l);
				stat.setProfit(s.getProfit(l.toString()));
				stat.setAmountBuy(s.getTotalAmountBuy(l.toString()));
				stat.setAmountSell(s.getTotalAmountSell(l.toString()));
				stat.setTotal(s.getTotalAmount(l.toString()));

				result.add(stat);
			}
		}

		result.stream().collect(Collectors.groupingBy(StatDay::getSite)).forEach((k, v) -> {
			Map<String, Integer> profit = new HashMap<>();
			Map<String, Integer> amountBuy = new HashMap<>();
			Map<String, Integer> amountSell = new HashMap<>();
			Map<String, Integer> total = new HashMap<>();

			System.out.println(k);
			v.forEach((s) -> {
				System.out.println(s.getDate());
				System.out.println(s.getProfit());
				System.out.println(s.getAmountBuy());
				System.out.println(s.getAmountSell());
				System.out.println(s.getTotal());

				s.getProfit().forEach((a, b) -> profit.merge(a, b, Integer::sum));
				s.getAmountBuy().forEach((a, b) -> amountBuy.merge(a, b, Integer::sum));
				s.getAmountSell().forEach((a, b) -> amountSell.merge(a, b, Integer::sum));
				s.getTotal().forEach((a, b) -> total.merge(a, b, Integer::sum));
			});
			System.out.println("TOTAL:");
			System.out.println(profit);
			System.out.println(amountBuy);
			System.out.println(amountSell);
			System.out.println(total);
		});

		// Display Statistics for all sites.
		System.out.println("ALL");
		statistics.stream().map(s -> s.getDiffCurrency()).flatMap(m -> m.entrySet().stream())
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)))
				.forEach((x, y) -> System.out.println(x + ": " + y));

		// Display Total Profit all sites combined for each currency.
		System.out.println("Profit:");
		result.stream().collect(Collectors.toMap(e -> "ALL", StatDay::getProfit, (s1, s2) -> {
			s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
			return s2;
		})).get("ALL").forEach((k, v) -> System.out.println(k + " " + v));

		// Display Total Buy amount all sites combined for each currency.
		System.out.println("Amount Buy:");
		result.stream().collect(Collectors.toMap(e -> "ALL", StatDay::getAmountBuy, (s1, s2) -> {
			s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
			return s2;
		})).get("ALL").forEach((k, v) -> System.out.println(k + " " + v));

		// Display Total Sell amount all sites combined for each currency.
		System.out.println("Amount Sell:");
		result.stream().collect(Collectors.toMap(e -> "ALL", StatDay::getAmountSell, (s1, s2) -> {
			s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
			return s2;
		})).get("ALL").forEach((k, v) -> System.out.println(k + " " + v));

		// Display Total amount all sites combined for each currency.
		System.out.println("Amount Total:");
		result.stream().collect(Collectors.toMap(e -> "ALL", StatDay::getTotal, (s1, s2) -> {
			s1.forEach((k, v) -> s2.merge(k, v, Integer::sum));
			return s2;
		})).get("ALL").forEach((k, v) -> System.out.println(k + " " + v));

	}

	/**
	 * readSites.
	 * 
	 * Display menu for selecting sites. The user can select 1 to n sites, choose
	 * 'ALL' option to select all sites.
	 * 
	 * Returns a list with the selected site names.
	 * 
	 * @return Set<String>
	 */
	private static Set<Site> readSites() {
		System.out.println("Choose a Site (For multiple choices use comma seperation)");

		List<Site> sites = new ArrayList<>();
		sites.addAll(HQApp.sites.values());

		// Print the options.
		int i = 0;
		for (Site site : sites) {
			System.out.println(String.format("%d: %s", ++i, site.getSiteName()));
		}
		System.out.println(String.format("%d: %s", ++i, "ALL"));

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
			return new HashSet<>();
		}

		System.out.println("Site selected: ");
		result.forEach((s) -> System.out.println(s.getSiteName()));
		System.out.println("---");

		return result;
	}

	/**
	 * readStartDate.
	 * 
	 * Display menu for entering start date.
	 * 
	 * @return Optional<LocalDate>
	 */
	private static Optional<LocalDate> readStartDay() {
		System.out.println("Enter start day of Period");
		try {
			return Optional.of(LocalDate.parse(input.next()));
		} catch (DateTimeParseException e) {
			System.out.println("Invalid format, try again");
			return Optional.empty();
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
			System.out.println(String.format("%d: %s", ++i, p));
		}

		System.out.print("Enter your choice:");
		int data = input.nextInt();
		try {
			return Optional.of(Period.values()[data - 1]);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Wrong input.");
			return Optional.empty();
		}
	}

	/**
	 * readCurrencyCodes.
	 * 
	 * Display menu for selecting currencies.
	 * 
	 * @return Optional<String>
	 */
	private static List<String> readCurrencyCodes() {
		System.out.println("Choose currencies (Use comma as seperator)");
		HQApp.currencyMap.keySet().forEach((x) -> System.out.print(x + " "));
		System.out.println("ALL");
		String data = input.next();

		List<String> currencies = new ArrayList<>();
		
		if(data.equals("ALL")) {
			return new ArrayList<String>(HQApp.currencyMap.keySet());
		}

		for (String code : data.split(",")) {
			if (HQApp.currencyMap.get(code) != null)
				currencies.add(code);
		}

		return currencies;

	}

	/**
	 * createEndDay.
	 * 
	 * Calculates the endDate for startDate and Period.
	 * 
	 * @return LocalDate
	 */
	private static LocalDate createEndDay(Optional<Period> periodOption, Optional<LocalDate> startDay) {
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
}

class StatDay {
	private Map<String, Integer> profit;
	private Map<String, Integer> amountBuy;
	private Map<String, Integer> amountSell;
	private Map<String, Integer> total;
	private String site;
	private LocalDate date;

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
