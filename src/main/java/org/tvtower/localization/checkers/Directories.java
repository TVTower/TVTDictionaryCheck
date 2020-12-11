package org.tvtower.localization.checkers;

class Directories {

	//we assume that the git repos were cloned with the default names to the same base folder 
	static String TVT_BASE_DIR="../TVTower";

	static String getLocalizationDir(String baseDir) {
		return baseDir+"/res/lang";
	}

	static String getSourceDir(String baseDir) {
		return baseDir+"/source";
	}
}
