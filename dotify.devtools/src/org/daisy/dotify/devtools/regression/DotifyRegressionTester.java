package org.daisy.dotify.devtools.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.daisy.braille.pef.FileTools;
import org.daisy.braille.pef.PEFFileCompare;
import org.daisy.dotify.common.io.FileIO;
import org.daisy.dotify.devtools.jvm.ProcessStarter;
import org.daisy.dotify.devtools.unbrailler.Unbrailler;

class DotifyRegressionTester implements Runnable {
	private final RegressionInterface inf;
	private final File input, expected;
	private final String setup, locale, table, ext;

	
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
		this.ext = expected.getName().substring(expected.getName().lastIndexOf('.'));
		if (!(".pef".equalsIgnoreCase(ext)|| ".obfl".equalsIgnoreCase(ext))) {
			throw new IllegalArgumentException("Unsupported extension: " + ext);
		}
	}

	public void run() {
		ProcessStarter starter = inf.requestStarter();
		try {
			File res = File.createTempFile("reg-test", ext);
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
					if (".pef".equalsIgnoreCase(ext)) {
						PEFFileCompare core = new PEFFileCompare();
						try {
							ok = core.compare(res.getAbsoluteFile(), expected);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (".obfl".equalsIgnoreCase(ext)) {
						try {
							//TODO: compare xml
							ok = -1 == FileIO.diff(new FileInputStream(expected), new FileInputStream(res.getAbsoluteFile()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (!ok) { // if not equal, write to output folder

						File expectedCopy = null;
						File actual = null;
						boolean copyFailed = false;
						try {
							// Make sure we at least have the expected copy
							expectedCopy = new File(inf.testOutputFolder(), "expected-" + input.getName() + ext);
							FileTools.copy(new FileInputStream(expected), new FileOutputStream(expectedCopy));
						} catch (Exception e) {
							copyFailed = true;
							e.printStackTrace();
						}

						try {
							// If that works, see if we can copy the result
							actual = new File(inf.testOutputFolder(), "actual-" + input.getName() + ext);
							FileTools.copy(new FileInputStream(res), new FileOutputStream(actual));
						} catch (Exception e) {
							copyFailed = true;
							e.printStackTrace();
						}

						if (".pef".equalsIgnoreCase(ext) && !copyFailed) {
							//Now, try to convert this into readable characters
							Unbrailler ub = new Unbrailler(table);
							ub.convert(expectedCopy);
							ub.convert(actual);
						}
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
