package org.daisy.dotify.hyphenator.latex.rules;

import java.io.FileNotFoundException;
import java.net.URL;

public class LatexRulesLocator {

	public URL getCatalogResourceURL() throws FileNotFoundException {
		return getResource("hyphenation_tables.xml");
	}
	
	public URL getResource(String path) throws FileNotFoundException {
		URL url;
	    url = this.getClass().getResource(path);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+path);
	    }
	    if(url==null) throw new FileNotFoundException("Cannot find resource path '" + path + "' relative to " + this.getClass().getCanonicalName());
	    return url;
	}
}
