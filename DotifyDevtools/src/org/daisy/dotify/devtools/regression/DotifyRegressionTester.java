package org.daisy.dotify.devtools.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.daisy.braille.tools.FileTools;
import org.daisy.dotify.devtools.compare.PEFFileCompareCore;
import org.daisy.dotify.devtools.jvm.JVMStarter;
import org.daisy.dotify.devtools.unbrailler.Unbrailler;

class DotifyRegressionTester implements Runnable {
	private final RegressionInterface inf;
	private final File input, expected;
	private final String setup, locale, table;

	
	public DotifyRegressionTester(RegressionInterface inf, File input, File expected, String setup, String locale, String table) {
		this.inf = inf;
		if (!input.isFile()) {
			throw new IllegalArgumentException("Input does not exist or is not a file: " + input);
		}
		this.input = input;
		if (!expected.isFile()) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Comparison file does not exist or is not a file: " + expected);
		}
		this.expected = expected;
		this.setup = setup;
		this.locale = locale;
		this.table = table;
	}

	@Override
	public void run() {
		JVMStarter starter = inf.requestStarter();
		try {
			File res = File.createTempFile("reg-test", ".pef");

			try {
				ArrayList<String> command = new ArrayList<String>();
				command.add("-jar");
				command.add(inf.getPathToCLI());
				command.add(input.getAbsolutePath());
				command.add(res.getAbsolutePath());
				command.add(setup);
				command.add(locale);

				starter.startNewJVMSync(command.toArray(new String[command.size()]));

				if (!res.isFile()) {
					if (expected.isFile()) {
						inf.reportError();
					} else {
						// ok
					}
				} else {

					PEFFileCompareCore core = new PEFFileCompareCore();

					if (!core.compare(res.getAbsoluteFile(), expected)) {
						// if not equal, write to output folder
						Unbrailler ub = new Unbrailler(table);
						File actual = new File(inf.testOutputFolder(), "actual-" + input.getName() + ".pef");
						File expectedCopy = new File(inf.testOutputFolder(), "expected-" + input.getName() + ".pef");
						FileTools.copy(new FileInputStream(res), new FileOutputStream(actual));
						FileTools.copy(new FileInputStream(expected), new FileOutputStream(expectedCopy));

						ub.convert(actual);
						ub.convert(expectedCopy);
						// set error
						inf.reportError();
					} else {
						// ok
					}
				}
			} finally {
				if (!res.delete()) {
					res.deleteOnExit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		inf.returnStarter(starter);
	}


}
