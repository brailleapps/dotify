package org.daisy.dotify.devtools.jvm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Provides an easy way to start separate JVM instances.
 * 
 * @author Joel Håkansson
 * 
 */
public class JVMStarter {
	private static int id = 0;
	private final PrintStream ps;
	private final String path;
	private final String idStr;

	public JVMStarter() {
		this(System.out);
	}

	public JVMStarter(OutputStream os) {
		this(new PrintStream(os));
	}

	public JVMStarter(PrintStream ps) {
		this.ps = ps;
		this.idStr = id + "";
		id++;
		String separator = System.getProperty("file.separator");
		path = System.getProperty("java.home") + separator + "bin" + separator + "java";
	}

	/**
	 * Starts a second JVM synchronously with the supplied args.
	 * 
	 * @param args
	 *            the args, e.g. -jar path/to/jar
	 * @throws Exception
	 */
	public void startNewJVMSync(String... args) throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(path);
		for (String arg : args) {
			command.add(arg);
		}

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		// hook up child process output to parent
		InputStream lsOut = process.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(lsOut));

		// read the child process' output
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line + "\n");
			// ps.println(line);
		}
		in.close();
		process.waitFor();
		ps.println("\nOutput for " + this.getClass().getCanonicalName() + "-" + idStr + "\n" + sb.toString());
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Starting a new JVM");
		JVMStarter jvm = new JVMStarter();
		jvm.startNewJVMSync(args);
		System.out.println("Returning from the new JVM...");
	}

}
