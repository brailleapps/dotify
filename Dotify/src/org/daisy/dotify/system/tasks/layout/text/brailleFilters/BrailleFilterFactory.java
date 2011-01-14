package org.daisy.dotify.system.tasks.layout.text.brailleFilters;

import java.net.URL;
import java.util.HashMap;

import org.daisy.dotify.system.tasks.layout.text.CaseFilter;
import org.daisy.dotify.system.tasks.layout.text.CharFilter;
import org.daisy.dotify.system.tasks.layout.text.CombinationFilter;
import org.daisy.dotify.system.tasks.layout.text.FilterFactory;
import org.daisy.dotify.system.tasks.layout.text.FilterLocale;
import org.daisy.dotify.system.tasks.layout.text.RegexFilter;
import org.daisy.dotify.system.tasks.layout.text.StringFilter;
import org.daisy.dotify.system.tasks.layout.text.brailleFilters.sv_SE.CapitalizationMarkers;


/**
 * Provides a factory for braille StringFilters. It can return different StringFilters
 * depending on the requested locale. Allow access to all locale rules and optionally output braille using that locale.
 * @author Joel Håkansson, TPB
 */
public class BrailleFilterFactory implements FilterFactory {
	private static HashMap<String, FilterLocale> locales = null;
	private StringFilter def;

	protected BrailleFilterFactory() {
		if (locales == null) {
			initTable();
		}
		def = new CombinationFilter();
	}
	
	/**
	 * Gets a new instance of BrailleFilterFactory
	 * @return returns a new instance of BrailleFilterFactory
	 */
	public static BrailleFilterFactory newInstance() {
		return new BrailleFilterFactory();
	}
	
	private void initTable() {
		locales = new HashMap<String, FilterLocale>();
		putLocale("sv");
		putLocale("sv-SE");
	}
	
	private void putLocale(String str) {
		FilterLocale loc = FilterLocale.parse(str);
		locales.put(loc.toString(), loc);
	}
	
	/**
	 * Gets the default StringFilter for this factory
	 */
	public StringFilter getDefault() {
		return def;
	}

	/**
	 * Attempts to retrieve a StringFilter for the given locale. If none is found
	 * the default StringFilter is returned.
	 * @param target target locale
	 * @return returns a StringFilter for the given locale, or the default StringFilter if no match is found
	 */
	public StringFilter newStringFilter(FilterLocale target) {
		if (target.isA(locales.get("sv"))) {
			CombinationFilter filters = new CombinationFilter();
			// Remove zero width space
			filters.add(new RegexFilter("\\u200B", ""));
			// One or more digit followed by zero or more digits, commas or periods
			filters.add(new RegexFilter("([\\d]+[\\d,\\.]*)", "\u283c$1"));
			// Insert a "reset character" between a digit and lower case a-j
			filters.add(new RegexFilter("([\\d])([a-j])", "$1\u2831$2"));
			// Add upper case marker to the beginning of any upper case sequence
			//filters.add(new RegexFilter("(\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
			// Add another upper case marker if the upper case sequence contains more than one character
			//filters.add(new RegexFilter("(\\u2820\\p{Lu}\\u00ad*\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
			filters.add(new CapitalizationMarkers());
			// Change case to lower case
			filters.add(new CaseFilter(CaseFilter.Mode.LOWER_CASE));
			if (target.isA(locales.get("sv-SE"))) {
				// Text to braille, Pas 1
				filters.add(new CharFilter(getResource("sv_SE/sv_SE-pas1.xml")));
				// Text to braille, Pas 2
				filters.add(new CharFilter(getResource("sv_SE/sv_SE-pas2.xml")));
			}
			// Remove redundant whitespace
			filters.add(new RegexFilter("(\\s+)", " "));
			return filters;
		}
		// use default
		return def;
	}
	
	/**
	 * Sets the default StringFilter for this factory by retrieving a StringFilter for the supplied FilterLocale.
	 * Identical to setDefault(newStringFilter(locale));
	 * @param locale the FilterLocale to use
	 */
	public void setDefault(FilterLocale locale) {
		def = newStringFilter(locale);
	}
	
	/**
	 * Sets the default StringFilter for this factory.
	 */
	public void setDefault(StringFilter filter) {
		def = filter;
	}

	/**
	 * Retrieve a URL of a resource associated with this transformer.
	 * <p>This method is preferred to {@link #getTransformerDirectory()} since
	 * it supports jarness.</p>
	 */
	final protected URL getResource(String subPath) throws IllegalArgumentException {
		//TODO check the viability of this method
		URL url;
	    url = this.getClass().getResource(subPath);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+subPath);
	    }
	    if(url==null) throw new IllegalArgumentException(subPath);
	    return url;
	}

}
