package org.tvtower.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HourAggregate {

	private int hour;
	List<StatLine> lines=new ArrayList<>();

	public HourAggregate(int hour) {
		this.hour=hour;
	}

	public void add(StatLine line) {
		if(line.hour==this.hour) {
			lines.add(line);
		}
	}

	public int getHour() {
		return hour;
	}

	public void print() {
		System.out.println("    "+hour+ " - avgProfit "+avgProfit());
	}

	public int avgProfit() {
		return avg(l->l.income);
	}

	public int avgCost() {
		return avg(l->l.costs);
	}

	public int avgReach() {
		return avg(l->l.reach);
	}

	private int avg(Function<StatLine, Integer> valueProvider) {
		BigDecimal b=BigDecimal.ZERO;
		if(lines.size()>0) {
			for (StatLine statLine : lines) {
				b=b.add(new BigDecimal(valueProvider.apply(statLine)));
			}
			b= b.divide(new BigDecimal(lines.size()),0, RoundingMode.HALF_UP);
		}
		return b.intValue();
	}
}
