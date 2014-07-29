package org.daisy.dotify.obfl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.obfl.ExpressionFactory;

public abstract class OBFLExpressionBase {
	public final static String DEFAULT_PAGE_NUMBER_VARIABLE_NAME = "page";
	public final static String DEFAULT_VOLUME_NUMBER_VARIABLE_NAME = "volume";
	public final static String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
	public final static String DEFAULT_EVENT_VOLUME_NUMBER = "started-volume-number";
	public final static String DEFAULT_EVENT_PAGE_NUMBER = "started-page-number";
	protected final ExpressionFactory ef;
	protected final String exp;

	protected String pageNumberVariable;
	protected String volumeNumberVariable;
	protected String volumeCountVariable;
	protected String metaVolumeNumberVariable;
	protected String metaPageNumberVariable;
	
	public OBFLExpressionBase(String exp, ExpressionFactory ef, boolean extended) {
		this.ef = ef;
		this.exp = exp;
		this.pageNumberVariable = DEFAULT_PAGE_NUMBER_VARIABLE_NAME;
		this.volumeNumberVariable = DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
		this.volumeCountVariable = DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
		if (extended) {
			this.metaVolumeNumberVariable = DEFAULT_EVENT_VOLUME_NUMBER;
			this.metaPageNumberVariable = DEFAULT_EVENT_PAGE_NUMBER;
		} else {
			this.metaVolumeNumberVariable = null;
			this.metaPageNumberVariable = null;
		}
	}
	
	public void setPageNumberVariable(String pageNumberVariable) {
		if (pageNumberVariable==null) {
			this.pageNumberVariable = DEFAULT_PAGE_NUMBER_VARIABLE_NAME;
		} else {
			this.pageNumberVariable = pageNumberVariable;
		}
	}
	
	public void setVolumeNumberVariable(String volumeNumberVariable) {
		if (volumeNumberVariable==null) {
			this.volumeNumberVariable = DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
		} else {
			this.volumeNumberVariable = volumeNumberVariable;
		}
	}

	public void setVolumeCountVariable(String volumeCountVariable) {
		if (volumeCountVariable==null) {
			this.volumeCountVariable = DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
		} else {
			this.volumeCountVariable = volumeCountVariable;
		}
	}

	public void setMetaVolumeNumberVariable(String metaVolumeNumberVariable) {
		if (metaVolumeNumberVariable==null) {
			this.metaVolumeNumberVariable = DEFAULT_EVENT_VOLUME_NUMBER;
		} else {
			this.metaVolumeNumberVariable = metaVolumeNumberVariable;
		}
	}
	
	public void setMetaPageNumberVariable(String metaPageNumberVariable) {
		if (metaPageNumberVariable==null) {
			this.metaPageNumberVariable = DEFAULT_EVENT_PAGE_NUMBER;
		} else {
			this.metaPageNumberVariable = metaPageNumberVariable;
		}
	}

	protected Map<String, String> buildArgs(Context context) {
		HashMap<String, String> variables = new HashMap<String, String>();
		if (pageNumberVariable!=null) {
			variables.put(pageNumberVariable, ""+context.getCurrentPage());
		}
		if (volumeNumberVariable!=null) {
			variables.put(volumeNumberVariable, ""+context.getCurrentVolume());
		}
		if (volumeCountVariable!=null) {
			variables.put(volumeCountVariable, ""+context.getVolumeCount());
		}
		if (metaVolumeNumberVariable!=null) {
			variables.put(metaVolumeNumberVariable, ""+context.getMetaVolume());
		}
		if (metaPageNumberVariable!=null) {
			variables.put(metaPageNumberVariable, ""+context.getMetaPage());
		}
		return variables;
	}

}
