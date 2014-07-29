package org.daisy.dotify.formatter.impl;

import java.util.Map;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.VolumeContentBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;


class VolumeTemplate implements VolumeTemplateBuilder {
	private final Condition condition;
	private final int splitterMax;
	private final VolumeContentBuilderImpl preVolumeContent, postVolumeContent;

	public VolumeTemplate(Map<String, TableOfContentsImpl> tocs, Map<String, ContentCollectionImpl> collections, Condition condition, Integer splitterMax) {
		this.condition = condition;
		this.splitterMax = splitterMax;
		this.preVolumeContent = new VolumeContentBuilderImpl(tocs, collections);
		this.postVolumeContent = new VolumeContentBuilderImpl(tocs, collections);
	}

	/**
	 * Test if this Template applies to this combination of volume and volume count.
	 * @param volume the volume to test
	 * @return returns true if the Template should be applied to the volume
	 */
	public boolean appliesTo(Context context) {
		if (condition==null) {
			return true;
		}
		return condition.evaluate(context);
	}
	
	public Iterable<VolumeSequence> getPreVolumeContent() {
		return preVolumeContent;
	}

	public Iterable<VolumeSequence> getPostVolumeContent() {
		return postVolumeContent;
	}

	/**
	 * Gets the maximum number of sheets allowed.
	 * @return returns the number of sheets allowed
	 */
	public int getVolumeMaxSize() {
		return splitterMax;
	}

	public VolumeContentBuilder getPreVolumeContentBuilder() {
		return preVolumeContent;
	}

	public VolumeContentBuilder getPostVolumeContentBuilder() {
		return postVolumeContent;
	}

}
