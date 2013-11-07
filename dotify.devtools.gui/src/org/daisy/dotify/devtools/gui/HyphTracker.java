package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.osgi.framework.BundleContext;

public class HyphTracker extends MyTracker<HyphenatorFactoryMakerService> {

	public HyphTracker(BundleContext context) {
		super(context, HyphenatorFactoryMakerService.class.getName());
	}

}
