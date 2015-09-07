package org.daisy.dotify.devtools.regression;

import java.io.File;

import org.daisy.dotify.devtools.jvm.ProcessStarter;

interface RegressionInterface {

	public ProcessStarter requestStarter();

	public void returnStarter(ProcessStarter starter);

	public String getPathToCLI();

	public File testOutputFolder();

	public void reportError();
}
