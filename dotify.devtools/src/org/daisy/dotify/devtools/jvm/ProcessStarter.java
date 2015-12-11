package org.daisy.dotify.devtools.jvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
public class ProcessStarter {
	private static int id = 0;
	private final PrintStream ps;
	private final static String path;
	private final String idStr;
	
	static {
		String separator = System.getProperty("file.separator");
		path = System.getProperty("java.home") + separator + "bin" + separator + "java";
	}

	public ProcessStarter() {
		this(System.out);
	}

	public ProcessStarter(OutputStream os) {
		this(new PrintStream(os));
	}

	public ProcessStarter(PrintStream ps) {
		this.ps = ps;
		this.idStr = id + "";
		id++;
	}
	
	public static String getJavaPath() {
		return path;
	}
	
	/**
	 * Returns the supplied arguments with the path to java added as the first argument
	 * @param args the arguments
	 * @return returns the command
	 */
	public static String[] buildJavaCommand(String... args) {
		ArrayList<String> command = new ArrayList<>();
		command.add(path);
		for (String arg : args) {
			command.add(arg);
		}
		return command.toArray(new String[]{});
	}

	/**
	 * Starts a process synchronously with the supplied command.
	 * 
	 * @param command
	 *            the command, e.g. java -jar path/to/jar
	 * @return returns the return value of the process
	 * @throws Exception
	 */
	public int startProcess(String ... command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);
		File cmd = new File(command[0]);
		if (cmd.isFile()) {
			//start in...
			processBuilder.directory(cmd.getParentFile());
		}
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
		int ret = process.waitFor();
		ps.println("\nOutput for " + this.getClass().getCanonicalName() + "-" + idStr + "\n" + sb.toString());
		return ret;
	}

}
