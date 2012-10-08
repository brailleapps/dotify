package org.daisy.dotify.formatter.dom.book;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.tools.CompoundIterable;
import org.daisy.dotify.writer.Volume;

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