package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.VolumeContentBuilder;


class VolumeTemplateImpl implements VolumeTemplate {
	private final Condition condition;
	private final int splitterMax;
	private final VolumeContentBuilderImpl preVolumeContent, postVolumeContent;

	public VolumeTemplateImpl(Condition condition, Integer splitterMax) {
		this.condition = condition;
		this.splitterMax = splitterMax;
		this.preVolumeContent = new VolumeContentBuilderImpl(this);
		this.postVolumeContent = new VolumeContentBuilderImpl(this);
	}

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
