package org.daisy.dotify.devtools.jvm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JVMStarterAsync {
	private final List<JVMStarter> pool;
	private final ExecutorService exe;

	public JVMStarterAsync() {
		this(0);
	}
	
	public JVMStarterAsync(int threads) {
		int t = Runtime.getRuntime().availableProcessors();
		if (threads > 0 && threads < t) {
			t = threads;
		}
		pool = Collections.synchronizedList(new ArrayList<JVMStarter>());
		exe = Executors.newFixedThreadPool(threads);
		Logger.getLogger(this.getClass().getCanonicalName()).info("Using " + t + " threads.");
	}
	
	public void addJob(final String ... args) {
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
					starter.startNewJVMSync(args);
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getCanonicalName()).warning("Failed to execute: " + args);
				}
				synchronized (pool) {
					pool.add(starter);
				}
			}
		});
	}

	public void shutdown(long timeout, TimeUnit unit) throws IOException {
		exe.shutdown();
		try {
			exe.awaitTermination(timeout, unit);
		} catch (InterruptedException e1) {
			throw new IOException(e1);
		}
	}
	
	
}
