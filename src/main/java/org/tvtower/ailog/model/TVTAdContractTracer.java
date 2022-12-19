package org.tvtower.ailog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.tvtower.statistics.StatLine;

class TVTAdContractTracer {

	private boolean hasContent = false;
	private List<String> interestingLines = new ArrayList<>();
	private List<StatLine> trailerOrOutage = new ArrayList<>();
	private List<StatLine> failed = new ArrayList<>();

	public boolean hasContent() {
		return hasContent;
	}

	public String getInfo() {
		StringBuilder b = new StringBuilder();
		addInfo("failed ad spots:", failed, b);
		addInfo("trailer/outage:", trailerOrOutage, b);
		if (!interestingLines.isEmpty()) {
			b.append("further ad events:\n");
			b.append(String.join("\n", interestingLines));
		}
		return b.toString();
	}

	private void addInfo(String type, List<StatLine> lines, StringBuilder b) {
		if(!lines.isEmpty()) {
			b.append(type);
			final AtomicInteger currentDay=new AtomicInteger(-1);
			lines.forEach(l -> {
				if(currentDay.get()!=l.day) {
					b.append(String.format("\n  day %d hour %d", l.day, l.hour));//TODO string format padding
					currentDay.set(l.day);
				}else {
					b.append(", "+l.hour);
				}
			});
			b.append("\n");
		}
	}

	public void setStats(List<StatLine> list) {
		addStat(list, -1, failed);
		addStat(list, 0, trailerOrOutage);// TODO distinguish?
	}

	private void addStat( List<StatLine> list, int income,List<StatLine> targetList) {
		targetList.addAll(list.stream().filter(l -> l.income == income).collect(Collectors.toList()));
		if (targetList.size() > 0) {
			hasContent = true;
		}
	}
}
