package org.daisy.dotify.devtools.gui;

import org.daisy.paper.PaperCatalogService;
import org.osgi.framework.BundleContext;

public class PaperCatalogTracker extends MyTracker<PaperCatalogService> {

	public PaperCatalogTracker(BundleContext context) {
		super(context, PaperCatalogService.class.getName());
	}

}
