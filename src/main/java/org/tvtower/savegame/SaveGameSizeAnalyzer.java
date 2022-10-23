package org.tvtower.savegame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SaveGameSizeAnalyzer {

	static Map<String, SaveGameInfo>infos=new TreeMap<>();

	public static void main(String[] args) throws IOException {
		File[] games = new File("results/savegames/1985_normal").listFiles();
		SaveGameInfo last=null;
		for (File game : games) {
			SaveGameInfo info = new SaveGameInfo(game);
			infos.put(game.getName(), info);
			last=info;
		}
		print(last.getFields());
//		new SaveGameInfo(new File("savegames/fastNew1/AI-day-05.xml"));
	}

	private static void print(List<String> fields) throws IOException {
		List<String> lines=new ArrayList<>();
		List<String> files=new ArrayList<>(infos.keySet());
		lines.add(";"+String.join(";", files));
		for (String field : fields) {
			String csv=field+";";
			csv=csv+files.stream().map(f->""+infos.get(f).get(field)).collect(Collectors.joining(";"));
			lines.add(csv);
		}
		lines.forEach(l->System.out.println(l));
		Files.write(new File("info.csv").toPath(), lines);
	}
}
