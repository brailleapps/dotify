package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.IOException;

import org.daisy.dotify.devtools.regression.DotifyRegressionTesterRunner;

public class DotifyRegressionTesterUI {

	/**
	 * First argument should point to a file containing paths to input xml
	 * and reference output. E.g. DTB00001/DTB00001.xml baseline/P1.pef
	 * Second argument should point to the CLI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 6) {
			System.out.println("Not enough arguments. Expected at least six arguments.");
			System.exit(-1);
		}
		try {
			DotifyRegressionTesterRunner rt = new DotifyRegressionTesterRunner(new File(args[0]), args[1], new File(args[2]), args[3], args[4], args[5]);
			if (args.length >= 7) {
				int thArg = 6;
				try {
					rt.setThreads(Integer.parseInt(args[thArg]));
				} catch (NumberFormatException e) {
					System.out.println(args[thArg] + " is not an integer.");
				}
			}
			rt.run();
		} catch (IOException e) {
			System.exit(-1);
		}
	}

}
