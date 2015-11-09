package org.daisy.dotify.formatter.impl;

import org.junit.Test;

import static org.junit.Assert.*;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.formatter.impl.Margin.Type;
public class FormatterCoreImplTest {

	@Test
	public void testBlockPropertiesHierarchy() {
		//Setup
		FormatterCoreImpl formatter = new FormatterCoreImpl();
		formatter.startBlock(new BlockProperties.Builder().rowSpacing(1.0f).firstLineIndent(1).orphans(2).widows(2).build());
		formatter.startBlock(new BlockProperties.Builder().rowSpacing(2.0f).firstLineIndent(2).orphans(3).widows(3).build());
		formatter.endBlock();
		formatter.endBlock();
		
		Margin left = (Margin)new Margin(Type.LEFT);
		Margin right = (Margin)new Margin(Type.RIGHT);
		left.add(new MarginComponent("", 0, 0));
		right.add(new MarginComponent("", 0, 0));
		Margin leftInner = (Margin)left.clone();
		leftInner.add(new MarginComponent("", 0, 0));
		Margin rightInner = (Margin)right.clone();
		rightInner.add(new MarginComponent("", 0, 0));
		
		RowDataProperties expectedOuter = new RowDataProperties.Builder().rowSpacing(1.0f).firstLineIndent(1).orphans(2).widows(2).leftMargin(left).rightMargin(right).build();
		RowDataProperties expectedInner = new RowDataProperties.Builder().rowSpacing(2.0f).firstLineIndent(2).orphans(3).widows(3).leftMargin(leftInner).rightMargin(rightInner).build();
		
		//Test
		assertEquals(3, formatter.size());
		assertEquals(expectedOuter, formatter.get(0).getRowDataProperties());
		assertEquals(expectedInner, formatter.get(1).getRowDataProperties());
		assertEquals(expectedOuter, formatter.get(2).getRowDataProperties());
	}
}
