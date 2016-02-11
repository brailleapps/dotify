package org.daisy.dotify.devtools.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.daisy.braille.api.table.BrailleConverter;
import org.daisy.braille.pef.FileTools;
import org.daisy.braille.pef.PEFFileCompare;
import org.daisy.dotify.common.io.FileIO;
import org.daisy.dotify.devtools.jvm.ProcessStarter;
import org.daisy.dotify.devtools.unbrailler.Unbrailler;

class DotifyRegressionTester implements Runnable {
	private final RegressionInterface inf;
	private final File input, expected;
	private final String setup, locale, ext;
	private final BrailleConverter table;
	private final boolean folders = true;

	
	public DotifyRegressionTester(RegressionInterface inf, File input, File expected, String setup, String locale, BrailleConverter table) {
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

	@Override
	public void run() {
		ProcessStarter starter = inf.requestStarter();
		try {
			File res = File.createTempFile("reg-test", ext);
			boolean ok = false;
			try {

				ArrayList<String> command = new ArrayList<>();
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
					if (System.getProperty("org.daisy.dotify.devtools.regression.baseline", "compare").equals("update")) {
						ok = true;
						//Overwrite baseline
						FileTools.copy(new FileInputStream(res), new FileOutputStream(expected));
					} else {
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
								expectedCopy = buildResultFile("expected");
								FileTools.copy(new FileInputStream(expected), new FileOutputStream(expectedCopy));
							} catch (Exception e) {
								copyFailed = true;
								e.printStackTrace();
							}
	
							try {
								// If that works, see if we can copy the result
								actual = buildResultFile("actual");
								FileTools.copy(new FileInputStream(res), new FileOutputStream(actual));
							} catch (Exception e) {
								copyFailed = true;
								e.printStackTrace();
							}
	
							if (".pef".equalsIgnoreCase(ext) && !copyFailed) {
								//Now, try to convert this into readable characters
								Unbrailler ub = new Unbrailler(table);
								ub.convert(expectedCopy, new File(buildResultFolder("ub-expected"), replaceSuffix(expectedCopy.getName(), ".xml")));
								ub.convert(actual, new File(buildResultFolder("ub-actual"), replaceSuffix(actual.getName(), ".xml")));
							}
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
	
	private String replaceSuffix(String in, String suffix) {
		int inx = in.lastIndexOf('.');
		if (inx>-1) {
			return in.substring(0, inx) + suffix;
		} else {
			return in + suffix;
		}
	}
	
	private File buildResultFolder(String prefix) {
		File ret = new File(inf.testOutputFolder(), prefix);
		ret.mkdirs();
		return ret;
	}
	
	private File buildResultFile(String prefix) {
		if (folders) {
			return new File(buildResultFolder(prefix), replaceSuffix(input.getName(), ext));
		} else {
			return new File(inf.testOutputFolder(), prefix + "-" + replaceSuffix(input.getName(), ext));
		}
	}


}
