package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.engine.FormatterEngineFactoryMakerService;
import org.osgi.framework.BundleContext;

public class FormatterTracker extends MyTracker<FormatterEngineFactoryMakerService> {

	public FormatterTracker(BundleContext context) {
		super(context, FormatterEngineFactoryMakerService.class.getName());
	}

}
