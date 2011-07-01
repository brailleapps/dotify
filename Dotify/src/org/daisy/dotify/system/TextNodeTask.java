package org.daisy.dotify.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.text.StringFilter;
import org.daisy.dotify.text.TextNodeFilter;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * <p>Task that runs a list of StringFilters on the character data of the input file.</p>
 * <p>Input file type requirement: XML</p>
 * 
 * @author  Joel Håkansson, TPB
 * @version 4 maj 2009
 * @since 1.0
 */
public class TextNodeTask extends InternalTask {
	private StringFilter filters;

	/**
	 * Create a new TextNodeTask.
	 * @param name task name
	 * @param filters ArrayList of StringFilters
	 */
	public TextNodeTask(String name, StringFilter filters) {
		super(name);
		this.filters = filters;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        
    	try {
			inFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		} catch (CatalogExceptionNotRecoverable e1) {
			e1.printStackTrace();
		}

		TextNodeFilter tnf;

		try {
			tnf = new TextNodeFilter(inFactory.createXMLEventReader(new FileInputStream(input)), new FileOutputStream(output), filters);
			tnf.filter();
			tnf.close();
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException:", e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException("XMLStreamException:", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException:", e);
		}

	}

}
