package org.daisy.dotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.daisy.dotify.config.ConfigurationsCatalog;
import org.daisy.dotify.input.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.TaskRunner;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.system.TaskSystemFactoryMaker;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.ResourceLocatorException;

import se.mtm.common.xml.XMLTools;
import se.mtm.common.xml.XMLToolsException;

/**
 * Provides an entry point for simple embedding of Dotify. To run, call <tt>Dotify.run</tt>.
 * 
 * @author Joel Håkansson
 *
 */
public class Dotify {
	private final static HashMap<String, String> extensionBindings;
	static {
		extensionBindings = new HashMap<String, String>();
		extensionBindings.put(".pef", SystemKeys.PEF_FORMAT);
		extensionBindings.put(".txt", SystemKeys.TEXT_FORMAT);
		extensionBindings.put(".obfl", SystemKeys.OBFL_FORMAT);
	}
	
	private final boolean writeTempFiles;
	private final boolean keepTempFilesOnSuccess;
	// hide default constructor to disable instantiation.
	private Dotify(Map<String, String> params) { 
		// get parameters
		writeTempFiles = "true".equals(params.get(SystemKeys.WRITE_TEMP_FILES));
		keepTempFilesOnSuccess = !("false".equals(params.get(SystemKeys.KEEP_TEMP_FILES_ON_SUCCESS)));
	}
	
	public static void run(File input, File output, String setup, String locale, Map<String, String> params) throws IOException, InternalTaskException {
		run(input, output, setup, FilterLocale.parse(locale), params);
	}

