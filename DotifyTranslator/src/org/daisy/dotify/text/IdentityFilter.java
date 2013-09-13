package org.daisy.dotify.text;

import org.daisy.dotify.api.translator.StringFilter;


public class IdentityFilter implements StringFilter {

	public String filter(String str) {
		return str;
	}

}
