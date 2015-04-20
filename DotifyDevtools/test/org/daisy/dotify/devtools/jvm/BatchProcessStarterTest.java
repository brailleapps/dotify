package org.daisy.dotify.devtools.jvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class BatchProcessStarterTest {

	@Test
	public void testJavaBatch() throws FileNotFoundException, IOException, URISyntaxException {
		File pathToCommandsList = new File(this.getClass().getResource("resource-files/commands.txt").toURI());
		BatchProcessStarter starter = new BatchProcessStarter(true);
		starter.run(new FileInputStream(pathToCommandsList));
		// we don't assert anything, which isn't very good, but it's perhaps better than nothing
	}

}
