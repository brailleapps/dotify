package org.daisy.dotify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.daisy.braille.ui.AbstractUI;
import org.daisy.dotify.setups.ConfigUrlLocator;
import org.daisy.dotify.system.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a command line entry point to Dotify.
 * @author Joel Håkansson
 */
public class Main extends AbstractUI {

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;

	private Main() {

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
			//vals.add(new Definition(SystemKeys.OBFL_FORMAT, "write result in OBFL-format (bypass formatter)"));
			optionalArgs.add(new OptionalArgument(SystemKeys.OUTPUT_FORMAT, "Specifies output format", vals, "[detect]"));
		}
		optionalArgs.add(new OptionalArgument(SystemKeys.IDENTIFIER, "Sets identifier in meta data (if available)", "[generated value]"));
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			vals.add(new Definition("true", "outputs temp files"));
			vals.add(new Definition("false", "does not output temp files"));
			optionalArgs.add(new OptionalArgument(SystemKeys.WRITE_TEMP_FILES, "Writes temp files", vals, "true"));
		}
		optionalArgs.add(new OptionalArgument(SystemKeys.TEMP_FILES_DIRECTORY, "Path to temp files directory", Dotify.TEMP_DIR));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE, "Sets date in meta data (if available)", Dotify.getDefaultDate(SystemProperties.DEFAULT_DATE_FORMAT)));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE_FORMAT, "Date format in meta data (if available and date is not specified)", SystemProperties.DEFAULT_DATE_FORMAT));
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
		}

		List<String> p = m.getRequired(args);
		// remove required arguments
		File input = new File(p.get(0));
		//File input = new File(args[0]);
		if (!input.exists()) {
			System.out.println("Cannot find input file: " + input);
			Main.exitWithCode(ExitCode.MISSING_RESOURCE);
		}
		
		File output = new File(p.get(1)).getAbsoluteFile();

		String setup = p.get(2);
		String context = p.get(3);
		
		//File output = new File(args[1]);
		HashMap<String, String> props = new HashMap<String, String>();
		//props.put("debug", "true");
		//props.put(SystemKeys.TEMP_FILES_DIRECTORY, TEMP_DIR);
		props.put(SystemKeys.WRITE_TEMP_FILES, "true");

		props.putAll(m.getOptional(args));
		
		Dotify.run(input, output, setup, FilterLocale.parse(context), props);
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
