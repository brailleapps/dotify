package org.daisy.dotify.devtools.gui;

import org.daisy.braille.api.embosser.EmbosserCatalogService;
import org.osgi.framework.BundleContext;

public class EmbosserCatalogTracker extends MyTracker<EmbosserCatalogService> {

	public EmbosserCatalogTracker(BundleContext context) {
		super(context, EmbosserCatalogService.class.getName());
	}

}
