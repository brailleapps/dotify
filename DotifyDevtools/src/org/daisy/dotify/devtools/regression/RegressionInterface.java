package org.daisy.dotify.devtools.regression;

import java.io.File;

import org.daisy.dotify.devtools.jvm.JVMStarter;

interface RegressionInterface {

	public JVMStarter requestStarter();

	public void returnStarter(JVMStarter starter);

	public String getPathToCLI();

	public File testOutputFolder();

	public void reportError();
}
