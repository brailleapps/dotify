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
	@Test
	public void testEmptyMarginRegion() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/dp2/empty-margin-region-input.obfl",
		        "resource-files/dp2/empty-margin-region-expected.pef", false);
	}
	@Test
	public void testMarkerIndicator() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/dp2/marker-indicator-input.obfl",
		        "resource-files/dp2/marker-indicator-expected.pef", false);
	}
}
