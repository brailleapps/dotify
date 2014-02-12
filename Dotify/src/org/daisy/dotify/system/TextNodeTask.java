package org.daisy.dotify.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.text.StringFilter;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

import se.mtm.common.io.InputStreamMaker;

/**
 * <p>Task that runs a list of StringFilters on the character data of the input file.</p>
 * <p>Input file type requirement: XML</p>
 * 
 * @author  Joel Håkansson
 * @version 4 maj 2009
 * @since 1.0
 */
public class TextNodeTask extends ReadWriteTask {
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
	public void execute(InputStreamMaker input, OutputStream output) throws InternalTaskException {
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

		TextNodeFilter tnf = null;

		try {
			tnf = new TextNodeFilter(inFactory.createXMLEventReader(input.newInputStream()), output, filters);
			tnf.filter();
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException:", e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException("XMLStreamException:", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException:", e);
		} finally {
			if (tnf!=null) {
				try { tnf.close(); } catch (IOException e) { }
			}
		}

	}

}
