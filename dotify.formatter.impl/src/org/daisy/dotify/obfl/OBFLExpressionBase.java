package org.daisy.dotify.obfl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.obfl.ExpressionFactory;

public abstract class OBFLExpressionBase {
	public final static String DEFAULT_VOLUME_NUMBER_VARIABLE_NAME = "volume";
	public final static String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
	public final static String DEFAULT_EVENT_VOLUME_NUMBER = "started-volume-number";
	protected final ExpressionFactory ef;
	protected final String exp;
	
	protected String volumeNumberVariable;
	protected String volumeCountVariable;
	protected String metaVolumeNumberVariable;
	
	public OBFLExpressionBase(String exp, ExpressionFactory ef, boolean extended) {
		this.ef = ef;
		this.exp = exp;
		this.volumeNumberVariable = DEFAULT_VOLUME_NUMBER_VARIABLE_NAME;
		this.volumeCountVariable = DEFAULT_VOLUME_COUNT_VARIABLE_NAME;
		if (extended) {
			this.metaVolumeNumberVariable = DEFAULT_EVENT_VOLUME_NUMBER;
		} else {
			this.metaVolumeNumberVariable = null;
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

	protected Map<String, String> buildArgs(Context context) {
		HashMap<String, String> variables = new HashMap<String, String>();
		if (volumeNumberVariable!=null) {
			variables.put(volumeNumberVariable, ""+context.getCurrentVolume());
		}
		if (volumeCountVariable!=null) {
			variables.put(volumeCountVariable, ""+context.getVolumeCount());
		}
		if (metaVolumeNumberVariable!=null) {
			variables.put(metaVolumeNumberVariable, ""+context.getMetaVolume());
		}
		return variables;
	}

}
