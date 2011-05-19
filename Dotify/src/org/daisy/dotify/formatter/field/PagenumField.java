package org.daisy.dotify.formatter.field;


/**
 * A PagenumField is a reference to some property of the physical pages in
 * the final document. Its value is resolved by the LayoutPerformer when its 
 * location in the flow is known.
 * 
 * @author joha
 *
 */
public class PagenumField extends NumeralField {
	
	public PagenumField(NumeralStyle style) {
		super(style);
	}

}
