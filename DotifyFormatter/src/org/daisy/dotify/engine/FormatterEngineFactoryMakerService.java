package org.daisy.dotify.engine;

import org.daisy.dotify.writer.PagedMediaWriter;

public interface FormatterEngineFactoryMakerService {

	public FormatterEngine newFormatterEngine(String locale, String mode, PagedMediaWriter writer);

}
