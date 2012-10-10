package org.daisy.dotify.formatter.obfl;

import java.util.Map;



interface SequenceEvent extends Iterable<BlockEvent>, VolumeSequenceEvent {
	
	public void setEvaluateContext(Map<String, String> vars);

}
