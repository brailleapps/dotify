package org.daisy.dotify.engine.impl;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
public class RowSpacingTest extends AbstractFormatterEngineTest {
	
	@Test
	public void testRowSpacing_01() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/row-spacing-input.obfl", "resource-files/row-spacing-expected.pef", false);
	}

}
