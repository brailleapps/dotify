package org.daisy.dotify.obfl;

import java.util.Map;



public interface SequenceEvent extends Iterable<BlockEvent>, VolumeSequenceEvent {
	
	public void setEvaluateContext(Map<String, String> vars);

}
