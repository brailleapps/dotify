package org.daisy.dotify.engine.impl;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
public class VerticalPositionTest extends AbstractFormatterEngineTest {

	@Test
	public void testVerticalPosition() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-input.obfl", "resource-files/vertical-position-expected.pef", false);
	}
	
	@Test
	public void testVerticalPositionDLS() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-dls-input.obfl", "resource-files/vertical-position-dls-expected.pef", false);
	}


}
