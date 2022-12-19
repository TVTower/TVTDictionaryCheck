package org.tvtower.ailog.model;

import java.util.ArrayList;
import java.util.List;

public class TvtAiLog {

	private List<TvtAiTask> tasks = new ArrayList<>();

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
			System.out.println("day " + t.getDay() + ":" + t.getHour() + " " + t.getName());
		}
	}

	public static final String extractGameTime(String l) {
		String time = l.substring(11, 16);
		if (time.indexOf(':') != 2) {
			throw new IllegalArgumentException("not a regular log line: " + l);
		}
		return time;
	}
}
