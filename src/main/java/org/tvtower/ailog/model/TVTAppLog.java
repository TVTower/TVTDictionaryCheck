package org.tvtower.ailog.model;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TVTAppLog {

	// playerId,day->playerDay
	// for adding data to the current player day
	private BiFunction<Integer, Integer, TVTAiPlayerDay> playerdayFct;

	private int currentPlayer = -1;
	private int currentDay = -1;
	private boolean finStat = false;

	public static void parse(File appLogFile, BiFunction<Integer, Integer, TVTAiPlayerDay> playerDayFct) {
		final TVTAppLog log = new TVTAppLog();
		log.playerdayFct = playerDayFct;
		try {
			Files.lines(appLogFile.toPath(), StandardCharsets.ISO_8859_1).forEach(l -> log.parse(l));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	// TODO
	// * financial stats per day
	// * programme stats
	private void parse(String l) {
		if (l.contains("Finance Stats")) {
			finStat = true;
			Pattern p = Pattern.compile(".*player #(\\d) on day (\\d+) .*");
			Matcher m = p.matcher(l);
			if (!m.find()) {
				throw new IllegalStateException("financial stat pattern broken");
			}
			currentPlayer = Integer.parseInt(m.group(1));
			currentDay = Integer.parseInt(m.group(2));
			return;
		}
		if (finStat) {
			handleFinStatLine(l);
		}
	}

	private void handleFinStatLine(String l) {
		TVTAiPlayerDay dayData = playerdayFct.apply(currentPlayer, currentDay);
		if (dayData != null) {
			if (l.contains("Werbeeinkuenfte")) {
				dayData.dayAdIncome = finStatAmound(l, 1);
				dayData.dayFailedAdConsts = finStatAmound(l, 2);
				dayData.totalAdIncome = finStatAmound(l, 3);
				dayData.totalFailedAdsCost = finStatAmound(l, 4);
			}
			if(l.contains("Money:")) {
				dayData.currentMoney=finStatAmound(l.replace("Money:", ""), 0);
			}
		} else {
			throw new IllegalStateException("no day data found");
		}
		if (l.contains("| Total")) {
			finStat = false;
		}
	}

	private int finStatAmound(String line, int index) {
		int start = line.indexOf(": |");
		if (start > 0) {
			String split[] = line.substring(start + 4).split("\\|");
			return Integer.parseInt(split[index].trim().replaceAll("\\.", ""));
		} else {
			throw new IllegalStateException("table start not found in " + line);
		}
	}

}
