package org.daisy.dotify.writer.impl;

import java.util.Properties;

import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.api.writer.PagedMediaWriterFactory;
import org.daisy.dotify.writer.PEFMediaWriter;

class PEFMediaWriterFactory implements PagedMediaWriterFactory {
	private final Properties p;

	public PEFMediaWriterFactory() {
		super();
		p = new Properties();
	}

	public PagedMediaWriter newPagedMediaWriter(String mediaType)
			throws PagedMediaWriterConfigurationException {
		return new PEFMediaWriter(p);
	}

	public Object getFeature(String key) {
		return p.get(key);
	}

	public void setFeature(String key, Object value)
			throws PagedMediaWriterConfigurationException {
		p.put(key, value);
	}

}
