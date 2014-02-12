package org.daisy.dotify.impl.input.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.ReadWriteTask;

import se.mtm.common.io.InputStreamMaker;

class Text2ObflTask extends ReadWriteTask {
	private final String encoding;
	private final String rootLang;
	
	Text2ObflTask(String name, String rootLang) {
		this(name, rootLang, "utf-8");
	}

	Text2ObflTask(String name, String rootLang, String encoding) {
		super(name);
		this.rootLang = rootLang;
		this.encoding = encoding;
	}

	@Override
	public void execute(InputStreamMaker input, OutputStream output) throws InternalTaskException {
		try {
			Text2ObflWriter fw = new Text2ObflWriter(input.newInputStream(), output, encoding);
			fw.setRootLang(rootLang);
			fw.parse();
		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException", e);
		}
	}

}
