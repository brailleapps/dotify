package org.daisy.dotify.consumer.writer;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.writer.MediaTypes;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;
import org.junit.Test;

public class PagedMediaWriterFactoryMakerTest {

	@Test
	public void testFactoryExists() {
		//Setup
		PagedMediaWriterFactoryMakerService factory = PagedMediaWriterFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testImplementationForPEFExists() throws PagedMediaWriterConfigurationException {
		//Setup
		PagedMediaWriterFactoryMakerService factory = PagedMediaWriterFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory.newPagedMediaWriter(MediaTypes.PEF_MEDIA_TYPE) != null);
	}
	
	@Test
	public void testImplementationForTextExists() throws PagedMediaWriterConfigurationException {
		//Setup
		PagedMediaWriterFactoryMakerService factory = PagedMediaWriterFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory.newPagedMediaWriter(MediaTypes.TEXT_MEDIA_TYPE) != null);
	}
	
}
