package org.tvtower.statistics;

public class StatLine {

	int player;
	int day;
	int hour;
	int reach;
	int costs;
	int income;

	public StatLine (String line) {
		String[] split = line.split(";");
		player=get(split,0);
		day=get(split,1);
		hour=get(split,2);
		reach=get(split,3);
		costs=get(split,4);
		income=get(split,5);
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
