package org.daisy.dotify.api.formatter;

/**
 * Provides a static string field.
 * 
 * @author Joel Håkansson
 */
public class StringField implements Field {
	private final Object obj;
	
	/**
	 * Creates a new string field using the specified object.
	 * The instance's toString method will return the same value
	 * as the toString method of the supplied object.
	 * @param obj the object to use for this field.
	 */
	public StringField(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return obj.toString();
	}

}
