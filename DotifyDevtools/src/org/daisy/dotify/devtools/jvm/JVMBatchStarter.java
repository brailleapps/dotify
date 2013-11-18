package org.daisy.dotify.devtools.jvm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JVMBatchStarter {
	private String argsSeparator = "\\t";
	private int timeout = 60;
	private final int threads;

	public JVMBatchStarter() {
		this(Runtime.getRuntime().availableProcessors());
	}

	public JVMBatchStarter(int threads) {
		int t = Runtime.getRuntime().availableProcessors();
		if (threads > 0 && threads < t) {
			this.threads = threads;
		} else {
			this.threads = t;
		}
		Logger.getLogger(this.getClass().getCanonicalName()).info("Using " + this.threads + " threads.");
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

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void run(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		final List<JVMStarter> pool = Collections.synchronizedList(new ArrayList<JVMStarter>());
		ExecutorService exe = Executors.newFixedThreadPool(threads);
		while ((line = in.readLine()) != null) {
			final String line2 = line;

			exe.execute(new Runnable() {
				public void run() {
					JVMStarter starter;
					synchronized (pool) {
						if (pool.size() > 0) {
							starter = pool.remove(0);
						} else {
							starter = new JVMStarter();
						}
					}
					try {
						starter.startNewJVMSync(line2.split(argsSeparator));
					} catch (Exception e) {
						System.out.println("Failed to start: " + line2);
					}
					synchronized (pool) {
						pool.add(starter);
					}
				}
			});
		}
		exe.shutdown();
		try {
			exe.awaitTermination(timeout * 60, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length != 1) {
			System.out.println("Expected two argument: path-to-file separator-regex");
			System.exit(-1);
		} else {
			JVMBatchStarter starter = new JVMBatchStarter();
			starter.run(new FileInputStream(args[0]));
		}
	}

}
