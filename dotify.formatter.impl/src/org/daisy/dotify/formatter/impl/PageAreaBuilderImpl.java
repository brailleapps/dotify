package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.PageAreaBuilder;

class PageAreaBuilderImpl implements PageAreaBuilder {
	private final PageAreaProperties properties;
	private final FormatterCore beforeArea;
	private final FormatterCore afterArea;

	PageAreaBuilderImpl(PageAreaProperties properties) {
		this.properties = properties;
		this.beforeArea = new FormatterCoreImpl();
		this.afterArea = new FormatterCoreImpl();
	}
	
	PageAreaProperties getProperties() {
		return properties;
	}

	public FormatterCore getBeforeArea() {
		return beforeArea;
	}

	public FormatterCore getAfterArea() {
		return afterArea;
	}

}
