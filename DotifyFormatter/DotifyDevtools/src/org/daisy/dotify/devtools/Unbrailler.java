package org.daisy.dotify.devtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.braille.table.BrailleConverter;
import org.daisy.braille.table.Table;
import org.daisy.braille.table.TableCatalog;

public class Unbrailler {
	private final XMLInputFactory inFactory;
	private final BrailleConverter bc;
	
	public Unbrailler(String tableId) {
        inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        
        Table t = TableCatalog.newInstance().get(tableId);
        this.bc = t.newBrailleConverter();
	}

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException {
		if (args.length<2) {
			System.out.println("Expected two arguments, path to input file and table identifier.");
			for (Table t : TableCatalog.newInstance().list()) {
				System.out.println(t.getIdentifier());
			}
			System.exit(-1);
		}
		File input = new File(args[0]);
		FilenameFilter ff = new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".pef");
			}};
		Unbrailler ub = new Unbrailler(args[1]);
		if (input.isDirectory()) {
			for (File f : input.listFiles(ff)) {
				try {
					ub.convert(f);
				} catch (Exception e) {//continue with next file
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Not a directory");
		}

	}
	
	private void convert(File input) throws IOException, XMLStreamException {
		File output = new File(input.getParent(), input.getName().substring(0, input.getName().length()-4)+".xml");

        XMLEventReader r = inFactory.createXMLEventReader(new FileInputStream(input));
		FileOutputStream os = new FileOutputStream(output);

		UnbrailleFilter f = new UnbrailleFilter(r, os, bc);
		f.filter();
		f.close();
	}

}
