package org.daisy.dotify.system;

import java.io.File;
import java.io.IOException;

//@FunctionalInterface
public interface TempFileWriter {

	public void writeTempFile(File source, String taskName, int i) throws IOException;
}
