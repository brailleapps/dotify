package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import org.daisy.braille.pef.NormalizationResource;
import org.daisy.braille.pef.PEFFileBatchCompare;
import org.daisy.braille.pef.PEFFileBatchCompare.Diff;

public class PEFFileCompareTester {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.out.println("Not enough arguments...");
			System.exit(-1);
		}
		// Run pef-compare against baseline and unbraille if different
		PEFFileBatchCompare fc = new PEFFileBatchCompare(new PEFFileFilter(), new NormalizationResource() {

			@Override
			public InputStream getNormalizationResourceAsStream() {
				return this.getClass().getResourceAsStream("resource-files/strip-meta.xsl");
			}
		});

		fc.run(args[1], args[2]);

		/*
		Unbrailler ub = null;
		if (args[0] != null && !"".equals(args[0])) {

			ub = new Unbrailler(args[0]);

		}*/
		if (fc.getDiffs().size() > 0) {
			System.out.println();
			System.out.println("--- Differences ---");
			for (Diff d : fc.getDiffs()) {
				/*
				if (ub != null) {
					ub.convert(new File(args[2], d.getKey()));
					ub.convert(new File(args[3], d.getKey()));
				}*/
				System.out.println(d.getKey() + " " + d.getPos());
			}
		}
		if (fc.getOk().size() != fc.checkedCount()) {
			System.out.println("Checked " + fc.checkedCount() + " file(s), but only " + fc.getOk().size() + " were ok.");
			System.exit(-10);
		}

	}

	private static class PEFFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".pef") && !pathname.isDirectory();
		}
	}

}
