package org.daisy.dotify.devtools.cli;

import java.io.IOException;


public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Not enough arguments");
			System.out.println(-1);
		} else {
			if (args[0].equals("-execute")) {
				JVMBatchStartTester.main(copyRange(args, 1, args.length));
			} else if (args[0].equals("-diff")) {
				PEFFileCompareTester.main(copyRange(args, 1, args.length));
			} else if (args[0].equals("-regression")) {
				DotifyRegressionTesterUI.main(copyRange(args, 1, args.length));
			} else {
				System.out.println("Unknown command.");
				System.exit(-2);
			}
		}

	}

	private static String[] copyRange(String[] args, int offs, int len) {
		String[] ret = new String[len - offs];
		System.arraycopy(args, offs, ret, 0, len - offs);
		return ret;
	}

}
