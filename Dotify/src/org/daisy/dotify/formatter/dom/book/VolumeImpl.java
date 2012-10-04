package org.daisy.dotify.formatter.dom.book;

import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Volume;

class VolumeImpl implements Volume {
	private final Iterable<PageSequence> preVolume;
	private final Iterable<PageSequence> body;
	private final Iterable<PageSequence> postVolume;
	
	public VolumeImpl(Iterable<PageSequence> preVolume, Iterable<PageSequence> body, Iterable<PageSequence> postVolume) {
		this.preVolume = preVolume;
		this.body = body;
		this.postVolume = postVolume;
	}

	public Iterable<PageSequence> getPreVolumeContents() {
		return preVolume;
	}

	public Iterable<PageSequence> getPostVolumeContents() {
		return postVolume;
	}

	public Iterable<PageSequence> getBody() {
		return body;
	}

}