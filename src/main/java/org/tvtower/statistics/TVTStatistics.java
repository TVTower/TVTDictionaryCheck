package org.tvtower.statistics;

import java.io.File;
import java.util.ArrayList;

import org.tvtower.statistics.ReachAggregate.PrintHourMode;

public class TVTStatistics {

	public static void main(String[] args) {
		File baseFolder=new File("result/statistics");
		File[] subFolders = baseFolder.listFiles();
		ArrayList<FolderAggregate> aggreagates = new ArrayList<FolderAggregate>();

		ReachAggregate.printHourMode=PrintHourMode.prime;
		ReachAggregate.showNormalizedToReach=false;

		for (File file : subFolders) {
//			boolean printFile=contains(file, "CPM2", "CPM3");
			boolean printFile=contains(file, "95");
//			boolean printFile=true;
			if(printFile) {
				System.out.println(file);
				FolderAggregate agg = new FolderAggregate(baseFolder, file.getName());
				aggreagates.add(agg);

				printAll(agg);
//				printSelectedReaches(agg);

				System.out.println();
			}
		}
	}

	private static boolean contains(File folder, String ...segments) {
		String name = folder.getName();
		if(segments==null) {
			return true;
		}else {
			for (String segment : segments) {
				if(name.contains(segment)) {
					return true;
				}
			}
		}
		return false;
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
