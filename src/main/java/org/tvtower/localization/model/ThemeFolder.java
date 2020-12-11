package org.tvtower.localization.model;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ThemeFolder {

	private Map<File, List<String>> fileContent = new HashMap<>();
	//list of keys even if they are commented out
	private List<String> properties = new ArrayList<>();

	public ThemeFolder(File folder) {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				fileContent.put(file, content(file));
			}
		}
		//currently we do not care about the "base" language, as all files must completely coincide
		properties = fileContent.entrySet().iterator().next().getValue().stream()
				.filter(l -> !l.isEmpty() && !l.startsWith("##")).map(l -> propName(l))
				.map(p -> p.substring(0, p.length() - 2).trim()).collect(Collectors.toList());
	}

	public List<File> getFiles() {
		return new ArrayList<>(fileContent.keySet());
	}

	public List<String> getProperties() {
		return properties;
	}

	public boolean checkKeysCoincide() {
		final AtomicBoolean result=new AtomicBoolean(true);
		Iterator<Entry<File, List<String>>> iterator = fileContent.entrySet().iterator();
		Entry<File, List<String>> first = iterator.next();

		List<String> firstFileProps = first.getValue().stream().map(l -> propName(l)).collect(Collectors.toList());

		while (iterator.hasNext()) {
			Entry<File, List<String>> next = iterator.next();
			List<String> compare = next.getValue();
			if (firstFileProps.size() != compare.size()) {
				result.set(false);
				System.err.println("file size" + first.getKey() + " vs " + next.getKey());
			}
			//check keys only if the file size coincides
			if(result.get()) {
				for (int j = 0; j < compare.size(); j++) {
					String prop = propName(compare.get(j));
					if (!firstFileProps.get(j).equals(prop)) {
						result.set(false);
						System.err.println(next.getKey().getName() + " line" + " " + (j + 1) + " " + compare.get(j));
					}
				}
			}
		}
		return result.get();
	}

	public boolean checkVariableUsage() {
		Map<String, Properties> themes=new HashMap<>();
		getFiles().forEach(f->{
			LocaleInfo themeLocale;
			try {
				themeLocale = new LocaleInfo(f);
				addProperties(themes, themeLocale);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
		boolean result=true;
		result&=checkVariableUsage(themes);
		return result;
	}

	private boolean checkVariableUsage(Map<String, Properties> themes) {
		boolean result=true;
		Properties deProperties = themes.get("de");
		Set<Object> keys = deProperties.keySet();
		for (Object key : keys) {
			String value = deProperties.getProperty(key.toString());
			Set<String> variables=getVariables(value);
			for (String  language : themes.keySet()) {
				String lValue=themes.get(language).getProperty(key.toString());
				if (lValue != null) {
					Set<String> lVariables = getVariables(lValue);
					if (!variables.equals(lVariables)) {
						System.err.println("variable mismatch for " + key + " in language " + language);
						System.err.println("  "+ variables + "vs " + lVariables);
						result=false;
					}
				}
			}
		}
		return result;
	}

	private Pattern p=Pattern.compile(".*%(\\w*)%.*");
	private Set<String> getVariables(String localization){
		Set<String> result=new HashSet<>();
		Matcher matcher = p.matcher(localization);
		while(matcher.find()) {
			result.add(matcher.group(1));
		}
		if(!result.isEmpty()) {
//			System.out.println(localization);
//			System.out.println(result);
		}
		return result;
	}

	void addProperties(Map<String, Properties>props , LocaleInfo info) {
		Properties themeProps = props.get(info.getLang());
		if(themeProps==null) {
			themeProps=new Properties();
			props.put(info.getLang(), themeProps);
		}
		final Properties p=themeProps;
		info.getProperties().entrySet().stream().forEach(e->p.put(e.getKey() , e.getValue()));
	}

	private List<String> content(File f) {
		try {
			return Files.readAllLines(f.toPath());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String propName(String line) {
		if (line.isEmpty() || line.startsWith("##")) {
			return line;
		} else {
			String result = line.substring(0, line.indexOf('=') + 1);
			if (result.charAt(0) == '#') {
				result = result.substring(1);
			}
			return result;
		}
	}
}