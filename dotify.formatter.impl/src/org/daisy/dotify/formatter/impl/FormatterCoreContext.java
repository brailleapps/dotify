package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;

class FormatterCoreContext {
	private final TextBorderFactoryMakerService tbf;
	private final String mode;

	FormatterCoreContext(TextBorderFactoryMakerService tbf, String mode) {
		this.tbf = tbf;
		this.mode = mode;
	}

	String getTranslatorMode() {
		return mode;
	}

	TextBorderFactoryMakerService getTextBorderFactoryMakerService() {
		return tbf;
	}

}
