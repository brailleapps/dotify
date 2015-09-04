package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.api.tasks.InternalTask;
import org.daisy.dotify.api.tasks.ReadOnlyTask;
import org.daisy.dotify.api.tasks.ReadWriteTask;
import org.daisy.dotify.api.tasks.TaskSystem;
import org.daisy.dotify.api.tasks.TaskSystemException;
import org.daisy.dotify.api.tasks.TaskSystemFactoryException;
import org.daisy.dotify.api.tasks.TaskSystemFactoryMakerService;
import org.daisy.dotify.common.io.TempFileHandler;
import org.junit.Test;

public abstract class TaskSystemFactoryMakerTestbase {
	
	public abstract TaskSystemFactoryMakerService getTaskSystemFMS();

	@Test
	public void testFactoryExists() {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		
		//Test
		assertNotNull("Factory exists.", factory);
	}

	@Test
	public void testGetFactoryForSwedish() throws TaskSystemFactoryException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		
		//Test
		assertNotNull(factory.getFactory("sv-SE", "pef"));
	}
	
	@Test
	public void testFactoryForSwedish() throws TaskSystemFactoryException, TaskSystemException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		TaskSystem tf = factory.newTaskSystem("sv-SE", "pef");
		assertNotNull(tf);
		HashMap<String, Object> options = new HashMap<String, Object>();
		//This test really shows how this code has to improve...
		File f = new File("integrationtest/base/resource-files/dtbook.xml");
		options.put(SystemKeys.INPUT_FORMAT, "xml");
		options.put(SystemKeys.INPUT, f.getAbsolutePath());
		List<InternalTask> tasks = tf.compile(options);
		
		//Test
		assertEquals(3, tasks.size());
		assertEquals("Conformance checker: validation-files/dtbook.sch", tasks.get(0).getName());
		assertEquals("Input to OBFL converter: xslt-files/dtbook2flow_sv_SE_braille.xsl", tasks.get(1).getName());
		assertEquals("OBFL to PEF converter", tasks.get(2).getName());
	}
	
	@Test
	public void runFactoryForSwedish() throws TaskSystemFactoryException, TaskSystemException, IOException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		TaskSystem tf = factory.newTaskSystem("sv-SE", "obfl");
		assertNotNull(tf);
		HashMap<String, Object> options = new HashMap<String, Object>();

		//This test really shows how this code has to improve...
		File f = new File("integrationtest/base/resource-files/dtbook.xml");
		options.put(SystemKeys.INPUT_FORMAT, "xml");
		options.put(SystemKeys.INPUT, f.getAbsolutePath());
		List<InternalTask> tasks = tf.compile(options);

		File out = File.createTempFile(this.getClass().getName(), ".tmp");
		try {
			TempFileHandler fj = new TempFileHandler(f, out);
			for (InternalTask task : tasks) {
				if (task instanceof ReadWriteTask) {
					((ReadWriteTask)task).execute(fj.getInput(), fj.getOutput());
					fj.reset();
				} else if (task instanceof ReadOnlyTask) {
					((ReadOnlyTask)task).execute(fj.getInput());
				} else {
				}
			}
			fj.close();
		} finally {
			if (!out.delete()) {
				out.deleteOnExit();
			}
		}
	}
	
	@Test
	public void testGetFactoryForEnglish() throws TaskSystemFactoryException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		
		//Test
		assertNotNull(factory.getFactory("en-US", "text"));
	}
}