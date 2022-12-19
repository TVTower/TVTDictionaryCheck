package org.tvtower.ailog;

import org.tvtower.ailog.model.TVTAiLogs;
import org.tvtower.checkers.readers.Directories;

public class TvtAiLogsAnalyzer {

	public static void main(String[] args) {
		TVTAiLogs logs = new TVTAiLogs();
		logs.analyze(Directories.getLogDir(Directories.TVT_BASE_DIR));
	}

	// TODO:
	// * Zeit pro Job
	// * Gruppieren
	// * Anzahl Tasks pro Tag (Vergleich)
}
