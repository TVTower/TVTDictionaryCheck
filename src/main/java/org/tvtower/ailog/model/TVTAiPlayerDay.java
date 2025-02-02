package org.tvtower.ailog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tvtower.statistics.StatLine;

class TVTAiPlayerDay {

	enum Category {
		adTrailer, adFailed
	}

	private static final String AD_PENALTY_MARKER = "pay ad penalty";
	List<String> interestingLogLines = new ArrayList<>();

	int day;
	int player;
	List<TVTAiTask> tasks = new ArrayList<>();

	Map<Integer, TVTAiPlayerHour> hours = new LinkedHashMap<>();
	int currentMoney;
//	int totalReceivers;
	
//	int totalTrailersCount;
//	int totalFailedAdsCount;
	int dayFailedAdConsts;
	int totalFailedAdsCost;
	int dayAdIncome;
	int totalAdIncome;
	BigDecimal image;
	int dailyCosts;

	public TVTAiPlayerDay(int player) {
		this(player, -1);
	}

	public TVTAiPlayerDay(int player, int day) {
		this.player = player;
		setDay(day);
	}

	void setDay(int day) {
		this.day = day;
		if (hours.isEmpty()) {
			for (int i = 0; i < 24; i++) {
				hours.put(i, new TVTAiPlayerHour(player, day, i));
			}
		} else {
			for (TVTAiPlayerHour hour : hours.values()) {
				hour.day = day;
			}
		}
	}

	public void addTask(TVTAiTask task) {
		tasks.add(task);
	}

	// handle special lines
	public void addLine(String l) {
		if (l.contains(AD_PENALTY_MARKER)) {
			interestingLogLines.add(l.substring(l.indexOf(AD_PENALTY_MARKER)));
		}
	}

	void handleStat(StatLine l) {
		if (l.day != day) {
			throw new IllegalArgumentException();
		}
		TVTAiPlayerHour hour = hours.get(l.hour);
		hour.populateFrom(l);
		if(l.hour==23) {
			image=l.image;
		}
	}

	public long count(Category cat) {
		Predicate<TVTAiPlayerHour> p = null;
		switch (cat) {
		case adFailed:
			p = h -> h.adSpotIncome == -1;
			break;
		case adTrailer:
			p = h -> h.adSpotIncome == 0;
			break;
		default:
			throw new IllegalArgumentException();
		}
		return getBaseFiltered(p).count();
	}

	public String hours(Category cat) {
		Predicate<TVTAiPlayerHour> p = null;
		switch (cat) {
		case adFailed:
			p = h -> h.adSpotIncome == -1;
			break;
		case adTrailer:
			p = h -> h.adSpotIncome == 0;
			break;
		default:
			throw new IllegalArgumentException();
		}
		return getBaseFiltered(p).map(h->""+h.hour).collect(Collectors.joining(", "));
	}

	private Stream<TVTAiPlayerHour> getBaseFiltered(Predicate<TVTAiPlayerHour> p){
		return hours.values().stream().filter(p.and(h->h.receivers>0));
	}
}
