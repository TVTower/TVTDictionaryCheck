package org.tvtower.statistics;

import java.math.BigDecimal;

public class StatLine {

	public final int player;
	public final int day;
	public final int hour;
	public int reach;
	public int costs;
	public final int income;
	public final BigDecimal image;

	public StatLine (String line) {
		String[] split = line.split(";");
		player=get(split,0);
		day=get(split,1);
		hour=get(split,2);
		reach=get(split,3);
		costs=get(split,4);
		income=get(split,5);
		BigDecimal img=null;
		if(split.length>6 && !"-".equals(split[6])) {
			img=new BigDecimal(split[6]);
		}
		image=img;
	}

	private static int get(String[] split, int index) {
		return Integer.parseInt(split[index]);
	}

	@Override
	public String toString() {
		return new StringBuilder().
				append("pl: ").append(player).append("; ").
				append("d : ").append(day).append("; ").
				append("h: ").append(hour).append("; ").
				append("r: ").append(reach).append("; ").
				append("co: ").append(costs).append("; ").
				append("pr: ").append(income).append("; ").toString();
				
	}
}
