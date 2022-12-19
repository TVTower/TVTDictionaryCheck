		package org.tvtower.ailog;

import org.tvtower.ailog.model.TVTAllLogs;
import org.tvtower.checkers.readers.Directories;

public class TVTLogsAnalyzer {

	public static void main(String[] args) {
		String logDir=Directories.getLogDir(Directories.TVT_BASE_DIR);

		TVTAllLogs logs=new TVTAllLogs(logDir);
//		logs.analyzePerformance(-1, 1, true);
//		logs.analyzePerformance(-1, 5, true);
//		logs.analyzePerformance(-1, 10, true);
		logs.analyzePerformance(-1, -1, false);
//		logs.showTaskOverview(-1, 50);
	}

	// TODO:
	// * Kurzzusammenfassung (Anzahl outages, Anzahl failed
	// * Betty noch nicht Teil der Auswertung
	// * interessante Teile aus PlayerStat parsen
}
