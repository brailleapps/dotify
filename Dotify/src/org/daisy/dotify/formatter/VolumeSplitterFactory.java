package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

/**
 * Provides a factory for volume splitters. The factory will instantiate 
 * the first VolumeSplitter it encounters when querying the services API.
 * 
 * This behavior is temporary, since a volume splitter might be selected 
 * through a UI based on a set of properties or from a list.
 * 
 * @author Joel Håkansson
 */
public class VolumeSplitterFactory {
	private Integer targetVolumeSize;
	
	protected VolumeSplitterFactory() {
		this.targetVolumeSize = null;
	}
	
	public void setTargetVolumeSize(int targetVolumeSize) {
		this.targetVolumeSize = targetVolumeSize;
	}

	public static VolumeSplitterFactory newInstance() {
		Iterator<VolumeSplitterFactory> i = ServiceRegistry.lookupProviders(VolumeSplitterFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new VolumeSplitterFactory();
	}
	
	public VolumeSplitter newSplitter() {
		Iterator<VolumeSplitter> i = ServiceRegistry.lookupProviders(VolumeSplitter.class);
		while (i.hasNext()) {
			VolumeSplitter ret = i.next();
			if (targetVolumeSize!=null) {
				ret.setTargetVolumeSize(targetVolumeSize);
			}
			return ret;
		}
		throw new RuntimeException("Cannot find splitter.");
	}
}
