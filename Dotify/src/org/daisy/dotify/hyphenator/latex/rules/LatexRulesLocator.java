package org.daisy.dotify.hyphenator.latex.rules;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

public class LatexRulesLocator extends AbstractResourceLocator {

	public URL getCatalogResourceURL() throws ResourceLocatorException {
		return getResource("hyphenation_tables.xml");
	}
}
