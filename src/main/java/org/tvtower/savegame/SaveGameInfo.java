package org.tvtower.savegame;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SaveGameInfo {

	Map<String, Integer> sizes = new LinkedHashMap<>();
	String fileName;

	public SaveGameInfo(File f) {
		try {
			String currentEntry = null;
			int currentCount = 0;
			List<String> lines = Files.readAllLines(f.toPath());
			for (String s : lines) {
				if (s.startsWith("    <field name")) {
					if (currentEntry != null) {
						sizes.put(currentEntry, currentCount);
					}
					currentEntry = getEntryName(s);
					currentCount=s.length();
				} else {
					currentCount+=s.length();
				}
			}
			sizes.put(currentEntry, currentCount);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		cleanUp();
//		lineCount.forEach((a,b)->{
//			System.out.println("   "+a +": "+b);
//		});
	}

	public int get(String key) {
		Integer result = sizes.get(key);
		if (result != null) {
			return result;
		} else {
			return -1;
		}
	}

	public List<String> getFields() {
		List<String> keys = new ArrayList<>(sizes.keySet());
		return keys;
	}

	private void cleanUp() {
		List<String> keys = getFields();
		for (String key : keys) {
			if (sizes.get(key) < 1000) {
				sizes.remove(key);
			}
		}
	}

	private String getEntryName(String e) {
		String truncated = e.substring(17);
		int index = truncated.indexOf('"');
		return truncated.substring(0, index);
	}
}
