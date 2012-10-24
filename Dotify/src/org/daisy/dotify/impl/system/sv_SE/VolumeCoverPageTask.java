package org.daisy.dotify.impl.system.sv_SE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.ReadWriteTask;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>Add a Volume Cover to each volume.</p>
 * <p>Input file type requirement: PEF</p>
 * @author Joel Håkansson, TPB
 * @deprecated replaced by the volume-template feature in OBFL
 */
class VolumeCoverPageTask extends ReadWriteTask {
	private final VolumeCoverPage frontCover;
	private final VolumeCoverPage rearCover;

	public VolumeCoverPageTask(String name, VolumeCoverPage cover) {
		this(name, cover, null);
	}
	
	public VolumeCoverPageTask(String name, VolumeCoverPage frontCover, VolumeCoverPage rearCover) {
		super(name);
		this.frontCover = frontCover;
		this.rearCover = rearCover;
	}

	@Override
	public void execute(File input, File output)
			throws InternalTaskException {

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
		
		try {
			Document d2 = initDocumentBuilder().parse(input);
			XPath xp = XPathFactory.newInstance().newXPath();
			int volumeCount = ((Double)xp.evaluate("count(//volume)", d2, XPathConstants.NUMBER)).intValue();
			VolumeCoverPageFilter pf = new VolumeCoverPageFilter(
					inFactory.createXMLEventReader(new FileInputStream(input)), 
					new FileOutputStream(output),
					frontCover, rearCover, volumeCount);
			pf.filter();
			pf.close();
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException:", e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException("XMLStreamException:", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException:", e);
		} catch (SAXException e) {
			throw new InternalTaskException("SAXException:", e);
		} catch (ParserConfigurationException e) {
			throw new InternalTaskException("ParserConfigurationException:", e);
		} catch (XPathExpressionException e) {
			throw new InternalTaskException("XPathExpressionException:", e);
		}
	}
	
	protected DocumentBuilder initDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		try {
			db.setEntityResolver(CatalogEntityResolver.getInstance());
		} catch (CatalogExceptionNotRecoverable e) {
			ParserConfigurationException pce = new ParserConfigurationException("Unable to set CatalogEntityResolver");
			pce.initCause(e);
			throw pce;
		}
		return db;
	}

}
