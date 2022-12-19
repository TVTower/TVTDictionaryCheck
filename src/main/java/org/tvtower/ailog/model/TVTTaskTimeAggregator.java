package org.tvtower.ailog.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

//TODO aggregate by day
public class TVTTaskTimeAggregator {

	private Map<String, TvtTaskAggregate> aggregates = new TreeMap<>();

	public void add(TvtAiTask t) {
		TvtTaskAggregate agg = aggregates.computeIfAbsent(t.getName(), s -> new TvtTaskAggregate(s));
		agg.add(t);
	}

	public void printOverview() {
		aggregates.values().forEach(agg -> agg.printOverview());
	}

	private static class TvtTaskAggregate {
		private String taskName;
		private int count;
		private int totalMinutes;
		private int goToRoomCount;
		private int totalGoToRoomMinutes;
		private int maxGoToTime;
		private int maxTaskTime;

		public TvtTaskAggregate(String name) {
			taskName = name;
		}

		public void printOverview() {
			System.out.println(taskName + " " + count + " " + getAverageGoToRoomTime() + " " + getAverageTaskTime());
		}

		void add(TvtAiTask t) {
			if (t.getTaskTime() < 0) {
				// task not finished
				return;
			}
			count++;
			totalMinutes += t.getTaskTime();
			if (t.getGoToRoomMinutes() >= 0) {
				goToRoomCount++;
				totalGoToRoomMinutes += t.getGoToRoomMinutes();
			}
			if (t.getGoToRoomMinutes() > maxGoToTime) {
				maxGoToTime = t.getGoToRoomMinutes();
			}
			if (t.getTaskTime() > maxTaskTime) {
				maxTaskTime = t.getTaskTime();
			}
		}

		public BigDecimal getAverageTaskTime() {
			return new BigDecimal(totalMinutes).divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
		}

		public BigDecimal getAverageGoToRoomTime() {
			return new BigDecimal(totalGoToRoomMinutes).divide(new BigDecimal(goToRoomCount), 2, RoundingMode.HALF_UP);
		}
	}

}
