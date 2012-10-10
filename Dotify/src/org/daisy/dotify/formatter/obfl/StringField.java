package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.dom.Field;

public class StringField implements Field {
	private final Object obj;
	
	public StringField(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return obj.toString();
	}

}
