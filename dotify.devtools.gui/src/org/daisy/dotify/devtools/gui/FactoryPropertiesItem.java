package org.daisy.dotify.devtools.gui;

import org.daisy.braille.api.factory.FactoryProperties;

class FactoryPropertiesItem {
	private final FactoryProperties factoryProperties;
	
	FactoryPropertiesItem(FactoryProperties factoryProperties) {
		this.factoryProperties = factoryProperties;
	}

	FactoryProperties getFactoryProperties() {
		return factoryProperties;
	}

	@Override
	public String toString() {
		
		return factoryProperties.getDisplayName() + (
				!factoryProperties.getDescription().equals("")?
				" - " + factoryProperties.getDescription():"");
	}
}