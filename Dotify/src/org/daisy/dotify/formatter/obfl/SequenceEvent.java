package org.daisy.dotify.formatter.obfl;

import java.util.Map;

import org.daisy.dotify.formatter.dom.VolumeSequenceEvent;


public interface SequenceEvent extends Iterable<BlockEvent>, VolumeSequenceEvent {
	
	public void setEvaluateContext(Map<String, String> vars);

}
