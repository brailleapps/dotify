package org.daisy.dotify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Provides common property values of the system
 * @author Joel Håkansson
 *
 */
public final class SystemProperties {
	/**
	 * Defines the system name
	 */
	public static final String SYSTEM_NAME;
	/**
	 * Defines the system build
	 */
	public static final String SYSTEM_BUILD;
	/**
	 * Defines the system release
	 */
	public static final String SYSTEM_RELEASE;
	
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	static {
		Class<SystemProperties> clazz = SystemProperties.class;
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		boolean failed = false;
		Attributes attr = null;
		if (!classPath.startsWith("jar")) {
		  // Class not from JAR
			failed = true;
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + 
			    "/META-INF/MANIFEST.MF";
			Manifest manifest;
			try {
				manifest = new Manifest(new URL(manifestPath).openStream());
				attr = manifest.getMainAttributes();
			} catch (MalformedURLException e) {
				failed = true;
			} catch (IOException e) {
				failed = true;
			}
		}
		if (failed || attr == null) {
			SYSTEM_NAME = "Dotify";
			SYSTEM_BUILD = "N/A";
			SYSTEM_RELEASE = "N/A";
		} else {
			SYSTEM_NAME = attr.getValue("Implementation-Title");
			SYSTEM_RELEASE = attr.getValue("Implementation-Version");
			SYSTEM_BUILD = attr.getValue("Repository-Revision");
		}
	}

}
