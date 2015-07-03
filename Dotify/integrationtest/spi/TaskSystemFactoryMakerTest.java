package spi;

import org.daisy.dotify.api.cr.TaskSystemFactoryMakerService;
import org.daisy.dotify.consumer.cr.TaskSystemFactoryMaker;

import base.TaskSystemFactoryMakerTestbase;

public class TaskSystemFactoryMakerTest extends TaskSystemFactoryMakerTestbase {

	@Override
	public TaskSystemFactoryMakerService getTaskSystemFMS() {
		return TaskSystemFactoryMaker.newInstance();
	}
}