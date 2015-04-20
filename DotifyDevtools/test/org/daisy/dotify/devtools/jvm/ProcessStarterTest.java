package org.daisy.dotify.devtools.jvm;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProcessStarterTest {

	@Test
	public void testProcessStarter() throws Exception {
		ProcessStarter starter = new ProcessStarter();
		int ret = starter.startProcess(ProcessStarter.buildJavaCommand(new String[]{"-version"}));
		assertEquals(0, ret);
	}
}
