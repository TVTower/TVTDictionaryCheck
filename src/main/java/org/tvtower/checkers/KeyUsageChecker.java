package org.tvtower.checkers;

import org.tvtower.checkers.readers.Directories;
import org.tvtower.checkers.readers.TvtLocaleReader;
import org.tvtower.checkers.readers.TvtSourceFileReader;

//TODO add key usage exclude file - do not report keys that have been checked manually
public class KeyUsageChecker {

	/**
	 * Check usage of localization keys in source code
	 * */
	public static void main(String[] args) {
		String baseDir = Directories.TVT_BASE_DIR;
		String langDir = Directories.getLocalizationDir(baseDir);

		// maybe only one theme... +"/rooms"
		String languageDirToCheck = langDir;

		// print usage only for theme folder
		boolean printUsage = !languageDirToCheck.endsWith("lang");
		checkPropertiesUsedInBMX(baseDir, languageDirToCheck, printUsage);
	}

	private static void checkPropertiesUsedInBMX(String baseDir, String langDirToCheck, boolean printUsage) {
		TvtSourceFileReader codeReader=TvtSourceFileReader.get(baseDir);
		TvtLocaleReader localeReader = new TvtLocaleReader();
		localeReader.storeContent(langDirToCheck);
		//as property files are consisten, check german keys only
		localeReader.checkUsageInGerman(codeReader.getCode(), printUsage);

	}
}
