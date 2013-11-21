package org.daisy.dotify.devtools.jvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class JVMBatchStarter {
	private final int threads;
	private int timeout = 60;
	private String argsSeparator = "\\t";

	public JVMBatchStarter() {
		this(0);
	}

	public JVMBatchStarter(int threads) {
		this.threads = threads;
	}

	public String getArgsSeparator() {
		return argsSeparator;
	}

	public void setArgsSeparator(String argsSeparator) {
		this.argsSeparator = argsSeparator;
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

		JVMStarterAsync starter = new JVMStarterAsync(this.threads);
		try {
			while ((line = in.readLine()) != null) {
				final String line2 = line;
				starter.addJob(line2.split(argsSeparator));
			}
		} finally {
			starter.shutdown(timeout * 60, TimeUnit.SECONDS);
		}
	}

}
