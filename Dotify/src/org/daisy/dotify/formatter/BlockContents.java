package org.daisy.dotify.formatter;

import java.util.Map;

public interface BlockContents extends EventContents {

	public void setEvaluateContext(Map<String, String> vars);
}
