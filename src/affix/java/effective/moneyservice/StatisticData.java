package affix.java.effective.moneyservice;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for data of transactions for a day.
 * 
 * @author jesper
 *
 */
class StatisticData {
	/**
	 * The container for calculated values.
	 */
	private Map<String, Map<String, Integer>> data = new HashMap<>();
	
	/**
	 * The site which the statistics belongs to.
	 */
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
	
	/**
	 * @return the data
	 */
	public Map<String, Map<String, Integer>> getData() {
		return data;
	}
	
	/**
	 * Merge data from another StatisticsData.
	 * 
	 * @param other - {@link StatisticData} to merge
	 * @return this - this merged data.
	 */
	public StatisticData mergeData(StatisticData other) {
		other.getData().forEach((k1, v1) -> {
			v1.forEach((k2, v2) -> {
					this.data.putIfAbsent(k1, new HashMap<String, Integer>());
					this.data.get(k1).merge(k2, v2, Integer::sum);
			});
		});
		return this;
	}
	
	/**
	 * Insert to data map.
	 * 
	 * @param key - Key for value.
	 * @param value - Value for key.
	 */
	public void putToData(String key, Map<String, Integer> value) {
		this.data.put(key, value);
	}
	
	/**
	 * Initialize {@link StatisticData} for each statistics
	 * for each day within sDay and endDay.
	 * 
	 * @param statistics - The statistics to initialize data from.
	 * @param sDay - The start day.
	 * @param endDay - The end day-
	 * @return list of {@link StatisticData} for each day for each {@link Statistic}.
	 */
	public static List<StatisticData> initializeDataFromStatistics(List<Statistic> statistics, LocalDate sDay, LocalDate endDay) {
		List<StatisticData> dataList = new ArrayList<>();
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

				dataList.add(temp);
				} catch(NullPointerException e) {
					
				}
			}
		}
		return dataList;
	}

}