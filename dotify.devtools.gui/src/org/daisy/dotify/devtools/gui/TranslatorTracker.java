package org.daisy.dotify.devtools.gui;

import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.osgi.framework.BundleContext;

public class TranslatorTracker extends MyTracker<BrailleTranslatorFactoryMakerService> {

	public TranslatorTracker(BundleContext context) {
		super(context, BrailleTranslatorFactoryMakerService.class.getName());
	}	

}
