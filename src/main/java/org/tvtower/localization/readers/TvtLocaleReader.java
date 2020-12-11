package org.tvtower.localization.readers;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.tvtower.localization.model.LocaleInfo;

public class TvtLocaleReader {

	private List<LocaleInfo> locales=new ArrayList<>();
	private List<String> keyPrefixes;
	private Set<String> usedPrefixes=new HashSet<>();
	private Set<File> filesContainingKeys=new HashSet<>();

	public TvtLocaleReader() {
		try {
			keyPrefixes=Files.readAllLines(new File("src/main/resources/prefixes.txt").toPath());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void storeContent(String baseDir) throws IOException {
		File dir = new File(baseDir);
		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(file.getFileName().toString().endsWith(".txt")) {
					storeContent(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public List<LocaleInfo> getLocales() {
		return locales;
	}

	private void storeContent(Path path) {
		try {
			locales.add(new LocaleInfo(path.toFile()));
		} catch (IOException e) {
			throw new UncheckedIOException("unable to read "+path, e);
		}
	}

	/**
	 * try to find keys that are not used in source code
	 * */
	public void checkUsageInGerman(Map<File, String> code, boolean printUsage) {
		System.out.println("keys not found as such in source code");
		usedPrefixes.clear();
		filesContainingKeys.clear();
		Set<String>germanKeys=getGermanKeys().stream().map(s->s.toString()).collect(Collectors.toSet());
		for (String key : germanKeys) {
			if(checkKey(key)) {
				boolean found=false;
				String keyToCheck=stripDigits(key);
				for (Entry <File, String> e : code.entrySet()) {
					if(e.getValue().contains("\""+keyToCheck+"\"")) {
//						System.out.println(keyToCheck+ " "+e.getKey());
						found=true;
						filesContainingKeys.add(e.getKey());
						continue;
					} else if(e.getValue().contains("\""+keyToCheck.toLowerCase()+"\"")) {
//						System.out.println("lowerCaseKey used: "+keyToCheck + " in "+e.getKey());
//						filesContainingKeys.add(e.getKey());
						//TODO store those and check
						found=true;
						continue;
					}
				}
				if(!found) {
					System.out.println("  "+key);
				}
			}
		}
		if(printUsage) {
			System.out.println("\n\nFiles containing the keys:");
			filesContainingKeys.forEach(f->System.out.println("  "+f.getName()));
			System.out.println("\n\nUsed prefixes");
			System.out.println("  "+usedPrefixes);
			//TODO print files with used prefixes
		}
	}

	private boolean checkKey(String key) {
		//Job_ contains JOB_AMATEUR, JOB_ALL TODO analyse all...
		for (String prefix : keyPrefixes) {
			if (key.startsWith(prefix)) {
				usedPrefixes.add(prefix);
				return false;
			}
		}
		return true;
	}

	//Strip Numbers for random locale
	private String stripDigits(String key) {
		String result=key;
		while(Character.isDigit(result.charAt(result.length()-1))) {
			result=result.substring(0, result.length()-1);
		}
		return result;
	}

	private Set<Object> getGermanKeys(){
		Set<Object> germanKeys=new HashSet<>();
		locales.stream().filter(l->l.isGerman()).forEach(l->germanKeys.addAll(l.getProperties().keySet()));
		return germanKeys;
	}
}
