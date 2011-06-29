package org.daisy.dotify.formatter.dom;

import java.util.Map;


public interface SequenceEvent extends Iterable<BlockEvent>, VolumeSequence {
	
	public void setEvaluateContext(Map<String, String> vars);

}
