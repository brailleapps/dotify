package org.daisy.dotify.translator.attributes;

import org.junit.Test;

public class DefaultTextAttributeTest {

	@Test(expected = IllegalArgumentException.class)
	public void testTranslatorAttributes_UnmatchedAttributesList() {
		new DefaultTextAttribute.Builder(105)
			.add(new DefaultTextAttribute.Builder(10, "bold").build())
			.build();
	}

}
