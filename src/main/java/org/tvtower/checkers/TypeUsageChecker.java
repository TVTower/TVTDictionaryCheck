package org.tvtower.checkers;

import org.tvtower.checkers.readers.Directories;
import org.tvtower.checkers.readers.TvtSourceFileReader;

public class TypeUsageChecker {

	/**
	 * check type usage in source code files
	 * */
	public static void main(String[] args) {
		String baseDir = Directories.TVT_BASE_DIR;
		printTypeUsage(baseDir);
	}

	private static void printTypeUsage(String baseDir) {
		TvtSourceFileReader codeReader=TvtSourceFileReader.get(baseDir);
//		codeReader.printLong();
		codeReader.printPrint();
//		codeReader.printTime();
	}

}
