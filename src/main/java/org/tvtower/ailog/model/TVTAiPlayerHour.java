package org.tvtower.ailog.model;

import java.math.BigDecimal;

import org.tvtower.statistics.StatLine;

class TVTAiPlayerHour {

	final int player;
	int day;
	final int hour;

	int receivers;
	int dailyCosts;
	BigDecimal image;

	int newsViewers;

	int progViewers;
	int progShare;

	int adSpotIncome;
	int adMinAudience;

	public TVTAiPlayerHour(int player, int day, int hour) {
		this.player=player;
		this.day=day;
		this.hour=hour;
	}

	public void populateFrom(StatLine l) {
		adSpotIncome=l.income;
		receivers=l.reach;
		dailyCosts=l.costs;
		image=l.image;
		//TODO weitere Werte übernehmen
		
	}
}
