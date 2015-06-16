package org.daisy.dotify.devtools.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.daisy.braille.pef.FileTools;
import org.daisy.braille.pef.PEFFileCompare;
import org.daisy.dotify.devtools.jvm.ProcessStarter;
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

	public void run() {
		ProcessStarter starter = inf.requestStarter();
		try {
			File res = File.createTempFile("reg-test", ".pef");
			boolean ok = false;
			try {

				ArrayList<String> command = new ArrayList<String>();
				boolean jar = inf.getPathToCLI().toLowerCase().endsWith(".jar");
				if (jar) {
					command.add("-jar");
				}
				command.add(inf.getPathToCLI());
				if (System.getProperty("org.daisy.dotify.devtools.regression.mode", "legacy").equals("convert")) {
					command.add("convert");
				}
				command.add(input.getAbsolutePath());
				command.add(res.getAbsolutePath());
				command.add(setup);
				command.add(locale);
				res.delete();
				
				starter.startProcess(jar?
							ProcessStarter.buildJavaCommand(command.toArray(new String[command.size()])):
							command.toArray(new String[command.size()]));
				if (res.isFile()) {
					//We have a result
					PEFFileCompare core = new PEFFileCompare();
					try {
						ok = core.compare(res.getAbsoluteFile(), expected);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!ok) { // if not equal, write to output folder

						// Make sure we at least have the expected copy
						File expectedCopy = new File(inf.testOutputFolder(), "expected-" + input.getName() + ".pef");
						FileTools.copy(new FileInputStream(expected), new FileOutputStream(expectedCopy));

						// If that works, see if we can copy the result
						File actual = new File(inf.testOutputFolder(), "actual-" + input.getName() + ".pef");
						FileTools.copy(new FileInputStream(res), new FileOutputStream(actual));

						//Now, try to convert this into readable characters
						Unbrailler ub = new Unbrailler(table);
						ub.convert(expectedCopy);
						ub.convert(actual);
					}
				} else if (!expected.isFile()) {
					//Nothing to compare with, meaning this input should fail.
					ok = true;
				}
			} finally {
				if (!ok) {
					inf.reportError();
				}
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
