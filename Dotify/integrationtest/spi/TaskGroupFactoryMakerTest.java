package spi;

import org.daisy.dotify.api.cr.TaskGroupFactoryMakerService;
import org.daisy.dotify.consumer.cr.TaskGroupFactoryMaker;

import base.TaskGroupFactoryMakerTestbase;

public class TaskGroupFactoryMakerTest extends TaskGroupFactoryMakerTestbase {

	@Override
	public TaskGroupFactoryMakerService getTaskGroupFMS() {
		return TaskGroupFactoryMaker.newInstance();
	}

}