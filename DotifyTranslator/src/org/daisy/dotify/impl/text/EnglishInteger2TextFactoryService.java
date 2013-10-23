package org.daisy.dotify.impl.text;

import org.daisy.dotify.api.text.Integer2TextFactory;
import org.daisy.dotify.api.text.Integer2TextFactoryService;

import aQute.bnd.annotation.component.Component;

@Component
public class EnglishInteger2TextFactoryService implements
		Integer2TextFactoryService {

	public boolean supportsLocale(String locale) {
		return "en".equals(locale);
	}

	public Integer2TextFactory newFactory() {
		return new EnglishInteger2TextFactory();
	}


}
