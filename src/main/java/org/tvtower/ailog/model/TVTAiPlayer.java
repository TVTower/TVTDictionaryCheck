package org.tvtower.ailog.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tvtower.ailog.model.TVTAiPlayerDay.Category;
import org.tvtower.statistics.FileAggregate;

class TVTAiPlayer {

	private int playerNumber;
	private List<TVTAiPlayerDay> playerDays = new ArrayList<>();
	private static final NumberFormat amountFormat = new DecimalFormat("###,###");

	// for log parsing
	private TVTAiTask currentTask;
	private TVTAiPlayerDay currentDay = null;

	public TVTAiPlayer(int player) {
		playerNumber = player;
	}

	public void setStatAggregator(FileAggregate statAggregator) {
		statAggregator.getLines().stream().filter(l -> l.player == playerNumber)
				.forEach(l -> Optional.ofNullable(getPlayerDay(l.day)).ifPresent(d -> d.handleStat(l)));
	}

	public TVTAiPlayerDay getPlayerDay(int day) {
		return playerDays.stream().filter(d -> d.day == day).findFirst().orElse(null);
	}

	public void addLine(String l) {
		// TODO handling of first day fails!!!
		if (currentDay == null) {
			currentDay = new TVTAiPlayerDay(playerNumber);
			playerDays.add(currentDay);
		}
		if (l.contains("=== Budget day")) {
			String s = l.substring(l.indexOf("day ") + 4);
			s = s.substring(0, s.indexOf(" =="));
			int day = Integer.parseInt(s.trim());
			if (currentDay.day < 0) {
				currentDay.setDay(day - 1);
			}
			if (currentDay.day != day) {
				currentDay = new TVTAiPlayerDay(playerNumber, day);
				playerDays.add(currentDay);
			}
		}
		if (l.contains("Starting task '")) {
			currentTask = new TVTAiTask();
			currentDay.addTask(currentTask);
		}
		if (currentTask != null) {
			currentTask.addLine(l);
		}
		currentDay.addLine(l);
	}

	public List<String> analyze(int startDay, int endDay) {
		List<String> result=new ArrayList<>();
		result.add("player " + playerNumber);
		final int end = endDay < 0 ? Integer.MAX_VALUE : endDay;
		// failed ads overview
		Predicate<TVTAiPlayerDay> dayPred = d -> (d.day >= startDay && d.day <= end);

		playerDays.stream().filter(dayPred).forEach(d -> {
			int day = d.day;
			String prefix = "day " + (day < 10 ? "  " + day : (day < 100 ? " " + day : day)) + ": ";
			if (d.count(Category.adFailed) > 0) {
				result.add(
						prefix + d.count(Category.adFailed) + " !failed spots! hours: " + d.hours(Category.adFailed));
			}
			if (d.count(Category.adTrailer) > 0) {
				result.add(
						prefix + d.count(Category.adTrailer) + " trailers       hours: " + d.hours(Category.adTrailer));
			}
			d.interestingLogLines.forEach(l -> {
				result.add(prefix + l);
			});
		});

		// TODO really analyze
//		result.add("player " + playerNumber);
//		if (adTracer.hasContent()) {
//			result.add("\nad tracer:");
//			result.add(adTracer.getInfo());
//		}
//
		result.add("-----------------");
		return result;
	}

	public static List<String> overviewHeaders() {
		List<String> result = new ArrayList<>();
		result.add("                 ");
		result.add("-----------------");
		result.add("image            ");
		result.add("money            ");
		result.add("receivers        ");
		result.add("daily costs      ");
		result.add("total ad income  ");
		result.add("total ad penalty ");
		result.add("failed spots     ");
		result.add("trailers         ");
		return result;
	}

	public List<String> getOverviewColumn(int startDay, int endDay) {
		List<String> result = new ArrayList<>();
		TVTAiPlayerDay endDayData = getPlayerDay(endDay);
		result.add("       " + playerNumber + "       ");
		result.add("---------------");
		result.add(pad(endDayData.hours.get(23).image));
		result.add(pad(endDayData.currentMoney));
		result.add(pad(endDayData.hours.get(23).receivers));
		result.add(pad(endDayData.hours.get(23).dailyCosts));
		result.add(pad(endDayData.totalAdIncome));
		result.add(pad(endDayData.totalFailedAdsCost));
		AtomicInteger failed = new AtomicInteger(0);
		AtomicInteger trailer = new AtomicInteger(0);
		playerDays.stream().filter(d->(d.day>=startDay && d.day<=endDay))
		.forEach(d->{
			failed.addAndGet((int)d.count(Category.adFailed));
			trailer.addAndGet((int)d.count(Category.adTrailer));
		});
		result.add(pad(failed.get()));
		result.add(pad(trailer.get()));
		return result;
	}

	private String pad(Object o) {
		Object toShow = o;
		if (o instanceof Integer) {
			BigDecimal bd = new BigDecimal((Integer) o);
			toShow = amountFormat.format(bd);
		}
		String result = (toShow == null) ? "" : toShow.toString();
		while (result.length() < 14) {
			result = " " + result;
		}
		return result + " ";
	}

	public List<String> showTaskOverview(int startDay, int endDay) {
		List<String> result=new ArrayList<>();
		final int end = endDay < 0 ? Integer.MAX_VALUE : endDay;
		TVTTaskTimeAggregator aggregator = new TVTTaskTimeAggregator();
		List<TVTAiTask> tasks = playerDays.stream().filter(d -> (d.day >= startDay && d.day <= end))
				.flatMap(d -> d.tasks.stream()).collect(Collectors.toList());
		result.add("\nPlayer " + playerNumber +": "+tasks.size() + " tasks");
		for (TVTAiTask t : tasks) {
			aggregator.add(t);
		}
		result.addAll(aggregator.printOverview());
		result.add("-----------------");
		return result;
	}

	public static final String extractGameTime(String l) {
		String time = l.substring(11, 16);
		if (time.indexOf(':') != 2) {
			throw new IllegalArgumentException("not a regular log line: " + l);
		}
		return time;
	}

	public static final int minutes(String time1, String time2) {
		int hour1 = hour(time1);
		int hour2 = hour(time2);
		int minute1 = minute(time1);
		int minute2 = minute(time2);
		int diff = (60 * hour2 + minute2) - (60 * hour1 + minute1);
		return (diff + 24 * 60) % (24 * 60);
	}

	public static final int hour(String time) {
		return Integer.parseInt(time.substring(0, 2));
	}

	public static final int minute(String time) {
		return Integer.parseInt(time.substring(3, 5));
	}

}
