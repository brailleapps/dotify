package org.daisy.dotify.devtools.cli;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.daisy.braille.pef.PEFBook;
import org.xml.sax.SAXException;

public class PEFStatsCollector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length<2) {
			System.out.println("Expected two arguments: folder path and result file");
			System.exit(-1);
		}
		File input = new File(args[0]);
		if (!input.isDirectory()) {
			System.out.println("Input must be an existing directory.");
			System.exit(-2);
		}
		PrintStream p = null;
		try {
			p = new PrintStream(new File(args[1]));
		} catch (FileNotFoundException e1) {
			System.out.println("Cannot write to result file.");
			System.exit(-3);
		} 
		final PrintStream ps = p;
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (final File f : input.listFiles(new PEFFileFilter())) {
				es.execute(new Runnable(){

					public void run() {
						try {
							System.out.println("Reading file " + f);
							PEFBook p = PEFBook.load(f.toURI());
							ps.println(f.getName() + "\t" + p.getVolumes()+ "\t" + p.getSheets());
						} catch (XPathExpressionException e) {
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}});
		}
		es.shutdown();
		try {
			es.awaitTermination(10 * 60, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			ps.close();
		}
	}
	
	private static class PEFFileFilter implements FileFilter {


		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".pef") && !pathname.isDirectory();
		}
	}

}
