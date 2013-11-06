package org.daisy.dotify.engine;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.writer.PagedMediaWriter;

public interface FormatterEngineFactoryMakerService {

	public FormatterEngine newFormatterEngine(FilterLocale locale, String mode, PagedMediaWriter writer);

}
