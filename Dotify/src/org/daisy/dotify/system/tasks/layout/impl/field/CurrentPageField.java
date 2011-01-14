package org.daisy.dotify.system.tasks.layout.impl.field;

import org.daisy.dotify.system.tasks.layout.impl.field.NumeralField.NumeralStyle;

/**
 * CurrentPageField is a reference to the current page in
 * the final document. Its value is resolved by the LayoutPerformer when its 
 * location in the flow is known.
 * 
 * @author joha
 *
 */
public class CurrentPageField extends PagenumField {
	public static enum PagenumType {
		/**
		 * The field represents the current page
		 */
		CURRENT_PAGE
		}

	public CurrentPageField(NumeralStyle style) {
		super(style);
	}

}
