package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.FormatterConfiguration;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;

class FormatterCoreContext {
	private final TextBorderFactoryMakerService tbf;
	private final FormatterConfiguration config;

	FormatterCoreContext(TextBorderFactoryMakerService tbf, FormatterConfiguration config) {
		this.tbf = tbf;
		this.config = config;
	}

	String getTranslatorMode() {
		return config.getTranslationMode();
	}

	TextBorderFactoryMakerService getTextBorderFactoryMakerService() {
		return tbf;
	}
	
	FormatterConfiguration getConfiguration() {
		return config;
	}

}
