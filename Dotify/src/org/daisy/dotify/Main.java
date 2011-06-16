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

import org.daisy.dotify.input.InputManagerTaskSystem;
import org.daisy.dotify.input.InputManagerTaskSystemFactory;
import org.daisy.dotify.setups.ConfigUrlLocator;
import org.daisy.dotify.setups.TaskSystemFactory;
import org.daisy.dotify.setups.TaskSystemFactory.OutputFormat;
import org.daisy.dotify.setups.TaskSystemFactory.Setup;
import org.daisy.dotify.setups.TaskSystemFactoryException;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.Progress;
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FileUtils;


public class Main {
	private final Logger logger;

	public Main() {
		this.logger = Logger.getLogger(Main.class.getCanonicalName());
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InternalTaskException 
	 */
	public static void main(String[] args) throws InternalTaskException, IOException {
		// TODO: Use framework from Braille Utils here!
		if (args.length!=2) {
			System.out.println("Expected two arguments path_to_input path_to_output");
			System.exit(-1);
		}
		File input = new File(args[0]);
		if (!input.exists()) {
			System.out.println("Cannot find input file: " + input);
			System.exit(-2);
		}
		File output = new File(args[1]);
		HashMap<String, String> props = new HashMap<String, String>();
		//props.put("debug", "true");
		props.put("tempFilesDirectory", "C:\\Temp");
		props.put("writeTempFiles", "true");
		Main m = new Main();
		m.run(input, output, OutputFormat.PEF, Setup.sv_SE, props);
	}

	public void run(File input, File output, OutputFormat outputformat, Setup setup, HashMap<String, String> params) throws IOException, InternalTaskException {
		Progress progress = new Progress();
		
		// get parameters
		boolean writeTempFiles = "true".equals(params.get("writeTempFiles"));
		// user.home is guaranteed to be defined
		File debug = new File(System.getProperty("user.home"));
		String cols = params.get("cols");
		if (cols==null || "".equals(cols)) {
			params.remove("cols");
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.putAll(params);

		map.put(SystemKeys.INPUT, input.getAbsolutePath());
		map.put(SystemKeys.OUTPUT_FORMAT, outputformat.toString().toLowerCase());
		
		map.put(SystemKeys.SYSTEM_NAME, SystemProperties.SYSTEM_NAME);
		map.put(SystemKeys.SYSTEM_BUILD, SystemProperties.SYSTEM_BUILD);
		map.put(SystemKeys.SYSTEM_RELEASE, SystemProperties.SYSTEM_RELEASE);
		map.put("conversionDate", new Date().toString());

		map.put(SystemKeys.INPUT_URI, input.toURI().toString());
		
		// Add default values for optional parameters
		String dateFormat = params.get("dateFormat");
		if (dateFormat==null || "".equals(dateFormat)) {
			dateFormat = "yyyy-MM-dd";
			map.put("dateFormat", dateFormat);
		}
		String tempFilesDirectory = params.get("tempFilesDirectory");
		if (tempFilesDirectory!=null && !"".equals(tempFilesDirectory)) {
			File f = new File(tempFilesDirectory);
			if (f.exists() && f.isDirectory()) {
				debug = f;
			}
		}
		if (map.get("date")==null || "".equals(map.get("date"))) {
		    Calendar c = Calendar.getInstance();
		    c.setTime(new Date());
		    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			map.put("date", sdf.format(c.getTime()));
		}
		if (map.get("identifier")==null || "".equals(map.get("identifier"))) {
			String id = Double.toHexString(Math.random());
			id = id.substring(id.indexOf('.')+1);
			id = id.substring(0, id.indexOf('p'));
			map.put("identifier", "dummy-id-"+ id);
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
		
		//InputDetector
		InputManagerTaskSystem idts = 
			InputManagerTaskSystemFactory.newInstance()
				.newInputDetectorTaskSystem(
					FilterLocale.parse(setup.toString().replace('_', '-'))
				);

		RunParameters rp = null;
		try {
			URL configURL = new ConfigUrlLocator().getResourceURL(outputformat, setup);
			rp = RunParameters.load(configURL, map);
			
			tasks.addAll(idts.compile(rp));
			ts = new TaskSystemFactory().newTaskSystem(outputformat, setup);
			tasks.addAll(ts.compile(rp));
		} catch (TaskSystemException e) {
			throw new RuntimeException("Unable to load '" + (ts!=null?ts.getName():"") + "' with parameters " + rp.getProperties().toString(), e);
		} catch (TaskSystemFactoryException e) {
			throw new RuntimeException("Unable to retrieve a TaskSystem", e);
		}

		sendMessage("About to run TaskSystem \"" + (ts!=null?ts.getName():"") + "\" with parameters " + rp.getProperties().toString());

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
				FileUtils.copy(fj.getOutput(), new File(debug, "debug_dtbook2pef_" + it + "_" + task.getName().replaceAll("[\\s:]+", "_")));
			}
			fj.swap();
			i++;
			progress.setProgress(i/tasks.size());
			sendMessage(nf.format(progress.getProgress()) + " done. ETA " + progress.getETA());
			//progress(i/tasks.size());
		}
		fj.close();
		sendMessage("Completed task in " + Math.round(progress.timeSinceStart()/100d)/10d + " s");
	}

	private void sendMessage(String msg) {
		logger.log(Level.INFO, msg);
	}

}
