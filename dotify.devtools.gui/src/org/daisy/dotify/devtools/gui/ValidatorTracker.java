package org.daisy.dotify.devtools.gui;

import org.daisy.braille.api.validator.ValidatorFactoryService;
import org.osgi.framework.BundleContext;

public class ValidatorTracker extends MyTracker<ValidatorFactoryService> {

	public ValidatorTracker(BundleContext context) {
		super(context, ValidatorFactoryService.class.getName());
	}

}
