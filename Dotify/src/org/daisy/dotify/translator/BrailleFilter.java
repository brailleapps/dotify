package org.daisy.dotify.translator;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.StringFilter;

public interface BrailleFilter extends StringFilter {
	
	public void setLocale(FilterLocale locale);
	public boolean supportsLocale(FilterLocale locale);

}
