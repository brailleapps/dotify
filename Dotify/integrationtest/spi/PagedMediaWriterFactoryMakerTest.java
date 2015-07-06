package spi;

import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;
import org.daisy.dotify.consumer.writer.PagedMediaWriterFactoryMaker;

import base.PagedMediaWriterFactoryMakerTestbase;

public class PagedMediaWriterFactoryMakerTest extends PagedMediaWriterFactoryMakerTestbase {

	@Override
	public PagedMediaWriterFactoryMakerService getPageMedaWriterFMS() {
		return PagedMediaWriterFactoryMaker.newInstance();
	}
	
}
