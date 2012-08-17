package org.daisy.dotify.devtools;

import java.io.FileNotFoundException;

/**
 * Provides a command line tool for comparing two folders with pef files for differences.
 * Meta data in the files is ignored when comparing.
 * A flat organization of files is assumed.
 * 
 * Warnings are generated if stray files are found (a file with the same name
 * cannot be found in the other folder) or if the folders contain other folders
 * or files not ending with '.pef'.
 * 
 * @author Joel Håkansson
 */
public class PEFFileCompareUI {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length!=2) {
			System.out.println("Expected two arguments: PEF_folder_path1 PEF_folder_path2");
			System.exit(-1);
		}
		System.out.println("Initiating...");
		PEFFileCompare fc = new PEFFileCompare(args[0], args[1]);
		System.out.println("Running...");
		fc.run();
		boolean ok = true;
		if (fc.getWarnings().size()>0) {
			ok = false;
			System.out.println();
			System.out.println("--- Warnings ---");
			for (String msg : fc.getWarnings()) {
				System.out.println("Warning: " + msg);
			}
		}
		if (fc.getDiffs().size()>0) {
			ok = false;
			System.out.println();
			System.out.println("--- Differences ---");
			for (String filename : fc.getDiffs()) {
				System.out.println(filename);
			}
		}
		if (!ok) {
			System.out.println();
		}
		System.out.println("Done.");
		if (ok) {
			if (fc.getOk().size()<fc.checkedCount()) {
				System.out.println("Checked " + fc.checkedCount() + " file(s), but only " + fc.getOk().size() + " were ok.");
			} else {
				System.out.println("Everything was ok!");
			}
		}
	}
}