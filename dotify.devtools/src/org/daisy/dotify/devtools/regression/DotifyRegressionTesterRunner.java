package org.daisy.dotify.devtools.regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.daisy.braille.api.table.BrailleConverter;
import org.daisy.dotify.devtools.jvm.ProcessStarter;

public class DotifyRegressionTesterRunner implements RegressionInterface {
	private String argsSeparator = "\\t";
	private final String pathToDotifyCli;
	private final int maxThreads;
	private final File pathToOutput;
	private final String setup, locale;
	private final BrailleConverter table;
	private int timeout = 60;
	private int threads;
	private boolean haltOnError = true;
	private final File pathToCommandsList;
	private final List<ProcessStarter> pool;
	private boolean errors;

	public DotifyRegressionTesterRunner(File commandList, String pathToCli, File pathToOutput, String setup, String locale, BrailleConverter table) {
		this.pathToCommandsList = commandList;
		this.pathToDotifyCli = pathToCli;
		this.maxThreads = Runtime.getRuntime().availableProcessors();
		this.threads = maxThreads;
		this.pathToOutput = pathToOutput;
		this.setup = setup;
		this.locale = locale;
		this.table = table;
		Logger.getLogger(this.getClass().getCanonicalName()).info("Default is " + threads + " threads.");
		if (!pathToCommandsList.isFile()) {
			System.out.println("Cannot find file: " + pathToCommandsList);
			System.exit(-1);
		}
		if (!pathToOutput.isDirectory()) {
			System.out.println("Output is not a directory.");
			System.exit(-2);
		}
		pool = Collections.synchronizedList(new ArrayList<ProcessStarter>());
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		if (threads > 0 && threads < maxThreads) {
			Logger.getLogger(this.getClass().getCanonicalName()).info("Setting number of threads to " + threads + ".");
			this.threads = threads;
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Unable to set number of threads to " + threads + ". Resetting to default value: " + maxThreads);
			this.threads = maxThreads;
		}
	}

	public boolean isHaltOnError() {
		return haltOnError;
	}

	public void setHaltOnError(boolean haltOnError) {
		this.haltOnError = haltOnError;
	}

	public void run() throws IOException {
		errors = false;
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathToCommandsList)));
		String line;

		ExecutorService exe = Executors.newFixedThreadPool(threads);
		try {
			while ((line = in.readLine()) != null && (!errors || !haltOnError)) {
				final String line2 = line;
				if (line2.trim().equals("")) {
					// ignore
				} else if (line2.trim().startsWith("#")) {
					Logger.getLogger(this.getClass().getCanonicalName()).warning("Ignoring line: " + line2);
				} else {
					String[] args = line2.split(argsSeparator);
					exe.execute(new DotifyRegressionTester(this,
							new File(pathToCommandsList.getParentFile(), args[0]),
							new File(pathToCommandsList.getParentFile(), args[1]),
							(args.length>2?args[2]:setup), (args.length>3?args[3]:locale), table));
				}
			}
			exe.shutdown();
			try {
				while (!exe.isTerminated() && (!errors || !haltOnError)) {
					Thread.sleep(1000);
				}
				if (errors) {
					exe.shutdownNow();
				}
				//probably not need anymore
				exe.awaitTermination(timeout * 60, TimeUnit.SECONDS);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} finally {
			in.close();
			if (errors) {
				throw new IOException("Errors in test.");
			}
		}
	}


	@Override
	public ProcessStarter requestStarter() {
		try {
			return pool.remove(0);
		} catch (IndexOutOfBoundsException e) {
			return new ProcessStarter();
		}
	}

	@Override
	public void returnStarter(ProcessStarter starter) {
		pool.add(starter);
	}

	@Override
	public String getPathToCLI() {
		return pathToDotifyCli;
	}

	@Override
	public File testOutputFolder() {
		return pathToOutput;
	}

	@Override
	public void reportError() {
		errors = true;
	}
}
