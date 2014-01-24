package org.daisy.dotify.obfl;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.junit.Test;

public class FormatterCoreEventImplTest {
	

	public FormatterCoreEventImplTest() {
	}
	
	@Test
	public void testEvent() {
		String exp = "BLOCK\n chars1\n BLOCK\n   chars2\n   MARKER\n   LEADER\n   chars3\n chars4\nBLOCK\n chars5\n";
		FormatterCoreEventImpl ev = new FormatterCoreEventImpl();
		ev.startBlock(null);
		ev.addChars("chars1", null);
		ev.startBlock(null);
		ev.addChars("chars2", null);
		ev.insertMarker(new Marker("", ""));
		ev.insertLeader(new Leader.Builder().build());
		ev.addChars("chars3", null);
		ev.endBlock();
		ev.addChars("chars4", null);
		ev.endBlock();
		ev.startBlock(null);
		ev.addChars("chars5", null);
		ev.endBlock();
		StringWriter sw = new StringWriter();
		System.setProperty("line.separator", "\n");
		PrintWriter pw = new PrintWriter(sw);
		ev.printContents(pw);
		assertEquals(exp, sw.toString());
	}

}
