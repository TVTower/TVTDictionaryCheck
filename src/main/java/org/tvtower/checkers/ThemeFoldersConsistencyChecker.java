package org.tvtower.checkers;

import java.io.IOException;

import org.tvtower.checkers.localization.model.ThemeFolders;
import org.tvtower.checkers.readers.Directories;

//TODO check duplicate keys
//TODO check duplicate values (combine keys possible?)
public class ThemeFoldersConsistencyChecker {

	/**
	 * check if localization directories are consistent - same keys defined everywhere
	 * */
	public static void main(String[] args) {
		checkThemeFoldersContent(Directories.TVT_BASE_DIR);
		//checkThemeFoldersAgainstOriginalFiles(Directories.TVT_BASE_DIR);
	}

	private static void checkThemeFoldersContent(String baseDir) {
		String dirToCheck=Directories.getLocalizationDir(baseDir);
		ThemeFolders folders=new ThemeFolders(dirToCheck);
		System.out.println("checking "+dirToCheck+" for consistency");
		folders.check();
	}

	//still present for historic reasons - indicates how the original refactoring was supported
	//making sure that moving translations around did not change them
	@SuppressWarnings("unused")
	private static void checkThemeFoldersAgainstOriginalFiles(String baseDir) {
		ThemeFolders folders=new ThemeFolders(Directories.getLocalizationDir(baseDir));
		try {
			folders.checkThemeAgainstOriginal("src/main/resources/originalLanguageFiles");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
