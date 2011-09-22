package org.daisy.dotify;

/**
 * Provides common property keys used in the system.
 * @author Joel Håkansson
 */
public interface SystemKeys {
	/**
	 * Defines a key for the system name 
	 */
	public final static String SYSTEM_NAME = "systemName";
	/**
	 * Defines a key for the system build
	 */
	public final static String SYSTEM_BUILD = "systemBuild";
	/**
	 * Defines a key for the system release 
	 */
	public final static String SYSTEM_RELEASE = "systemRelease";
	/**
	 * Defines a key for the input file path 
	 */
	public final static String INPUT = "input";
	/**
	 * Defines a key for the input uri 
	 */
	public final static String INPUT_URI = "input-uri";
	/**
	 * Defines a key for the output format 
	 */
	public final static String OUTPUT_FORMAT = "outputFormat";
	/**
	 * Defines a key for the temp files.
	 * Corresponding value should be the string "true" or "false" 
	 */
	public final static String WRITE_TEMP_FILES = "writeTempFiles";
	/**
	 * Defines a key for the temp files directory.
	 * Corresponding value should be a string containing a file path  
	 */
	public final static String TEMP_FILES_DIRECTORY = "tempFilesDirectory";
}
