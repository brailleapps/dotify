package org.daisy.dotify.devtools.jvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class BatchProcessStarter {
	private final int threads;
	private final boolean javaCommand;
	private int timeout = 60;
	private String argsSeparator = "\\t";

	public BatchProcessStarter(boolean javaCommand) {
		this(0, javaCommand);
	}

	public BatchProcessStarter(int threads, boolean javaCommand) {
		this.threads = threads;
		this.javaCommand = javaCommand;
	}

	public String getArgsSeparator() {
		return argsSeparator;
	}

	public void setArgsSeparator(String argsSeparator) {
		this.argsSeparator = argsSeparator;
	}

	public boolean isJavaCommand() {
		return javaCommand;
	}

	/**
	 * Gets execution timeout, in minutes
	 * 
	 * @return returns timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the execution timeout, in minutes
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void run(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;

		ProcessStarterAsync starter = new ProcessStarterAsync(this.threads);
		try {
			while ((line = in.readLine()) != null) {
				final String line2 = line;
				starter.addProcess(
						javaCommand?
							ProcessStarter.buildJavaCommand(line2.split(argsSeparator)):
							line2.split(argsSeparator)
						);
			}
		} finally {
			starter.shutdown(timeout * 60, TimeUnit.SECONDS);
		}
	}

}
