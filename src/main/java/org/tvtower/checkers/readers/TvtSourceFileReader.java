package org.tvtower.checkers.readers;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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

	public static TvtSourceFileReader get(String baseDir) {
		String sourceDir = Directories.getSourceDir(baseDir);
		try {
			TvtSourceFileReader codeReader = new TvtSourceFileReader();
			codeReader.storeContent(sourceDir);
			return codeReader;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

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

	public void printTime() {
		code.forEach((f,content)-> printTime(f,content));
	}

	private Object printTime(File f, String content) {
		String[] s = content.split("\n");
		for (String line : s) {
			String lower = line.toLowerCase();
			if(lower.contains("time") && (lower.contains("double") || lower.contains("float"))) {
				System.out.println(f.getName()+": "+line.trim());
			}
		}
		return null;
	}

	public void printPrint() {
		code.forEach((f,content)-> printPrint(f,content));
	}

	private Object printPrint(File f, String content) {
		String[] s = content.split("\n");
		for (String line : s) {
			String lower = line.toLowerCase();
			if(lower.contains("print")) {
				if(lower.indexOf("print")<=lower.indexOf('\'')) {
					System.out.println(f.getName()+": "+line.trim());
				}
			}
		}
		return null;
	}

	public void printLong() {
		code.forEach((f,content)-> printLong(f,content));
	}

	private Object printLong(File f, String content) {
		boolean print = false;
		String[] s = content.split("\n");
		for (String line : s) {
			String lower = line.toLowerCase();
			if(lower.trim().startsWith("type")) {
				print = lower.contains("expose") && !lower.contains("selected");
			}else {
				boolean isApi=lower.contains("method") || lower.contains("function");
				if(isApi &&lower.contains("long") && print) {
					System.out.println(f.getName()+": "+line.trim());
				}
			}
		}
		return null;
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
