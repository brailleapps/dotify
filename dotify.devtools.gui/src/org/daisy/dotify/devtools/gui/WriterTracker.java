package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.writer.PagedMediaWriterFactoryMakerService;
import org.osgi.framework.BundleContext;

public class WriterTracker extends MyTracker<PagedMediaWriterFactoryMakerService> {

	public WriterTracker(BundleContext context) {
		super(context, PagedMediaWriterFactoryMakerService.class.getName());
	}

}
