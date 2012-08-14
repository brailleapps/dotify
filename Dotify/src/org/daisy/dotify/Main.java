package org.daisy.dotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.braille.ui.AbstractUI;
import org.daisy.dotify.setups.ConfigUrlLocator;
import org.daisy.dotify.system.InputManager;
import org.daisy.dotify.system.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.system.TaskSystemFactoryMaker;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.Progress;
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.FileUtils;

/**
 * Provides a command line entry point to Dotify.
 * @author Joel Håkansson
 */
public class Main extends AbstractUI {
	private final static String TEMP_DIR;// = System.getProperty("java.io.tmpdir");
	private final static HashMap<String, String> extensionBindings;
	static {
		extensionBindings = new HashMap<String, String>();
		extensionBindings.put(".pef", SystemKeys.PEF_FORMAT);
		extensionBindings.put(".txt", SystemKeys.TEXT_FORMAT);
		String path = System.getProperty("java.io.tmpdir");
		if (path!=null && !"".equals(path) && new File(path).isDirectory()) {
			TEMP_DIR = path;
		} else {
			// user.home is guaranteed to be defined
			TEMP_DIR = System.getProperty("user.home");
		}
	}
	private final Logger logger;

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;

	public Main() {
		this.logger = Logger.getLogger(Main.class.getCanonicalName());

		this.reqArgs = new ArrayList<Argument>();
		reqArgs.add(new Argument("path_to_input", "Path to the input file"));
		reqArgs.add(new Argument("path_to_output", "Path to the output file"));
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			ConfigUrlLocator c =  new ConfigUrlLocator();
			for (Object o : c.getKeys()) {
				vals.add(new Definition(o.toString(), "A setup"));
			}
			vals.add(new Definition("[other]", "Path to setup file"));
			reqArgs.add(new Argument("setup", "The formatting setup to use", vals));
		}
		
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			InputManagerFactoryMaker m = InputManagerFactoryMaker.newInstance();
			for (String o : m.listSupportedLocales()) {
				vals.add(new Definition(o, "A context locale"));
			}
			reqArgs.add(new Argument("locale", "The target locale for the result", vals));
		}
		
		this.optionalArgs = new ArrayList<OptionalArgument>();
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			vals.add(new Definition(SystemKeys.PEF_FORMAT, "write result in PEF-format"));
			vals.add(new Definition(SystemKeys.TEXT_FORMAT, "write result as text"));
			optionalArgs.add(new OptionalArgument(SystemKeys.OUTPUT_FORMAT, "Specifies output format", vals, "[detect]"));
		}
		optionalArgs.add(new OptionalArgument(SystemKeys.IDENTIFIER, "Sets identifier in meta data (if available)", "[generated value]"));
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			vals.add(new Definition("true", "outputs temp files"));
			vals.add(new Definition("false", "does not output temp files"));
			optionalArgs.add(new OptionalArgument(SystemKeys.WRITE_TEMP_FILES, "Writes temp files", vals, "true"));
		}
		optionalArgs.add(new OptionalArgument(SystemKeys.TEMP_FILES_DIRECTORY, "Path to temp files directory", TEMP_DIR));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE, "Sets date in meta data (if available)", getDefaultDate(SystemProperties.DEFAULT_DATE_FORMAT)));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE_FORMAT, "Date format in meta data (if available and date is not specified)", SystemProperties.DEFAULT_DATE_FORMAT));
	}
	
	private String getDefaultDate(String dateFormat) {
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(c.getTime());
	}

	/**
	 * Provides a entry point for Dotify from the command line.
	 * @param args command line arguments
	 * @throws IOException 
	 * @throws InternalTaskException 
	 * @throws TaskSystemFactoryException 
	 */
	public static void main(String[] args) throws InternalTaskException, IOException, TaskSystemFactoryException {
		Main m = new Main();
		if (args.length<4) {
			System.out.println("Expected at least four arguments");
			
			System.out.println();
			m.displayHelp(System.out);
			Main.exitWithCode(ExitCode.MISSING_ARGUMENT);
			
			//System.exit(-1);
		}

		List<String> p = m.getRequired(args);
		// remove required arguments
		File input = new File(p.get(0));
		//File input = new File(args[0]);
		if (!input.exists()) {
			System.out.println("Cannot find input file: " + input);
			Main.exitWithCode(ExitCode.MISSING_RESOURCE);

			//System.exit(-2);
		}
		
		File output = new File(p.get(1));
		String setup = p.get(2);
		String context = p.get(3);
		
		//File output = new File(args[1]);
		HashMap<String, String> props = new HashMap<String, String>();
		//props.put("debug", "true");
		//props.put(SystemKeys.TEMP_FILES_DIRECTORY, TEMP_DIR);
		props.put(SystemKeys.WRITE_TEMP_FILES, "true");

		props.putAll(m.getOptional(args));
		
		m.run(input, output, setup, FilterLocale.parse(context), props);
	}

	/**
	 * Runs Dotify with the supplied parameters.
	 * @param input the input file
	 * @param output the output file
	 * @param outputformat the output format
	 * @param setup the setup
	 * @param params additional parameters
	 * @throws IOException
	 * @throws InternalTaskException
	 */
	public void run(File input, File output, String setup, FilterLocale context, HashMap<String, String> params) throws IOException, InternalTaskException {
		Progress progress = new Progress();
		
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
		InputManager idts = InputManagerFactoryMaker.newInstance().newInputManager(context);

		RunParameters rp = null;
		try {
			rp = RunParameters.load(idts.getConfigurationURL(setup), map);
			
			tasks.addAll(idts.compile(rp));
			ts = TaskSystemFactoryMaker.newInstance().newTaskSystem(outputformat, context);
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
				File f = new File(debug, "debug_dotify_" + it + "_" + task.getName().replaceAll("[\\s:]+", "_"));
				logger.fine("Writing debug file: " + f);
				FileUtils.copy(fj.getOutput(), f);
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

	@Override
	public String getName() {
		return SystemProperties.SYSTEM_NAME;
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}

}
