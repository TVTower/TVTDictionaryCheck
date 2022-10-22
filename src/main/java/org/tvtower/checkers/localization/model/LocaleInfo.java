package org.tvtower.checkers.localization.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LocaleInfo {

	private String lang;
	private File file;
	private Properties properties;

	public LocaleInfo(File f) throws FileNotFoundException, IOException {
		this.file = f;
		Properties prop = new Properties();
		prop.load(new FileReader(file));
		String name = file.getName();
		if (name.indexOf('_') > 0) {
			lang = name.substring(name.indexOf('_') + 1, name.length() - 4);
		} else {
			lang = file.getParentFile().getName();
		}
		properties = prop;
	}

	public boolean isGerman() {
		return "de".equals(lang);
	}

	public File getFile() {
		return file;
	}

	public String getLang() {
		return lang;
	}

	public Properties getProperties() {
		return properties;
	}
}