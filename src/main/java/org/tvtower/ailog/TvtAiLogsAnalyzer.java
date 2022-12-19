package org.tvtower.ailog;

import org.tvtower.ailog.model.TVTAiLogs;
import org.tvtower.checkers.readers.Directories;

public class TvtAiLogsAnalyzer {

	public static void main(String[] args) {
		TVTAiLogs logs = new TVTAiLogs();
		logs.analyze(Directories.getLogDir(Directories.TVT_BASE_DIR));
	}

	// TODO:
	// * Zeit Start bis Raum betreten
	// * Zeit pro Job
	// * Zeit für Taskausführung
	// * Gruppieren
	// * Anzahl Tasks pro Tag (Vergleich)
	// * Zeit bis Raum betreten (Vergleich)
}
