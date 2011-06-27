package org.daisy.dotify.formatter;

import java.util.Map;

public interface SequenceEvent extends Iterable<BlockEvent>, VolumeSequence {
	
	public void setEvaluateContext(Map<String, String> vars);

}
