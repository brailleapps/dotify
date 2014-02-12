package org.daisy.dotify.impl.input.epub;

import java.io.OutputStream;

import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.ReadWriteTask;

import se.mtm.common.io.InputStreamMaker;

public class Epub3Task extends ReadWriteTask {

	Epub3Task(String name) {
		super(name);
	}

	@Override
	public void execute(InputStreamMaker input, OutputStream output) throws InternalTaskException {

	}

}
