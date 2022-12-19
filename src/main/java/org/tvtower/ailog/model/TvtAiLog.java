package org.tvtower.ailog.model;

import java.util.ArrayList;
import java.util.List;

public class TvtAiLog {

	private List<TvtAiTask> tasks = new ArrayList<>();
	private TVTTaskTimeAggregator aggregator = new TVTTaskTimeAggregator();

	private TvtAiTask currentTask;
	private int playerNumber;
	private int currentDay = -1;

	public TvtAiLog(int player) {
		playerNumber = player;
	}

	public void addLine(String l) {
		if (l.contains("=== Budget day")) {
			String s = l.substring(l.indexOf("day ") + 4);
			s = s.substring(0, s.indexOf(" =="));
			int day = Integer.parseInt(s.trim());
			if (currentDay < 0) {
				setDayForEarlierTasks(day);
			}
			currentDay = day;
		}
		if (l.contains("Starting task '")) {
			currentTask = new TvtAiTask(currentDay);
			tasks.add(currentTask);
		}
		if (currentTask != null) {
			currentTask.addLine(l);
		}
	}

	private void setDayForEarlierTasks(int day) {
		boolean newDayPossible = true;
		int currentDay = day;
		for (int i = tasks.size() - 1; i >= 0; i--) {
			TvtAiTask t = tasks.get(i);
			if (newDayPossible && t.getHour() == 23) {
				currentDay--;
				newDayPossible = false;
			} else if (!newDayPossible && t.getHour() < 5) {
				newDayPossible = true;
			}
			if (!t.setDay(currentDay)) {
				return;
			}
		}
	}

	public void analyze() {
		// TODO really analyze
		System.out.println(playerNumber);
		System.out.println(tasks.size() + " tasks");
		for (TvtAiTask t : tasks) {
			aggregator.add(t);
		}
		aggregator.printOverview();
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
