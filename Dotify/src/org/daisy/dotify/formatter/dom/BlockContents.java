package org.daisy.dotify.formatter.dom;

import java.util.Map;


public interface BlockContents extends EventContents {

	public void setEvaluateContext(Map<String, String> vars);
}
