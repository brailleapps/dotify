package org.daisy.dotify.engine.impl;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
public class MarginTest extends AbstractFormatterEngineTest {
	
	@Test
	public void testCollapsingMargin_01() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/margin-input.obfl", "resource-files/margin-expected.pef", false);
	}

	@Test
	public void testCollapsingMargin_02() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/margin-nested-input.obfl", "resource-files/margin-nested-expected.pef", false);
	}
}
