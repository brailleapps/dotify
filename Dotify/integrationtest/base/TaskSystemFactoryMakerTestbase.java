package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.api.tasks.InternalTask;
import org.daisy.dotify.api.tasks.TaskSystem;
import org.daisy.dotify.api.tasks.TaskSystemException;
import org.daisy.dotify.api.tasks.TaskSystemFactoryException;
import org.daisy.dotify.api.tasks.TaskSystemFactoryMakerService;
import org.daisy.dotify.common.io.TempFileHandler;
import org.daisy.dotify.tasks.runner.TaskRunnerCore;
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
		//This test shows how this code has to improve...
		options.put(SystemKeys.INPUT_FORMAT, "xml");
		List<InternalTask> tasks = tf.compile(options);
		
		//Test
		assertEquals(2, tasks.size());
		assertEquals("XML Tasks Bundle", tasks.get(0).getName());
		assertEquals("OBFL to PEF converter", tasks.get(1).getName());
	}
	
	@Test
	public void runFactoryForSwedish() throws TaskSystemFactoryException, TaskSystemException, IOException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		TaskSystem tf = factory.newTaskSystem("sv-SE", "obfl");
		assertNotNull(tf);
		HashMap<String, Object> options = new HashMap<String, Object>();

		//This test shows how this code has to improve...
		options.put(SystemKeys.INPUT_FORMAT, "xml");
		List<InternalTask> tasks = tf.compile(options);

		File out = File.createTempFile(this.getClass().getName(), ".tmp");
		try {
			File f = new File("integrationtest/base/resource-files/dtbook.xml");
			TempFileHandler fj = new TempFileHandler(f, out);
			TaskRunnerCore core = new TaskRunnerCore(fj);
			for (InternalTask task : tasks) {
				core.runTask(task);
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