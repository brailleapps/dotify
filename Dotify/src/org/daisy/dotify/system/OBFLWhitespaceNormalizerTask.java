package org.daisy.dotify.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.obfl.OBFLWsNormalizer;

public class OBFLWhitespaceNormalizerTask extends ReadWriteTask {

	public OBFLWhitespaceNormalizerTask(String name) {
		super(name);
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			XMLInputFactory inFactory = XMLInputFactory.newInstance();
			inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
			inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
			OBFLWsNormalizer normalizer = new OBFLWsNormalizer(inFactory.createXMLEventReader(new FileInputStream(input)), XMLEventFactory.newInstance(), new FileOutputStream(output));
			normalizer.parse(XMLOutputFactory.newInstance());
		} catch (FileNotFoundException e) {
			throw new InternalTaskException(e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException(e);
		}


	}

}
