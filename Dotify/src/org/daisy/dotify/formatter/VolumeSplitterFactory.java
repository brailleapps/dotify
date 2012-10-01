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
 * @deprecated volume splitting should be controlled by OBFL markup, if additional logic is required
 * for volume splitting, modify OBFL to support it.
 */
public class VolumeSplitterFactory {
	
	protected VolumeSplitterFactory() {
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
			return i.next();
		}
		throw new RuntimeException("Cannot find splitter.");
	}
}
