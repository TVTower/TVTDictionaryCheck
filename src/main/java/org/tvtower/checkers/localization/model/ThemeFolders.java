package org.tvtower.checkers.localization.model;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.tvtower.checkers.readers.TvtLocaleReader;

public class ThemeFolders {

	List<ThemeFolder> folders = new ArrayList<>();

	public ThemeFolders(String languageDir) {
		File[] folderCandidates = new File(languageDir).listFiles();
		for (File file : folderCandidates) {
			if (file.isDirectory()) {
				folders.add(new ThemeFolder(file));
			}
		}
	}

	public void check() {
		if (folders.stream().allMatch(f -> f.checkKeysCoincide())) {
			System.out.println("Yes, keys and structure is consistent.");
			if(folders.stream().anyMatch(f -> !f.checkVariableUsage())) {
				System.out.println("But variables have to be checked.");
			}
		}else {
			System.err.println("Please ensure key end structure consistency!");
		}
	}

	/**
	 * check if translations in the themeFolders coincide with orignial translations
	 * 
	 * @throws IOException
	 */
	public void checkThemeAgainstOriginal(String origLangFileFolder) throws IOException {
		TvtLocaleReader origReader = new TvtLocaleReader();
		origReader.storeContent(origLangFileFolder);
		Map<String, Properties> origs = new HashMap<>();
		origReader.getLocales().forEach(l -> origs.put(l.getLang(), l.getProperties()));

		Map<String, Properties> themes = new HashMap<>();

		for (ThemeFolder folder : folders) {
			List<String> properties = folder.getProperties();
			Properties deOrig = origs.get("de");
			for (String prop : properties) {
				if (!deOrig.containsKey(prop)) {
					System.out.println("original does not contain property " + prop);
				}
			}
			folder.getFiles().forEach(f -> {
				LocaleInfo themeLocale;
				try {
					themeLocale = new LocaleInfo(f);
					checkNoChangesInTheme(themeLocale, origs.get(themeLocale.getLang()));
					folder.addProperties(themes, themeLocale);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}

		for (String lang : origs.keySet()) {
			System.out.println("\n\nCompleteness " + lang);
			Properties orig = origs.get(lang);
			Properties theme = themes.get(lang);
			orig.keySet().forEach(key -> {
				if (!theme.containsKey(key)) {
					System.out.println("  now missing " + key + " (was '" + orig.getProperty(key.toString()) + "')");
				}
			});
		}
	}

	private void checkNoChangesInTheme(LocaleInfo themeInfo, Properties orig) {
		Properties themeProps = themeInfo.getProperties();
//		System.out.println("processing "+themeInfo.getFile().getName());
//		String prefix="";
		String prefix = themeInfo.getFile().getName() + " ";
		themeProps.keySet().forEach(key -> {
			String themeTranslation = themeProps.getProperty(key.toString());
			String originalTranslation = orig.getProperty(key.toString());
			if (!themeTranslation.equals(originalTranslation)) {
				System.out.println("  translation mismatch " + prefix + key + " (now: '" + themeTranslation + "')");
			}
		});
	}
}
