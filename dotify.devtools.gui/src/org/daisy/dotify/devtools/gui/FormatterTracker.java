package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.engine.FormatterEngineFactoryService;
import org.osgi.framework.BundleContext;

public class FormatterTracker extends MyTracker<FormatterEngineFactoryService> {

	public FormatterTracker(BundleContext context) {
		super(context, FormatterEngineFactoryService.class.getName());
	}

}
