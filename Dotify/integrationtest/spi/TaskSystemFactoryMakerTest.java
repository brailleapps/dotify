package spi;

import org.daisy.dotify.api.tasks.TaskSystemFactoryMakerService;
import org.daisy.dotify.consumer.tasks.TaskSystemFactoryMaker;

import base.TaskSystemFactoryMakerTestbase;

public class TaskSystemFactoryMakerTest extends TaskSystemFactoryMakerTestbase {

	@Override
	public TaskSystemFactoryMakerService getTaskSystemFMS() {
		return TaskSystemFactoryMaker.newInstance();
	}
}