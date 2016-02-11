package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.daisy.braille.api.table.TableCatalogService;
import org.daisy.dotify.devtools.regression.DotifyRegressionTesterRunner;

public class DotifyRegressionTesterUI {

	/**
	 * First argument should point to a file containing paths to input xml
	 * and reference output. E.g. DTB00001/DTB00001.xml baseline/P1.pef
	 * Second argument should point to the CLI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 6) {
			System.out.println("Not enough arguments. Expected at least six arguments.");
			System.exit(-1);
		}
		try {
			TableCatalogService tcs = invokeStatic("org.daisy.braille.consumer.table.TableCatalog", "newInstance");
			DotifyRegressionTesterRunner rt = new DotifyRegressionTesterRunner(new File(args[0]), args[1], new File(args[2]), args[3], args[4], tcs.newTable(args[5]).newBrailleConverter());
			if (args.length >= 7) {
				int thArg = 6;
				try {
					rt.setThreads(Integer.parseInt(args[thArg]));
				} catch (NumberFormatException e) {
					System.out.println(args[thArg] + " is not an integer.");
				}
			}
			if (args.length >= 8) {
				int errArg = 7;
				//This is a bit unusual, but the default value should be true and if the input is misspelled, it should use the default
				rt.setHaltOnError(!"false".equalsIgnoreCase(args[errArg]));
			}
			rt.run();
		} catch (IOException e) {
			System.exit(-1);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T invokeStatic(String clazz, String method) {
		T instance = null;
		try {
			Class<?> cls = Class.forName(clazz);
			Method m = cls.getMethod(method);
			instance = (T)m.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
}
