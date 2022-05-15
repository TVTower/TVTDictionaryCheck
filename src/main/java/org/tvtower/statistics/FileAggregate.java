package org.tvtower.statistics;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathVariableResolver;

public class FileAggregate {

	private List<StatLine> lines = new ArrayList<>();
	private Map<Integer, Player> players = new HashMap<>();

	public FileAggregate(File file) {
		read(file);
		// TODO fix costs
	}

	private void read(File file) {
		try {
			List<String> fileLines = Files.readAllLines(file.toPath());
			for (int i = 1; i < fileLines.size(); i++) {
				StatLine l = new StatLine(fileLines.get(i));
				players.computeIfAbsent(l.player, id -> new Player()).add(l);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		players.values().forEach(p -> this.lines.addAll(p.getLines()));
	}

	public List<StatLine> getLines() {
		return lines;
	}

	private class Player {
		private List<StatLine> lines = new ArrayList<>();
		private int currentDay = -1;
		private int previousDayReach = -1;
		private int previousDayCosts = -1;

		public void add(StatLine l) {
			if (l.day < currentDay) {
				throw new IllegalStateException();
			} else if (currentDay != l.day) {
				checkIfPreviousDayDataShouldUsed(l);
				currentDay=l.day;
				previousDayCosts=l.costs;
				previousDayReach=l.reach;
			}
			lines.add(l);
		}

		private void checkIfPreviousDayDataShouldUsed(StatLine l) {
//			System.out.println(previousDayReach+" "+l.reach);
			if(previousDayReach<0){
				//ignore
			}else if(Math.abs(previousDayReach-l.reach)<100000) {
				//costs were added although station was not active yet
				l.costs=previousDayCosts;
			}
		}

		public Collection<? extends StatLine> getLines() {
			return lines;
		}

	}
}
