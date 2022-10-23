package org.tvtower.statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReachAggregate {

	enum PrintHourMode{
		all,
		prime,
		none;
	}

	public static PrintHourMode printHourMode=PrintHourMode.prime;
	public static boolean showNormalizedToReach=false;
	private int min;
	private int max;
	List<StatLine> lines=new ArrayList<>();
	Map<Integer, HourAggregate> hourAggregates=new LinkedHashMap<>();

	public ReachAggregate(int min, int max) {
		this.min=min;
		this.max=max;
		for(int i=0; i<24; i++) {
			hourAggregates.put(i, new HourAggregate(i));
		}
	}

	public boolean matchesReach(int reach) {
		return reach>= min && reach<max;
	}

	public void add(StatLine line) {
		if(matchesReach(line.reach)) {
			lines.add(line);
			hourAggregates.get(line.hour).add(line);
		}
	}

	public List<String> print(int normalizedReach) {
		List<String> result=new ArrayList<>();
		HourAggregate rep = hourAggregates.get(0);
		if(rep.avgReach()==0) {
			return result;
		}
		long avgReach = rep.avgReach();
		long avgCosts = rep.avgCost();
		long avgCostPM=avgCosts / (avgReach/1000);
		result.add(" reach " +min +" - "+ max +"; costPerMil "+avgCostPM+" avgReach "+rep.avgReach()+" avgCostsPerDay "+rep.avgCost());
		long hourProfit=0;
		boolean hourFound=false;
		for (HourAggregate agg : hourAggregates.values()) {
			hourProfit=hourProfit+agg.avgProfit();
			if(hourProfit>=avgCosts && !hourFound) {
				result.add("   antenna fixed costs payed at hour "+agg.getHour());
				hourFound=true;
			}
		}
		long dayProfit=hourProfit-avgCosts;
		result.add("   day's avg profit (fix costs payed): "+dayProfit);
		if(normalizedReach > 0 && showNormalizedToReach) {
			result.add("   normalized to reach "+normalizedReach+ " - costs: "+normalizedReach*avgCosts/avgReach +", dayProfit: "+normalizedReach*dayProfit/avgReach);
		}
		result.addAll(printHours());
		return result;
	}

	private List<String> printHours() {
		List<String> result=new ArrayList<>();
		switch (printHourMode) {
		case all:
			hourAggregates.values().forEach(h->result.add(h.print()));
			break;
		case prime:
			hourAggregates.values().stream().filter(h->h.getHour()>17).forEach(h->result.add(h.print()));
			break;
		default:
			break;
		}
		return result;
	}
}
