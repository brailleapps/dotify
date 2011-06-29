package org.daisy.dotify.formatter.dom;

public interface VolumeSequence {
	enum Type {STATIC, TABLE_OF_CONTENTS};
	public Type getType();
	public SequenceProperties getSequenceProperties();
}
