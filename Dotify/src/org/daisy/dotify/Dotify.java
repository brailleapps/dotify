package org.daisy.dotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.config.ConfigurationsCatalog;
import org.daisy.dotify.setups.LocalizationManager;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.system.TaskSystemFactoryMaker;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.Progress;
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FileUtils;

public class Dotify {
	final static String TEMP_DIR;// = System.getProperty("java.io.tmpdir");
	private final static HashMap<String, String> extensionBindings;
	static {
		extensionBindings = new HashMap<String, String>();
		extensionBindings.put(".pef", SystemKeys.PEF_FORMAT);
		extensionBindings.put(".txt", SystemKeys.TEXT_FORMAT);
		extensionBindings.put(".obfl", SystemKeys.OBFL_FORMAT);
		String path = System.getProperty("java.io.tmpdir");
		if (path!=null && !"".equals(path) && new File(path).isDirectory()) {
			TEMP_DIR = path;
		} else {
			// user.home is guaranteed to be defined
			TEMP_DIR = System.getProperty("user.home");
		}
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
	public static void run(File input, File output, String setup, FilterLocale context, HashMap<String, String> params) throws IOException, InternalTaskException {
		Progress progress = new Progress();
		sendMessage(SystemProperties.SYSTEM_NAME + " started on " + progress.getStart());
		
		// get parameters
		boolean writeTempFiles = "true".equals(params.get(SystemKeys.WRITE_TEMP_FILES));

		File debug = new File(TEMP_DIR);
		String cols = params.get("cols");
		if (cols==null || "".equals(cols)) {
			params.remove("cols");
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.putAll(params);

		map.put(SystemKeys.INPUT, input.getAbsolutePath());
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
		String tempFilesDirectory = params.get(SystemKeys.TEMP_FILES_DIRECTORY);
		if (tempFilesDirectory!=null && !"".equals(tempFilesDirectory)) {
			File f = new File(tempFilesDirectory);
			if (f.isDirectory()) {
				debug = f;
			}
		}
		if (map.get(SystemKeys.DATE)==null || "".equals(map.get(SystemKeys.DATE))) {
			map.put(SystemKeys.DATE, getDefaultDate(dateFormat));
		}
		if (map.get(SystemKeys.IDENTIFIER)==null || "".equals(map.get(SystemKeys.IDENTIFIER))) {
			String id = Double.toHexString(Math.random());
			id = id.substring(id.indexOf('.')+1);
			id = id.substring(0, id.indexOf('p'));
			map.put(SystemKeys.IDENTIFIER, "dummy-id-"+ id);
		}
		
		try {
			URL res = new LocalizationManager().getLocalizationUrl(context);
			Properties p = new Properties();
			p.loadFromXML(res.openStream());
			for (Object key : p.keySet()) {
				map.put(key.toString(), p.get(key).toString());
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger(Dotify.class.getCanonicalName());
			log.fine("Failed to load localization");
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
		
		// Load setup
		ArrayList<InternalTask> tasks = new ArrayList<InternalTask>();
		TaskSystem ts = null;

		RunParameters rp = null;
		try {
			ConfigurationsCatalog cm = ConfigurationsCatalog.newInstance();
			URL url = null;
			try {
				url = cm.getConfigurationURL(setup);
			} catch (ResourceLocatorException e) {
				//try as file
				url = new URL(setup);
			}
			rp = RunParameters.load(url, map);

			ts = TaskSystemFactoryMaker.newInstance().newTaskSystem(outputformat, context);
			sendMessage("Adding tasks from TaskSystem: " + ts.getName());
			tasks.addAll(ts.compile(rp));
		} catch (TaskSystemException e) {
			throw new RuntimeException("Unable to load '" + (ts!=null?ts.getName():"") + "' with parameters " + rp, e);
		} catch (TaskSystemFactoryException e) {
			throw new RuntimeException("Unable to retrieve a TaskSystem", e);
		}
		
		sendMessage("About to run TaskSystem \"" + (ts!=null?ts.getName():"") + "\" with parameters " + rp);

		// Run tasks
		double i = 0;
		FileJuggler fj = new FileJuggler(input, output);
		NumberFormat nf = NumberFormat.getPercentInstance();
		for (InternalTask task : tasks) {
			sendMessage("Running " + task.getName());
			task.execute(fj.getInput(), fj.getOutput());
			if (writeTempFiles) {
				String it = ""+((int)i+1);
				while (it.length()<3) {
					it = "0" + it; 
				}
				File f = new File(debug, "debug_dotify_" + it + "_" + task.getName().replaceAll("[\\s:]+", "_"));
				Logger.getLogger(Dotify.class.getCanonicalName()).fine("Writing debug file: " + f);
				FileUtils.copy(fj.getOutput(), f);
			}
			fj.swap();
			i++;
			progress.setProgress(i/tasks.size());
			sendMessage(nf.format(progress.getProgress()) + " done. ETA " + progress.getETA());
			//progress(i/tasks.size());
		}
		fj.close();
		sendMessage(SystemProperties.SYSTEM_NAME + " finished in " + Math.round(progress.timeSinceStart()/100d)/10d + " s");
	}
	
	static String getDefaultDate(String dateFormat) {
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(c.getTime());
	}

	private static void sendMessage(String msg) {
		Logger.getLogger(Dotify.class.getCanonicalName()).log(Level.INFO, msg);
	}
}
