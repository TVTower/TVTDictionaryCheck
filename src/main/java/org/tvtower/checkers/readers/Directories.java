package org.tvtower.checkers.readers;

public class Directories {

	//we assume that the git repos were cloned with the default names to the same base folder 
	public static String TVT_BASE_DIR="../TVTower";

	public static String getLocalizationDir(String baseDir) {
		return baseDir+"/res/lang";
	}

	public static String getSourceDir(String baseDir) {
		return baseDir+"/source";
	}
}
