package org.tvtower.ailog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.tvtower.ailog.model.TVTAllLogs;
import org.tvtower.checkers.readers.Directories;

public class TVTLogsAnalyzer {

	public static void main(String[] args) {
		String logDir = Directories.getLogDir(Directories.TVT_BASE_DIR);

		List<TVTAllLogs> allLogs = new ArrayList<>();
		allLogs.add(new TVTAllLogs(logDir));
		File dir = new File(logDir);
		for (File subDir : dir.listFiles()) {
			if (subDir.isDirectory()) {
				allLogs.add(new TVTAllLogs(subDir.getAbsolutePath()));
			}
		}

		for (TVTAllLogs logs : allLogs) {
//			logs.analyzePerformance(-1, 1, true);
//			logs.analyzePerformance(-1, 5, true);
			logs.analyzePerformance(-1, 10, true); //day 10 as indicator of performance early in the game
			logs.analyzePerformance(-1, 50, false);
//			logs.showTaskOverview(-1, 50);
			logs.done();
		}

	}

	// TODO:
	// * Betty noch nicht Teil der Auswertung
	// * interessante Teile aus PlayerStat parsen
}
