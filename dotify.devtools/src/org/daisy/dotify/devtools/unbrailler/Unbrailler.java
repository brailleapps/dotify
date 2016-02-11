package org.daisy.dotify.devtools.unbrailler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.braille.api.factory.FactoryProperties;
import org.daisy.braille.api.table.BrailleConverter;
import org.daisy.braille.api.table.TableCatalogService;

public class Unbrailler {
	private final XMLInputFactory inFactory;
	private final BrailleConverter bc;
	
	public Unbrailler(BrailleConverter bc) {
        inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        this.bc = bc;
	}

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException {
		TableCatalogService tcs = invokeStatic("org.daisy.braille.consumer.table.TableCatalog", "newInstance");
		if (args.length<2) {
			System.out.println("Expected two arguments, path to input file and table identifier.");
			for (FactoryProperties t : tcs.list()) {
				System.out.println(t.getIdentifier());
			}
			System.exit(-1);
		}
		File input = new File(args[0]);

		
		Unbrailler ub = new Unbrailler(tcs.newTable(args[1]).newBrailleConverter());

		ub.run(input);
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

	public void run(File inputFolder) {
		if (!inputFolder.isDirectory()) {
			throw new IllegalArgumentException("Input is not a directory: " + inputFolder);
		}

		File outputFolder = new File(inputFolder, "unbrailler");
		outputFolder.mkdirs();
		FilenameFilter ff = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".pef");
			}};
		for (File input : inputFolder.listFiles(ff)) {
			try {
				convert(input, outputFolder);
			} catch (Exception e) {// continue with next file
				e.printStackTrace();
			}
		}
	}

	public boolean convert(File input, File output) {
		try {
			XMLEventReader r = inFactory.createXMLEventReader(new FileInputStream(input));
			FileOutputStream os = new FileOutputStream(output);

			UnbrailleFilter f = new UnbrailleFilter(r, os, bc);
			f.setFiltering(true);
			f.filter();
			f.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return false;
		}
	}

}
