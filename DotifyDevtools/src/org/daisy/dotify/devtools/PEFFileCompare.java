package org.daisy.dotify.devtools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.braille.tools.FileCompare;

/**
 * Provides comparing of two folders with pef files for differences.
 * Meta data in the files is ignored when comparing.
 * 
 * A flat organization of files is assumed.
 * 
 * Warnings are generated if stray files are found (a file with the same name
 * cannot be found in the other folder) or if the folders contain other folders
 * or files not ending with '.pef'.
 * 
 * @author Joel Håkansson
 */
public class PEFFileCompare {
	private final File dir1;
	private final File dir2;

	private List<String> warnings;
	private List<String> diffs;
	private List<String> oks;
	private int checked;
	
	/**
	 * 
	 * @param path1 a folder
	 * @param path2 another folder
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException if path is not a directory
	 */
	public PEFFileCompare(String path1, String path2) throws FileNotFoundException {
		dir1 = getExistingPath(path1);
		dir2 = getExistingPath(path2);
		if (!dir1.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory: " +path1);
		}
		if (!dir2.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory: " +path2);
		}
		warnings = new ArrayList<String>();
		diffs = new ArrayList<String>();
		oks = new ArrayList<String>();
		checked = 0;
	}
	
	public void run() {
		final HashMap<String, Integer> x = new HashMap<String, Integer>();
		final HashMap<String, File> files1 = new HashMap<String, File>();
		final HashMap<String, File> files2 = new HashMap<String, File>();
		PefFileFilter dir1Matches = new PefFileFilter();
		PefFileFilter dir2Matches = new PefFileFilter();
		for (File f : dir1.listFiles(dir1Matches)) {
			files1.put(f.getName(), f);
			x.put(f.getName(), 1);
		}
		for (File f : dir2.listFiles(dir2Matches)) {
			files2.put(f.getName(), f);
			Integer val = x.get(f.getName());
			if (val==null) {
				val = 2;
			} else {
				val = 0;
			}
			x.put(f.getName(), val);
		}
		
		for (File f : dir1Matches.getOtherFiles()) {
			warning(f + " will not be examined.");
		}
		
		for (File f : dir2Matches.getOtherFiles()) {
			warning(f + " will not be examined.");
		}

		checked += x.size();
		
		ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		int i2 = 1;
		for (final String key : x.keySet()) {
			final int i = i2;
			i2++;
			e.execute(new Runnable() {
		        public void run() {
		        	FileCompare fc = new FileCompare();

		    		//String originalTransformer = System.getProperty(TRANSFORMER_FACTORY_KEY);
		    		//System.setProperty(TRANSFORMER_FACTORY_KEY, "net.sf.saxon.TransformerFactoryImpl");
		        	TransformerFactory factory = TransformerFactory.newInstance();
		    		try {
		    			factory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
		    		} catch (IllegalArgumentException iae) { 
		    			iae.printStackTrace();
		    		}
					System.out.println("Comparing file " + key + " in " + dir1 + " and " + dir2 + " (" + i + "/" + x.size() + ")");
					
					int v = x.get(key);
					if (v!=0) {
						warning("Unmatched file '" + key + "' in " + (v==1?dir1:dir2));
					} else {
						File f1 = files1.get(key);
						File f2 = files2.get(key);
		
						try {
					        File t1 = File.createTempFile("FileCompare", ".tmp");
					        File t2 = File.createTempFile("FileCompare", ".tmp");
							try {
						        StreamSource xml1 = new StreamSource(f1);
						        StreamSource xml2 = new StreamSource(f2);
						        Source xslt;
						        Transformer transformer;
						        
						        xslt = new StreamSource(this.getClass().getResourceAsStream("resource-files/strip-meta.xsl"));
						        transformer = factory.newTransformer(xslt);
						        transformer.transform(xml1, new StreamResult(t1));
						        
						        xslt = new StreamSource(this.getClass().getResourceAsStream("resource-files/strip-meta.xsl"));
						        transformer = factory.newTransformer(xslt);
						        transformer.transform(xml2, new StreamResult(t2));
						        
								boolean ok = fc.compareXML(new FileInputStream(t1), new FileInputStream(t2));
								if (!ok) {
									diff(key + " " + fc.getPos());
								} else {
									ok(key);
								}
							} catch (FileNotFoundException e) {
								warning("An exception was thrown.");
								e.printStackTrace();
							} catch (IOException e) {
								warning("An exception was thrown.");
								e.printStackTrace();
							} catch (TransformerException e) {
								warning("An exception was thrown.");
								e.printStackTrace();
							} finally {
					        	if (!t1.delete()) {
					        		System.err.println("Delete failed");
					        		t1.deleteOnExit();
					        	}
					        	if (!t2.delete()) {
					        		System.err.println("Delete failed");
					        		t2.deleteOnExit();
					        	}
					        }
						} catch (IOException e) {
							warning("An exception was thrown.");
							e.printStackTrace();
						}
		
					}
		        }
		    });

		}
		e.shutdown();
		try {
			e.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
		
	private void warning(String msg) {
		warnings.add(msg);
	}
	
	private void diff(String filename) {
		diffs.add(filename);
	}
	
	private void ok(String filename) {
		oks.add(filename);
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
	public List<String> getDiffs() {
		return diffs;
	}
	
	public List<String> getOk() {
		return oks;
	}
	
	public int checkedCount() {
		return checked;
	}

	private static File getExistingPath(String path) throws FileNotFoundException {
		File ret = new File(path);
		if (!ret.exists()) {
			throw new FileNotFoundException("Path does not exist: " + path);
		}
		return ret;
	}
	
	private class PefFileFilter implements FileFilter {
		private ArrayList<File> noMatch;
		
		public PefFileFilter() {
			noMatch = new ArrayList<File>();
		}
		
		public List<File> getOtherFiles() {
			return noMatch;
		}

		public boolean accept(File pathname) {
			boolean isPef = pathname.getName().endsWith(".pef") && !pathname.isDirectory();
			if (!isPef) {
				noMatch.add(pathname);
			}
			return isPef;
		}
		
	}

}
