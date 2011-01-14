package org.daisy.dotify.setups;

import java.net.URL;

public abstract class AbstractResourceLocator implements ResourceLocator {

	public URL getResource(String subpath) throws ResourceLocatorException {
		//TODO check the viability of this method
		URL url;
	    url = this.getClass().getResource(subpath);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+subpath);
	    }
	    if(url==null) throw new ResourceLocatorException("Cannot find resource path '" + subpath + "' relative to " + this.getClass().getCanonicalName());
	    return url;
	}

}
