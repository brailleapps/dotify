package org.daisy.dotify.impl.text;

import org.daisy.dotify.api.text.Integer2TextFactory;
import org.daisy.dotify.api.text.Integer2TextFactoryService;

import aQute.bnd.annotation.component.Component;

@Component
public class SwedishInteger2TextFactoryService implements
		Integer2TextFactoryService {

	public boolean supportsLocale(String locale) {
		return "sv-SE".equalsIgnoreCase(locale);
	}

	public Integer2TextFactory newFactory() {
		return new SwedishInteger2TextFactory();
	}


}
