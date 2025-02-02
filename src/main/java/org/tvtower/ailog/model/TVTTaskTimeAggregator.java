package org.tvtower.ailog.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

class TVTTaskTimeAggregator {

	private Map<String, TvtTaskAggregate> aggregates = new TreeMap<>();

	public void add(TVTAiTask t) {
		TvtTaskAggregate agg = aggregates.computeIfAbsent(t.getName(), s -> new TvtTaskAggregate(s));
		agg.add(t);
	}

	public List<String> printOverview() {
		return aggregates.values().stream().map(agg -> agg.printOverview()).collect(Collectors.toList());
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
			while (taskName.length() < 20) {
				taskName = taskName+" ";
			}
		}

		public String printOverview() {
			return taskName+ pad(count, 5) + pad(getAverageGoToRoomTime(), 7) + pad(getAverageTaskTime(), 7);
		}

		private String pad(Object o, int length) {
			Object toShow = o;
			String result = (toShow == null) ? "" : toShow.toString();
			while (result.length() < length) {
				result = " " + result;
			}
			return result;
		}

		void add(TVTAiTask t) {
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
