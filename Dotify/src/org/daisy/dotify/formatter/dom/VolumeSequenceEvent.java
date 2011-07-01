package org.daisy.dotify.formatter.dom;


public interface VolumeSequenceEvent {
	enum Type {STATIC, TABLE_OF_CONTENTS};
	public Type getType();
	public SequenceProperties getSequenceProperties();
}
