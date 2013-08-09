package org.daisy.dotify.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.XMLStreamException;

import org.daisy.dotify.obfl.OBFLWsNormalizer;

public class OBFLWhitespaceNormalizerTask extends ReadWriteTask {

	public OBFLWhitespaceNormalizerTask(String name) {
		super(name);
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			OBFLWsNormalizer normalizer = new OBFLWsNormalizer(new FileInputStream(input), new FileOutputStream(output));
			normalizer.parse();
		} catch (FileNotFoundException e) {
			throw new InternalTaskException(e);
		} catch (XMLStreamException e) {
			throw new InternalTaskException(e);
		}


	}

}
