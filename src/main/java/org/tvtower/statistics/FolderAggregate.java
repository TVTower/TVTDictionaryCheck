package org.tvtower.statistics;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FolderAggregate {

	private List<FileAggregate> files=new ArrayList<>();
	private String name;
	private List<ReachAggregate> reaches=new ArrayList<>();
	private List<String> summary;
	private File summaryFile;

	public FolderAggregate(File baseFolder, String subFolder) {
		this.name=subFolder;
		File folder = new File(baseFolder, subFolder);
		summaryFile=new File(folder, name+"_summary.txt");
		File[] files = folder.listFiles();
		for (File file : files) {
			if(file.getName().endsWith(".csv")) {
				this.files.add(new FileAggregate(file));
			}
		}
		reaches.add(new ReachAggregate(0, 1250000));
		reaches.add(new ReachAggregate(1250000, 2500000));
		reaches.add(new ReachAggregate(2500000,5000000));
		reaches.add(new ReachAggregate(5000000,10000000));
		reaches.add(new ReachAggregate(10000000, 20000000));
		reaches.add(new ReachAggregate(20000000, 30000000));
		reaches.add(new ReachAggregate(30000000, 40000000));
		reaches.add(new ReachAggregate(40000000, 60000000));
		reaches.add(new ReachAggregate(60000000, 80000000));

		reaches.forEach(r->{
			this.files.forEach(f->{
				f.getLines().forEach(l->r.add(l));
			});
		});
	}

	public void startSummary() {
		summary=new ArrayList<>();
	}

	public void done() {
		try {
			Files.write(summaryFile.toPath(), summary,StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void print() {
		reaches.forEach(r->summary.addAll(r.print(-1)));
	}

	public void printReach(int reach) {
		reaches.stream().filter(r-> r.matchesReach(reach)).forEach(r->summary.addAll(r.print(reach)));
	}
}
