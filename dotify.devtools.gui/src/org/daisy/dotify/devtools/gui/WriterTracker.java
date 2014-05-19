package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.writer.PagedMediaWriterFactoryService;
import org.osgi.framework.BundleContext;

public class WriterTracker extends MyTracker<PagedMediaWriterFactoryService> {

	public WriterTracker(BundleContext context) {
		super(context, PagedMediaWriterFactoryService.class.getName());
	}

}
