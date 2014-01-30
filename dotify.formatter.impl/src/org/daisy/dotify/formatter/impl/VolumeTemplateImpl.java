package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.VolumeContentBuilder;
import org.daisy.dotify.api.obfl.ExpressionFactory;


class VolumeTemplateImpl implements VolumeTemplate {
	public final static String DEFAULT_VOLUME_NUMBER_VARIABLE_NAME = "volume";
	public final static String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
	private String volumeNumberVar, volumeCountVar;
	private final String condition;
	private final int splitterMax;
	private final ExpressionFactory ef;
	private final VolumeContentBuilderImpl preVolumeContent, postVolumeContent;

	public VolumeTemplateImpl(String condition, Integer splitterMax, ExpressionFactory ef) {
		this.volumeNumberVar = DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
		this.volumeCountVar = DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
		this.condition = condition;
		this.splitterMax = splitterMax;
		this.ef = ef;
		this.preVolumeContent = new VolumeContentBuilderImpl(ef, this);
		this.postVolumeContent = new VolumeContentBuilderImpl(ef, this);
	}

	public boolean appliesTo(int volume, int volumeCount) {
		if (condition==null) {
			return true;
		}
		return ef.newExpression().evaluate(
				condition.replaceAll("\\$"+volumeNumberVar+"(?=\\W)", ""+volume).replaceAll("\\$"+volumeCountVar+"(?=\\W)", ""+volumeCount)
			).equals(true);
	}
	
	public Iterable<VolumeSequenceEvent> getPreVolumeContent() {
		return preVolumeContent;
	}

	public Iterable<VolumeSequenceEvent> getPostVolumeContent() {
		return postVolumeContent;
	}

	public String getVolumeNumberVariableName() {
		return volumeNumberVar;
	}

	public String getVolumeCountVariableName() {
		return volumeCountVar;
	}

	public int getVolumeMaxSize() {
		return splitterMax;
	}

	public void setVolumeNumberVariableName(String name) {
		if (name!=null) {
			volumeNumberVar = name;
		}
	}

	public void setVolumeCountVariableName(String name) {
		if (name!=null) {
			volumeCountVar = name;
		}
	}

	public VolumeContentBuilder getPreVolumeContentBuilder() {
		return preVolumeContent;
	}

	public VolumeContentBuilder getPostVolumeContentBuilder() {
		return postVolumeContent;
	}

}
