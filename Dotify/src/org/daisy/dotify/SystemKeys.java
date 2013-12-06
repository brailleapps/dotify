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
	
	public final static String INPUT_FORMAT = "inputFormat";
	/**
	 * Defines a key for the input uri 
	 */
	public final static String INPUT_URI = "input-uri";
	/**
	 * Defines a key for the output format 
	 */
	public final static String OUTPUT_FORMAT = "outputFormat";
	
	public final static String TEMPLATE = "template";
	
	public final static String DATE_FORMAT = "dateFormat";
	public final static String DATE = "date";
	public final static String IDENTIFIER = "identifier";
	
	public final static String PEF_FORMAT = "pef";
	public final static String TEXT_FORMAT = "text";
	public final static String OBFL_FORMAT = "obfl";
	
	/**
	 * Defines a key for the configuration
	 */
	public final static String CONFIGURATION = "configuration";
	/**
	 * Defines a key for the temp files.
	 * Corresponding value should be the string "true" or "false" 
	 */
	public final static String WRITE_TEMP_FILES = "writeTempFiles";
	/**
	 * Defines a key for keeping temp files on success
	 * Corresponding value should be the string "true" or "false"
	 */
	public final static String KEEP_TEMP_FILES_ON_SUCCESS = "keepTempFilesOnSuccess";
	/**
	 * Defines a key for the temp files directory.
	 * Corresponding value should be a string containing a file path
	 */
	public final static String TEMP_FILES_DIRECTORY = "tempFilesDirectory";
}
