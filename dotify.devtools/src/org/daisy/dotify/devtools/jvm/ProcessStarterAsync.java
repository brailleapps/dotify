package org.daisy.dotify.devtools.jvm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ProcessStarterAsync {
	private final List<ProcessStarter> pool;
	private final ExecutorService exe;

	/**
	 * Creates a new asynchronous process starter with the same number of threads as there are
	 * processors available.
	 */
	public ProcessStarterAsync() {
		this(0);
	}
	
	/**
	 * Creates a new asynchronous process starter with the specified number of threads.
	 * 
	 * @param threads the number of threads to use
	 */
	public ProcessStarterAsync(int threads) {
		int t = Runtime.getRuntime().availableProcessors();
		if (threads > 0 && threads < t) {
			t = threads;
		}
		pool = Collections.synchronizedList(new ArrayList<ProcessStarter>());
		exe = Executors.newFixedThreadPool(t);
		Logger.getLogger(this.getClass().getCanonicalName()).info("Using " + t + " threads.");
	}
	
	/**
	 * Adds a new process to be executed as a thread becomes available.
	 * @param command the command to execute
	 */
	public void addProcess(final String ... command) {
		exe.execute(new Runnable() {
			@Override
			public void run() {
				ProcessStarter starter;
				synchronized (pool) {
					if (pool.size() > 0) {
						starter = pool.remove(0);
					} else {
						starter = new ProcessStarter();
					}
				}
				try {
					int ret = starter.startProcess(command);
					if (ret!=0) {
						Logger.getLogger(this.getClass().getCanonicalName()).warning("Non zero exit value (" + ret +") returned by: " + command);
					}
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getCanonicalName()).warning("Failed to execute: " + command);
				}
				synchronized (pool) {
					pool.add(starter);
				}
			}
		});
	}

	/**
	 * Blocks until all processes have terminated.
	 * @param timeout
	 * @param unit
	 * @throws IOException
	 */
	public void shutdown(long timeout, TimeUnit unit) throws IOException {
		exe.shutdown();
		try {
			exe.awaitTermination(timeout, unit);
		} catch (InterruptedException e1) {
			throw new IOException(e1);
		}
	}

}
