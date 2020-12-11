package org.tvtower.localization.checkers;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.tvtower.localization.readers.TvtLocaleReader;
import org.tvtower.localization.readers.TvtSourceFileReader;

//TODO add key usage exclude file - do not report keys that have been checked manually
public class KeyUsageChecker {

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
		String sourceDir = Directories.getSourceDir(baseDir);
		try {
			TvtSourceFileReader codeReader = new TvtSourceFileReader();
			codeReader.storeContent(sourceDir);
			TvtLocaleReader localeReader = new TvtLocaleReader();
			localeReader.storeContent(langDirToCheck);
			//as property files are consisten, check german keys only
			localeReader.checkUsageInGerman(codeReader.getCode(), printUsage);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
