package org.daisy.dotify.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Text2ObflTask extends ReadWriteTask {
	private final String encoding;
	
	public Text2ObflTask(String name) {
		this(name, "utf-8");
	}

	public Text2ObflTask(String name, String encoding) {
		super(name);
		this.encoding = encoding;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			Text2ObflWriter fw = new Text2ObflWriter(input, output, encoding);
			fw.parse();
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException", e);
		}
	}

}
