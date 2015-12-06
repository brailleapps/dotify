package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import org.daisy.braille.pef.PEFFileBatchCompare;
import org.daisy.braille.pef.PEFFileBatchCompare.Diff;

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
		final String arg1, arg2;
		if (args.length < 2) {
			// "${folder_prompt}" "${folder_prompt}" x
			JFileChooser f1 = new JFileChooser();
			arg1 = getFilePath(f1);
			if (arg1 != null) {
				arg2 = getFilePath(f1);
			} else {
				arg2 = null;
			}
			if (arg1 == null || arg2 == null) {
				System.out.println("Expected two arguments: PEF_folder_path1 PEF_folder_path2 [table]");
				System.exit(-1);
			}
		} else {
			arg1 = args[0];
			arg2 = args[1];
		}
		System.out.println("Initiating...");
		PEFFileBatchCompare fc = new PEFFileBatchCompare(new PEFFileFilter());
		if (args.length >= 3) {
			fc.setUnbraillerTable(args[2]);
		}
		System.out.println("Running...");
		fc.run(arg1, arg2);
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
			for (Diff d : fc.getDiffs()) {
				System.out.println(d.getKey() + " " + d.getPos());
			}
		}
		System.out.println();
		if (fc.getOk().size()!=fc.checkedCount()) {
			System.out.println("Checked " + fc.checkedCount() + " file(s), but only " + fc.getOk().size() + " were ok.");
		} else {
			System.out.println("No differences was found!");
		}
	}
	
	private static String getFilePath(JFileChooser f1) {
		f1.setName("Chooooose folder");
		f1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (f1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return f1.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}

	private static class PEFFileFilter implements FileFilter {


		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".pef") && !pathname.isDirectory();
		}
	}
}