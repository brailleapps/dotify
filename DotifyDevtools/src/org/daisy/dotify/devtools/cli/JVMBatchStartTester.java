package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.daisy.dotify.devtools.jvm.BatchProcessStarter;

public class JVMBatchStartTester {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Not enough arguments...");
			System.exit(-1);
		}
		// Convert to PEF based on file list
		File pathToCommandsList = new File(args[0]);
		if (pathToCommandsList.isFile()) {
			int threads = 0;
			if (args.length >= 2) {
				try {
					threads = Integer.parseInt(args[1]);
				} catch (Exception e) {

				}
			}
			BatchProcessStarter starter = new BatchProcessStarter(threads, true);
			starter.run(new FileInputStream(pathToCommandsList));
		} else {
			System.out.println("Cannot find file: " + args[0]);
			System.exit(-10);
		}

	}

}
