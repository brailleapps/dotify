package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.dom.VolumeSequenceEvent;
import org.daisy.dotify.formatter.dom.book.VolumeTemplate;
import org.daisy.dotify.formatter.utils.Expression;

class VolumeTemplateImpl implements VolumeTemplate {
	public final static String DEFAULT_VOLUME_NUMBER_VARIABLE_NAME = "volume";
	public final static String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
	private final String volumeNumberVar, volumeCountVar, condition;
	private final int splitterMax;
	private Iterable<VolumeSequenceEvent> preVolumeContent;
	private Iterable<VolumeSequenceEvent> postVolumeContent;

	public VolumeTemplateImpl(String volumeVar, String volumeCountVar, String condition, Integer splitterMax) {
		this.volumeNumberVar = (volumeVar!=null?volumeVar:DEFAULT_VOLUME_NUMBER_VARIABLE_NAME);
		this.volumeCountVar = (volumeCountVar!=null?volumeCountVar:DEFAULT_VOLUME_COUNT_VARIABLE_NAME);
		this.condition = condition;
		this.splitterMax = splitterMax;
	}

	public boolean appliesTo(int volume, int volumeCount) {
		if (condition==null) {
			return true;
		}
		return new Expression().evaluate(
				condition.replaceAll("\\$"+volumeNumberVar+"(?=\\W)", ""+volume).replaceAll("\\$"+volumeCountVar+"(?=\\W)", ""+volumeCount)
			).equals(true);
	}
	
	public void setPreVolumeContent(Iterable<VolumeSequenceEvent> preVolumeContent) {
		this.preVolumeContent = preVolumeContent;
	}
	
	public void setPostVolumeContent(Iterable<VolumeSequenceEvent> postVolumeContent) {
		this.postVolumeContent = postVolumeContent;
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

}
