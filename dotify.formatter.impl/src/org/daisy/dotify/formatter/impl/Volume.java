package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.tools.CompoundIterable;

/**
 * Provides a container for a physical volume of braille
 * @author Joel Håkansson
 */
class Volume {
	private final CompoundIterable<PageSequence> ret;
	
	Volume(Iterable<PageSequence> preVolume, Iterable<PageSequence> body, Iterable<PageSequence> postVolume) {
		List<Iterable<PageSequence>> contents = new ArrayList<Iterable<PageSequence>>();
		contents.add(preVolume);
		contents.add(body);
		contents.add(postVolume);
		this.ret = new CompoundIterable<PageSequence>(contents);
	}

	/**
	 * Gets the contents
	 * @return returns the contents
	 */
	Iterable<PageSequence> getContents() {
		return ret;
	}

}