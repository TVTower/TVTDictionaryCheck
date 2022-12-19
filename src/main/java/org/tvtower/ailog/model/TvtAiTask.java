package org.tvtower.ailog.model;

import java.util.ArrayList;
import java.util.List;

public class TvtAiTask {

	private String name;
	private boolean done = false;
	private int day = -1;
	private String taskStartTime;
	private String enterRoomTime;
	private int hour = -1;
	private int goToRoomMinutes = 0;
	private int totalMinutes = -1;
	private List<TVTAiJob> jobs = new ArrayList<>();

	public TvtAiTask(int currentDay) {
		day = currentDay;
	}

	public void addLine(String l) {
		if (done) {
			return;
		}
		if (name == null) {
			if (l.contains("Starting task '")) {
				int startIndex = l.indexOf('\'') + 1;
				name = l.substring(startIndex, l.indexOf('\'', startIndex + 1));
				extractTaskStartTime(l);
			} else {
				throw new IllegalStateException();
			}
		}
		if (l.contains("AIJobGoToRoom: Entering roomId:")) {
			enterRoomTime = TvtAiLog.extractGameTime(l);
			goToRoomMinutes = TvtAiLog.minutes(taskStartTime, enterRoomTime);
		}
		if (l.contains("Task finished")) {
			totalMinutes = TvtAiLog.minutes(taskStartTime, TvtAiLog.extractGameTime(l));
			done = true;
		}
	}

	private void extractTaskStartTime(String l) {
		taskStartTime = TvtAiLog.extractGameTime(l);
		hour = TvtAiLog.hour(taskStartTime);
	}

	public int getHour() {
		return hour;
	}

	public String getName() {
		return name;
	}

	public int getDay() {
		return day;
	}

	boolean setDay(int day) {
		if (this.day < 0) {
			this.day = day;
			return true;
		} else {
			return false;
		}
	}

	public int getTaskTime() {
		return totalMinutes - goToRoomMinutes;
	}

	public int getGoToRoomMinutes() {
		return goToRoomMinutes;
	}
}