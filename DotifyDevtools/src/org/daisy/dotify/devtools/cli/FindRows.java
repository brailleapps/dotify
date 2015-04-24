package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.daisy.dotify.common.text.TextFileReader;
import org.daisy.dotify.common.text.TextFileReader.LineData;

public class FindRows {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length<3) {
			System.out.println("Expected at least two arguments: input_path encoding lowerLimit");
			System.exit(-1);
		}
		File input = new File(args[0]);
		int limit = Integer.parseInt(args[2]);
		if (limit<1) {
			System.out.println("Lower limit must be at least 1.");
		}
		if (!input.exists()) {
			System.out.println("File does not exist");
			System.exit(-2);
		}
		TextFileReader tfr = new TextFileReader(new FileInputStream(input), Charset.forName(args[1]));
		LineData line;
		System.out.println("Scanning...");
		while ((line=tfr.nextLine())!=null) {
			if (line.getLine().length()>limit) {
				System.out.println("Line " + line.getLineNumber() + " '" + line.getLine() + "' is " + line.getLine().length() + " characters long.");
			}
		}
		tfr.close();
		System.out.println("Done!");
	}

}
