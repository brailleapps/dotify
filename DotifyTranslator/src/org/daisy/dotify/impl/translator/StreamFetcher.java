package org.daisy.dotify.impl.translator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class StreamFetcher {
	
	private final static String RESOURCE_BASE = "resource-files/";
	private final static String APP_BASE = "scripts/";

	private StreamFetcher() { }

	static InputStream getInputStream(String path) throws FileNotFoundException {
		try {
			return new FileInputStream(new File(APP_BASE + path));
		} catch (FileNotFoundException e) { }
		URL url = StreamFetcher.class.getResource(RESOURCE_BASE + path);
		if (url!=null) {
			try {
				return url.openStream();
			} catch (IOException e2) { }
		}
		throw new FileNotFoundException("Cannot find " + path);
	}
}
