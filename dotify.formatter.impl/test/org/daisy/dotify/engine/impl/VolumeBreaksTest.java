package org.daisy.dotify.engine.impl;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
public class VolumeBreaksTest extends AbstractFormatterEngineTest {

	@Test
	public void testUnevenVolumeBreak() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/volume-breaks-uneven-input.obfl", "resource-files/volume-breaks-uneven-expected.pef", false);
	}
	

}
