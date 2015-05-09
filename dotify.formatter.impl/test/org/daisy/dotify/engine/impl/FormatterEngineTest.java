package org.daisy.dotify.engine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;

import org.daisy.braille.pef.PEFFileCompare;
import org.daisy.braille.pef.PEFFileCompareException;
import org.daisy.dotify.api.engine.FormatterEngine;
import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.writer.MediaTypes;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.consumer.engine.FormatterEngineMaker;
import org.daisy.dotify.consumer.writer.PagedMediaWriterFactoryMaker;
import org.junit.Test;
public class FormatterEngineTest {

	@Test
	public void testFormatterEngingeKeep() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/obfl-input-keep.obfl", "resource-files/obfl-keep-expected.pef");
	}

	private void testPEF(String input, String expected) throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		FormatterEngine engine = FormatterEngineMaker.newInstance().newFormatterEngine("sv-SE",
				BrailleTranslatorFactory.MODE_UNCONTRACTED, 
				PagedMediaWriterFactoryMaker.newInstance().newPagedMediaWriter(MediaTypes.PEF_MEDIA_TYPE));

		File res = File.createTempFile("TestResult", ".tmp");
		System.err.println(res);
		res.deleteOnExit();

		engine.convert(this.getClass().getResourceAsStream(input), new FileOutputStream(res));

		try {
			PEFFileCompare cmp = new PEFFileCompare();
			cmp.compare(new StreamSource(this.getClass().getResourceAsStream(expected)), new StreamSource(new FileInputStream(res)));
			assertEquals("Binary compare is equal", -1, cmp.getPos());
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (PEFFileCompareException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (!res.delete()) {
				System.err.println("Delete failed.");
			}
			if (res.isFile()) {
				System.out.println(res.getAbsolutePath());
			}
		}
	}
}
