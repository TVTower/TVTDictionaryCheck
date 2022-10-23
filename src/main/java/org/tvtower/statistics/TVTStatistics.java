package org.tvtower.statistics;

import java.io.File;
import java.util.ArrayList;

import org.tvtower.statistics.ReachAggregate.PrintHourMode;

public class TVTStatistics {

	public static void main(String[] args) {
		File baseFolder=new File("results/statistics");

		ReachAggregate.printHourMode=PrintHourMode.prime;
		ReachAggregate.showNormalizedToReach=false;
		analyseFolder(baseFolder);
	}

	private static void analyseFolder(File folder) {
		File[] content = folder.listFiles();
		ArrayList<FolderAggregate> aggreagates = new ArrayList<FolderAggregate>();
		for (File file : content) {
			if(file.isDirectory()) {
				analyseFolder(file);
				FolderAggregate agg = new FolderAggregate(folder, file.getName());
				aggreagates.add(agg);
			}
		}
		aggreagates.forEach(agg->{
			agg.startSummary();
			printAll(agg);
//				printSelectedReaches(agg);
			agg.done();
		});
	}

	private static void printAll(FolderAggregate agg) {
		agg.print();
	}

	private static void printSelectedReaches(FolderAggregate agg) {
		agg.printReach(100000);
//		agg.printReach(2000000);
//		agg.printReach(3000000);
		agg.printReach(5000000);
		agg.printReach(10000000);
//		agg.printReach(30000000);
	}

}
