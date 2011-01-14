package org.daisy.dotify.system.tasks.layout.text;

import java.net.URL;

import org.daisy.util.i18n.UCharReplacer;

/**
 * Implements StringFilter using UCharReplacer.
 * 
 * @author  Joel Hakansson
 * @version 4 maj 2009
 * @since 1.0
 */
public class CharFilter implements StringFilter {
	private UCharReplacer ucr;
	
	/**
	 * Create a new CharFilter
	 * @param table relative path to replacement table, see UCharReplacement for more information
	 */
	public CharFilter(URL table) {
		this.ucr = new UCharReplacer();
		try {
			this.ucr.addSubstitutionTable(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String filter(String str) {
		return ucr.replace(str).toString();
	}

}
