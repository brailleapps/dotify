package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.osgi.framework.BundleContext;

public class Int2TextTracker extends MyTracker<Integer2TextFactoryMakerService> {

	public Int2TextTracker(BundleContext context) {
		super(context, Integer2TextFactoryMakerService.class.getName());
	}

}
