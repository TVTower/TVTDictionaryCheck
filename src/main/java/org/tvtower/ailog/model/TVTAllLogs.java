package org.tvtower.ailog.model;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.tvtower.statistics.FileAggregate;

public class TVTAllLogs {

	private FileAggregate statAggregator;
	private Map<Integer, TVTAiPlayer> logs = new LinkedHashMap<>();
	private int lastDay=-1;

	public TVTAllLogs(String logDir) {
		File statisticsLog = new File(logDir, "statistic.csv");
		if (statisticsLog.exists()) {
			statAggregator = new FileAggregate(statisticsLog);
			lastDay= statAggregator.getLines().get(statAggregator.getLines().size()-1).day;
		}
		for (int i = 1; i <= 4; i++) {
			readAiLog(logDir, i);
		}
		File appLogFile = new File(logDir, "log.app.txt");
		if (appLogFile.exists()) {
			TVTAppLog.parse(appLogFile, (player, day) -> Objects.requireNonNull(logs.get(player)).getPlayerDay(day));
		}
	}

	private void readAiLog(String logDir, int player) {
		File logFile = new File(logDir, "log.ai" + player + ".txt");
		if (logFile.exists()) {
			try {
				final TVTAiPlayer log = new TVTAiPlayer(player);
				Files.lines(logFile.toPath(), StandardCharsets.ISO_8859_1).forEach(l -> log.addLine(l));
				logs.put(player, log);
				if (statAggregator != null) {
					log.setStatAggregator(statAggregator);
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	// TODO compare all in one table

	public void showTaskOverview(int startDay, int endDay) {
		analyze(startDay, endDay, (s, e) -> {
			logs.values().forEach(l -> l.showTaskOverview(s, e));
		});
	}

	public void analyzePerformance(int startDay, int endDay, boolean summaryOnly) {
		analyze(startDay, endDay, (s, e) -> {
			System.out.println("Overview up to day "+getLastDayWithData(endDay));
			List<List<String>> tableColums=new ArrayList<>();
			tableColums.add(TVTAiPlayer.overviewHeaders());
			logs.values().forEach(p->tableColums.add(p.getOverviewColumn(startDay, getLastDayWithData(endDay))));
			int lines=tableColums.get(0).size();
			for(int line=0; line<lines;line++) {
				final int lineToUse=line;
				String lineString=tableColums.stream().map(c->c.get(lineToUse)).collect(Collectors.joining("|"));
				System.out.println(lineString);
			}
			if(!summaryOnly) {
				System.out.println("\nSinglePlayers\n");
				logs.values().forEach(l -> l.analyze(s, e));
			}
		});
	}

	private int getLastDayWithData(int endDay) {
		if (lastDay>0) {
			if(endDay>0 && endDay<lastDay) {
				return endDay;
			}
			return lastDay;
		}else {
			throw new IllegalStateException("last day has to be determined from player data");
		}
	}

	private void analyze(int startDay, int endDay, BiConsumer<Integer, Integer> analyze) {
		final AtomicInteger start = new AtomicInteger(-1);
		final AtomicInteger end = new AtomicInteger(-1);
		if (startDay > 0) {
			start.set(startDay);
		}
		if (endDay > 0) {
			end.set(endDay);
		}
		if (startDay > 0 && endDay > 0 && endDay < startDay) {
			throw new IllegalArgumentException("startDay must not be after endDay");
		}
		analyze.accept(start.get(), end.get());
	}
}