	/**
	 * Runs Dotify with the supplied parameters.
	 * @param input the input file
	 * @param output the output file
	 * @param setup the setup
	 * @param context the language/region context
	 * @param params additional parameters
	 * @throws IOException
	 * @throws InternalTaskException
	 */
	public static void run(File input, File output, String setup, FilterLocale context, Map<String, String> params) throws IOException, InternalTaskException {
		Logger logger = Logger.getLogger(Dotify.class.getCanonicalName());
		Dotify d = new Dotify(params);

		String cols = params.get("cols");
		if (cols==null || "".equals(cols)) {
			params.remove("cols");
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.putAll(params);

		map.put(SystemKeys.INPUT, input.getAbsolutePath());
		String inp = input.getName();
		int inx = inp.lastIndexOf('.');
		String inputFormat = "";
		if (inx>-1) {
			inputFormat = inp.substring(inx + 1);
			InputManagerFactoryMaker imfm = InputManagerFactoryMaker.newInstance();
			if (!imfm.listSupportedFileFormats().contains(inputFormat)) {
				logger.fine("No input factory for " + inputFormat);
				// attempt to detect a supported type
				try {
					if (XMLTools.isWellformedXML(input)) {
						inputFormat = "xml";
						logger.info("Input is well-formed xml.");
					}
				} catch (XMLToolsException e) {
					e.printStackTrace();
				}
			} else {
				logger.info("Found an input factory for " + inputFormat);
			}
		}
		
		map.put(SystemKeys.INPUT_FORMAT, inputFormat);

		String outputformat = params.get(SystemKeys.OUTPUT_FORMAT);
		if (outputformat==null || "".equals(outputformat)) {
			int indx = output.getName().lastIndexOf('.');
			if (indx>-1) {
				outputformat = extensionBindings.get(output.getName().substring(indx).toLowerCase());
			}
			if (outputformat==null) {
				throw new IllegalArgumentException("Cannot detect file format for output file. Please specify output format.");
			}
		}
		map.put(SystemKeys.OUTPUT_FORMAT, outputformat.toLowerCase());
		
		map.put(SystemKeys.SYSTEM_NAME, SystemProperties.SYSTEM_NAME);
		map.put(SystemKeys.SYSTEM_BUILD, SystemProperties.SYSTEM_BUILD);
		map.put(SystemKeys.SYSTEM_RELEASE, SystemProperties.SYSTEM_RELEASE);
		map.put("conversionDate", new Date().toString());

		map.put(SystemKeys.INPUT_URI, input.toURI().toString());
		
		// Add default values for optional parameters
		String dateFormat = params.get(SystemKeys.DATE_FORMAT);
		if (dateFormat==null || "".equals(dateFormat)) {
			dateFormat = SystemProperties.DEFAULT_DATE_FORMAT;
			map.put(SystemKeys.DATE_FORMAT, dateFormat);
		}
		final String tempFilesDirectory = params.get(SystemKeys.TEMP_FILES_DIRECTORY);

		if (map.get(SystemKeys.DATE)==null || "".equals(map.get(SystemKeys.DATE))) {
			map.put(SystemKeys.DATE, getDefaultDate(dateFormat));
		}
		if (map.get(SystemKeys.IDENTIFIER)==null || "".equals(map.get(SystemKeys.IDENTIFIER))) {
			String id = Double.toHexString(Math.random());
			id = id.substring(id.indexOf('.')+1);
			id = id.substring(0, id.indexOf('p'));
			map.put(SystemKeys.IDENTIFIER, "dummy-id-"+ id);
		}

		// Load additional settings from file
		if (map.get("config")==null || "".equals(map.get("config"))) {
			map.remove("config");
		} else {
			File config = new File(map.get("config"));
			Properties p = new Properties();
			FileInputStream in = new FileInputStream(config);
			p.loadFromXML(in);
			for (Object key : p.keySet()) {
				map.put(key.toString(), p.get(key).toString());
			}
		}
		
		TaskRunner tr = new TaskRunner();
		tr.setWriteTempFiles(d.writeTempFiles);
		tr.setKeepTempFilesOnSuccess(d.keepTempFilesOnSuccess);
		if (tempFilesDirectory!=null && !"".equals(tempFilesDirectory)) {
			tr.setTempFilesFolder(new File(tempFilesDirectory));
		}

		tr.setIdentifier("Dotify@" + Integer.toHexString((int)(System.currentTimeMillis()-1261440000000l)));

		// Load setup
		Map<String, Object> rp = d.loadSetup(map, setup, outputformat, context);

		// Run tasks
		try {
			TaskSystem ts = TaskSystemFactoryMaker.newInstance().newTaskSystem(outputformat, context);
			try {
				tr.runTasks(input, output, ts, rp);
			} catch (TaskSystemException e) {
				throw new RuntimeException("Unable to run '" +ts.getName() + "' with parameters " + rp, e);
			}
		} catch (TaskSystemFactoryException e) {
			throw new RuntimeException("Unable to retrieve a TaskSystem", e);
		}
	}
	
	private Map<String, Object> loadSetup(Map<String, String> guiParams, String setup, String outputformat, FilterLocale context) {
		ConfigurationsCatalog cm = ConfigurationsCatalog.newInstance();
		Properties p0;
		try {
			p0 = cm.getConfiguration(setup);
		} catch (ResourceLocatorException e) {
			//try as file
			p0 = new Properties();
			URL configURL;
			try {
				configURL = new URL(setup);
				try {
					p0.loadFromXML(configURL.openStream());
				} catch (FileNotFoundException e2) {
					throw new RuntimeException("Configuration file not found: " + configURL, e2);
				} catch (InvalidPropertiesFormatException e2) {
					throw new RuntimeException("Configuration file could not be parsed: " + configURL, e2);
				} catch (IOException e2) {
					throw new RuntimeException("IOException while reading configuration file: " + configURL, e2);
				}
			} catch (MalformedURLException e1) {
				throw new RuntimeException("'"+ setup + "' is not a known configuration nor a valid URL.", e1);
			}

		}
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Object key : p0.keySet()) {
			ret.put(key.toString(), p0.get(key));
		}

		// GUI parameters should take precedence
		ret.putAll(guiParams);

		return ret;
	}

	public static String getDefaultDate(String dateFormat) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(c.getTime());
	}

}
