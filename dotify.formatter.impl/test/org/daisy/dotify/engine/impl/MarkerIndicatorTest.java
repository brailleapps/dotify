package org.daisy.dotify.engine.impl;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
public class MarkerIndicatorTest extends AbstractFormatterEngineTest {
	
	@Test
	public void testPageMargin_01() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/marker-indicator-input.obfl", "resource-files/marker-indicator-expected.pef", false);
	}

}
