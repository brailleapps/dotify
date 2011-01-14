package org.daisy.dotify.system.tasks.layout.text;

import java.util.Locale;

/**
 * Custom Locale implementation. 
 * @author joha
 *
 */
public class FilterLocale {
	private final String lang, country, variant, str;
	
	private FilterLocale(String lang, String country, String variant) {
		this.lang = lang.intern();
		this.country = country.intern();
		this.variant = variant.intern();
		this.str = (lang + (country.equals("") ? "" : "-" + country + (variant.equals("") ? "" : "-" + variant))).intern();
	}
	
	public static FilterLocale parse(String locale) {
		if (!locale.matches("([a-zA-Z]{1,8}(\\-[0-9a-zA-Z]{1,8})*)?")) {
			throw new IllegalArgumentException("Not a valid locale as defined by IETF RFC 3066: " + locale);
		}
		String[] parts = locale.split("-", 3);
		String lang = parts[0].toLowerCase();
		String country = "";
		String variant = "";
		if (parts.length>=2) {
			country = parts[1].toUpperCase();
		}
		if (parts.length>=3) {
			variant = parts[2];
		}
		return new FilterLocale(lang, country, variant);
	}
	
	public Locale toLocale() {
		return new Locale(lang, country, variant);
	}
	
	public String getLanguage() {
		return lang;
	}
	
	public String getCountry() {
		return country;
	}
	
	public String getVariant() {
		return variant;
	}

	public boolean equals(FilterLocale other) {
		// str is pooled using intern(), so str == other.str implies that str.equals(other.str)
		return str == other.str;
	}
	
	/**
	 * This locale is a subtype of the other locale
	 * @param other
	 * @return returns true if this locale is a subtype of the supplied locale
	 */
	public boolean isA(FilterLocale other) {
		// all strings are pooled so str == other.str implies that str.equals(other.str)
		if (other.variant != "" && this.variant != other.variant) {
			return false;
		}
		if (other.country != "" && this.country != other.country) {
			return false;
		}
		if (this.lang != other.lang) {
			return false;
		}
		return true;

	}

	public String toString() {
		return str;
	}


}
