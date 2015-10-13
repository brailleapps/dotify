package org.daisy.dotify.engine.impl;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
public class FieldTest extends AbstractFormatterEngineTest {

	@Test
	public void testFieldStyle() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/field-style-input.obfl", "resource-files/field-style-expected.pef", true);
	}

}
