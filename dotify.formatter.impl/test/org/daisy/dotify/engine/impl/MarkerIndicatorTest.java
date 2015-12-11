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
	public void testFormat_55() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/test_format.xprocspec/test_55-input.obfl",
		        "resource-files/test_format.xprocspec/test_55-expected.pef", true);
	}
	@Test
	public void testFormat_56() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/test_format.xprocspec/test_56-input.obfl",
		        "resource-files/test_format.xprocspec/test_56-expected.pef", true);
	}
	@Test
	public void testFormat_57() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/test_format.xprocspec/test_57-input.obfl",
		        "resource-files/test_format.xprocspec/test_57-expected.pef", true);
	}
}
