package org.tvtower.ailog.model;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TVTAiLogs {

	private List<TvtAiLog> logs = new ArrayList<>();

	public void analyze(String logDir) {
		for (int i = 1; i <= 4; i++) {
			readLog(logDir, i);
		}
	}

	private void readLog(String logDir, int player) {
		File logFile = new File(logDir, "log.ai" + player + ".txt");
		if (logFile.exists()) {
			try {
				TvtAiLog log = new TvtAiLog(player);
				List<String> lines = Files.readAllLines(logFile.toPath());
				for (String l : lines) {
					log.addLine(l);
				}
				logs.add(log);
				log.analyze();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
