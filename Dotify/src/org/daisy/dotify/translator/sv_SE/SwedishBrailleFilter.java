package org.daisy.dotify.translator.sv_SE;

import java.net.URL;
import java.util.HashMap;

import org.daisy.dotify.text.CaseFilter;
import org.daisy.dotify.text.CharFilter;
import org.daisy.dotify.text.CombinationFilter;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.RegexFilter;
import org.daisy.dotify.translator.BrailleFilter;

public class SwedishBrailleFilter implements BrailleFilter {
	private final static String sv_SE = "sv-SE";
	private final static HashMap<String, FilterLocale> locales;
	static {
		locales = new HashMap<String, FilterLocale>();
		//putLocale("sv");
		putLocale(sv_SE);
	}
	
	private static void putLocale(String str) {
		FilterLocale loc = FilterLocale.parse(str);
		locales.put(loc.toString(), loc);
	}

	private CombinationFilter filters;
	
	public SwedishBrailleFilter() { 
		filters = null;
		//setLocale(locales.get(sv_SE));
	}

	public String filter(String str) {
		return filters.filter(str);
	}

	public boolean supportsLocale(FilterLocale target) {
		for (FilterLocale loc : locales.values()) {
			if (target.isA(loc)) {
				return true;
			}
		}
		return false;
	}

	public void setLocale(FilterLocale target) {
		filters = new CombinationFilter();
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
		if (target.equals(locales.get(sv_SE))) {
			// Text to braille, Pas 1
			filters.add(new CharFilter(getResource("sv_SE-pas1.xml")));
			// Text to braille, Pas 2
			filters.add(new CharFilter(getResource("sv_SE-pas2.xml")));
		}
		// Remove redundant whitespace
		filters.add(new RegexFilter("(\\s+)", " "));
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