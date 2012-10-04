package org.daisy.dotify.devtools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.daisy.dotify.devtools.PEFFileCompare.NormalizationResource;

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
		PEFFileCompare fc = new PEFFileCompare(new PEFFileFilter(), new NormalizationResource() {
			@Override
			public InputStream getNormalizationResourceAsStream() {
				return this.getClass().getResourceAsStream("resource-files/strip-meta.xsl");
			}
		});
		System.out.println("Running...");
		fc.run(args[0], args[1]);
		System.out.println("Done.");
		if (fc.getNotices().size()>0) {
			System.out.println();
			System.out.println("--- Notices ---");
			for (String msg : fc.getNotices()) {
				System.out.println("Notice: " + msg);
			}
		}
		if (fc.getWarnings().size()>0) {
			System.out.println();
			System.out.println("--- Warnings ---");
			for (String msg : fc.getWarnings()) {
				System.out.println("Warning: " + msg);
			}
		}
		if (fc.getDiffs().size()>0) {
			System.out.println();
			System.out.println("--- Differences ---");
			for (String filename : fc.getDiffs()) {
				System.out.println(filename);
			}
		}
		System.out.println();
		if (fc.getOk().size()!=fc.checkedCount()) {
			System.out.println("Checked " + fc.checkedCount() + " file(s), but only " + fc.getOk().size() + " were ok.");
		} else {
			System.out.println("No differences was found!");
		}
	}
	
	private static class PEFFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".pef") && !pathname.isDirectory();
		}
	}
}