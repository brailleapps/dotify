package org.daisy.dotify.formatter.impl;

import java.util.Map;



interface SequenceEvent extends Iterable<BlockEvent>, VolumeSequenceEvent {
	
	public void setEvaluateContext(Map<String, String> vars);

}
