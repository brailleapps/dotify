package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.tools.CompoundIterable;

class VolumeImpl implements Volume {
	private final CompoundIterable<PageSequence> ret;
	
	public VolumeImpl(Iterable<PageSequence> preVolume, Iterable<PageSequence> body, Iterable<PageSequence> postVolume) {
		List<Iterable<PageSequence>> contents = new ArrayList<Iterable<PageSequence>>();
		contents.add(preVolume);
		contents.add(body);
		contents.add(postVolume);
		this.ret = new CompoundIterable<PageSequence>(contents);
	}

	public Iterable<PageSequence> getContents() {
		return ret;
	}

}