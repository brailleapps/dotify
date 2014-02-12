package org.daisy.dotify.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.obfl.OBFLWsNormalizer;

import se.mtm.common.io.InputStreamMaker;

/**
 * @deprecated Not needed anymore. It is the formatter's responsibility.
 *
 */
public class OBFLWhitespaceNormalizerTask extends ReadWriteTask {

	public OBFLWhitespaceNormalizerTask(String name) {
		super(name);
	}

	@Override
	public void execute(InputStreamMaker input, OutputStream output) throws InternalTaskException {
		try {
			XMLInputFactory inFactory = XMLInputFactory.newInstance();
			inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
			inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
			OBFLWsNormalizer normalizer = new OBFLWsNormalizer(inFactory.createXMLEventReader(input.newInputStream()), XMLEventFactory.newInstance(), output);
			normalizer.parse(XMLOutputFactory.newInstance());
		} catch (FileNotFoundException e) {
			throw new InternalTaskException(e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException(e);
		} catch (IOException e) {
			throw new InternalTaskException(e);
		} catch (FactoryConfigurationError e) {
			throw new InternalTaskException(e);
		}


	}

}
