package org.tvtower.localization.readers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TvtSourceFileReader {

	private Pattern p=Pattern.compile(".*GetLocale\\(.*");
	private Map<File, String> code=new HashMap<>();

	public void storeContent(String baseDir) throws IOException {
		File dir = new File(baseDir);
		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(file.getFileName().toString().endsWith(".bmx")) {
					storeContent(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void storeContent(Path file) {
		try {
			code.put(file.toFile(), new String(Files.readAllBytes(file)));
		} catch (IOException e) {
			System.err.println(file.getFileName()+": "+e.getMessage());
		}
	}

	//TODO find actual localisation usage in source
	@SuppressWarnings("unused")
	private void storeLocalisations(Path file) throws IOException {
//		System.out.println(file.getFileName());
		try {
			Files.readAllLines(file).forEach(l->{
				if(p.matcher(l).matches()) {
					String trimmed=l.trim();
					if(trimmed.charAt(0)!='\'') {
//						System.out.println("  "+l.trim());
					}
				}
			});
		}catch(MalformedInputException e) {
			System.err.println(file.getFileName()+": "+e.getMessage());
		}
	}

	public Map<File, String> getCode() {
		return code;
	}
}
