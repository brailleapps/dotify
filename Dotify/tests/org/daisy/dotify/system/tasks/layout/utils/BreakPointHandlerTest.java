package org.daisy.dotify.system.tasks.layout.utils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.formatter.utils.BreakPoint;
import org.daisy.dotify.formatter.utils.BreakPointHandler;
import org.junit.Test;

public class BreakPointHandlerTest {

	@Test
	public void testHardBreak() {
		// case 1
		BreakPointHandler bph = new BreakPointHandler("citat/blockcitat20");
		BreakPoint bp = bph.nextRow(17);
		assertEquals("citat/blockcitat2", bp.getHead());
		assertEquals("0", bp.getTail());
		assertTrue(bp.isHardBreak());
		// case 2
		bph = new BreakPointHandler("citat/blockcitat20");
		bp = bph.nextRow(1);
		assertEquals("c", bp.getHead());
		assertEquals("itat/blockcitat20", bp.getTail());
		assertTrue(bp.isHardBreak());
		// case 3
		bph = new BreakPointHandler("citat blockcitat20");
		bp = bph.nextRow(4);
		assertEquals("cita", bp.getHead());
		assertEquals("t blockcitat20", bp.getTail());
		assertTrue(bp.isHardBreak());
	}
	
	@Test
	public void testBreakBefore() {
		BreakPointHandler bph = new BreakPointHandler("citat/blockcitat20");
		BreakPoint bp = bph.nextRow(0);
		assertEquals("", bp.getHead());
		assertEquals("citat/blockcitat20", bp.getTail());
		assertTrue(!bp.isHardBreak());		
	}
	
	@Test
	public void testBreakAfter() {
		BreakPointHandler bph = new BreakPointHandler("citat/blockcitat20");
		BreakPoint bp = bph.nextRow(35);
		assertEquals("citat/blockcitat20", bp.getHead());
		assertEquals("", bp.getTail());
		assertTrue(!bp.isHardBreak());
	}
	
	@Test
	public void testSoftBreakIncWhiteSpace() {
		BreakPointHandler bph = new BreakPointHandler("citat blockcitat20");
		BreakPoint bp = bph.nextRow(5);
		assertEquals("citat ", bp.getHead());
		assertEquals("blockcitat20", bp.getTail());
		assertTrue(!bp.isHardBreak());
	}
	
	@Test
	public void testHyphen() {
		BreakPointHandler bph = new BreakPointHandler("citat-blockcitat20");
		BreakPoint bp = bph.nextRow(12);
		assertEquals("citat-", bp.getHead());
		assertEquals("blockcitat20", bp.getTail());
		assertTrue(!bp.isHardBreak());
		
		bph = new BreakPointHandler("Negative number: -154");
		bp = bph.nextRow(19);
		assertEquals("Negative number: ", bp.getHead());
		assertEquals("-154", bp.getTail());
		assertTrue(!bp.isHardBreak());
		
		bph = new BreakPointHandler("Negative numbers - odd! (and even)");
		bp = bph.nextRow(18);
		assertEquals("Negative numbers - ", bp.getHead());
		assertEquals("odd! (and even)", bp.getTail());
		assertTrue(!bp.isHardBreak());
		
		bph = new BreakPointHandler("Negative numbers - odd! (and even)");
		bp = bph.nextRow(17);
		assertEquals("Negative numbers ", bp.getHead());
		assertEquals("- odd! (and even)", bp.getTail());
		assertTrue(!bp.isHardBreak());
	}
	
	@Test
	public void testHyphenKeepRemove() {
		BreakPointHandler bph = new BreakPointHandler("at the ev­i­dence on the ev­i­dence");
		BreakPoint bp = bph.nextRow(11);
		assertEquals("at the evi-", bp.getHead());
		assertEquals("dence on the ev­i­dence", bp.getTail());
		assertTrue(!bp.isHardBreak());
	}
	
}