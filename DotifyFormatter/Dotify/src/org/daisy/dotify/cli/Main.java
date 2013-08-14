package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.braille.embosser.EmbosserFactoryException;
import org.daisy.braille.embosser.UnsupportedWidthException;
import org.daisy.braille.facade.PEFConverterFacade;
import org.daisy.braille.pef.PEFValidator;
import org.daisy.braille.table.TableCatalog;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.ShortFormResolver;
import org.daisy.dotify.Dotify;
import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.SystemProperties;
import org.daisy.dotify.config.ConfigurationsCatalog;
import org.daisy.dotify.input.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.TaskRunner;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.text.FilterLocale;
import org.xml.sax.SAXException;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;*/

/**
 * Provides a command line entry point to Dotify.
 * @author Joel Håkansson
 */
public class Main extends AbstractUI {

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;

	private final ShortFormResolver tableSF;

	private Main() {

		this.reqArgs = new ArrayList<Argument>();
		reqArgs.add(new Argument("path_to_input", "Path to the input file"));
		reqArgs.add(new Argument("path_to_output", "Path to the output file"));
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			ConfigurationsCatalog c = ConfigurationsCatalog.newInstance();
			for (String o : c.getKeys()) {
				vals.add(new Definition(o, c.getConfigurationDescription(o)));
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
			optionalArgs.add(new OptionalArgument(SystemKeys.WRITE_TEMP_FILES, "Writes temp files", vals, "false"));
		}
		optionalArgs.add(new OptionalArgument(SystemKeys.TEMP_FILES_DIRECTORY, "Path to temp files directory", TaskRunner.TEMP_DIR));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE, "Sets date in meta data (if available)", Dotify.getDefaultDate(SystemProperties.DEFAULT_DATE_FORMAT)));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE_FORMAT, "Date format in meta data (if available and date is not specified)", SystemProperties.DEFAULT_DATE_FORMAT));
		TableCatalog tableCatalog = TableCatalog.newInstance();
		tableSF = new ShortFormResolver(tableCatalog.list());
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_TABLE, "If specified, an ASCII-braille file (.brl) is generated in addition to the PEF-file using the specified braille code table", getDefinitionList(tableCatalog, tableSF), ""));
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
			if (args.length==1 && args[0].equals("-version")) {
				System.out.println("About " + SystemProperties.SYSTEM_NAME);
				System.out.println("Version: "+ SystemProperties.SYSTEM_RELEASE);
				System.out.println("Build: "+ SystemProperties.SYSTEM_BUILD);
				Main.exitWithCode(ExitCode.OK);
			} else {
				System.out.println("Expected at least four arguments");
				
				System.out.println();
				m.displayHelp(System.out);
				Main.exitWithCode(ExitCode.MISSING_ARGUMENT);
			}
		}

		List<String> p = m.getRequired(args);
		// remove required arguments
		File input = new File(p.get(0));
		//File input = new File(args[0]);
		if (!input.exists()) {
			System.out.println("Cannot find input file: " + input);
			Main.exitWithCode(ExitCode.MISSING_RESOURCE);
		}
		
		final File output = new File(p.get(1)).getAbsoluteFile();

		final String setup = p.get(2);
		final String context = p.get(3);
		
		//File output = new File(args[1]);
		final HashMap<String, String> props = new HashMap<String, String>();
		//props.put("debug", "true");
		//props.put(SystemKeys.TEMP_FILES_DIRECTORY, TEMP_DIR);

		props.putAll(m.getOptional(args));
		
		if (input.isDirectory() && output.isDirectory()) {
			if ("true".equals(props.get(SystemKeys.WRITE_TEMP_FILES))) {
				Main.exitWithCode(ExitCode.ILLEGAL_ARGUMENT_VALUE, "Cannot write debug files in batch mode.");
			}
			String format = props.get(SystemKeys.OUTPUT_FORMAT);
			if (format==null) {
				Main.exitWithCode(ExitCode.MISSING_ARGUMENT, SystemKeys.OUTPUT_FORMAT + " must be specified in batch mode.");
			} else if (format.equals(SystemKeys.PEF_FORMAT)) {
				format = "pef";
			} else if (format.equals(SystemKeys.TEXT_FORMAT)) {
				format = "txt";
			} else if (format.equals(SystemKeys.OBFL_FORMAT)) {
				format = "obfl";
			} else {
				Main.exitWithCode(ExitCode.ILLEGAL_ARGUMENT_VALUE, "Unknown output format.");
			}
			//Experimental parallelization code in comment.
			//ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			final String ext = format;
			for (final File f : input.listFiles()) {
				//es.execute(new Runnable() {
					//public void run() {
						try {
					m.runDotify(f, new File(output, f.getName() + "." + ext), setup, context, props);
						} catch (InternalTaskException e) {
							Logger.getLogger(Main.class.getCanonicalName()).log(Level.WARNING, "Failed to process " + f, e);
						} catch (IOException e) {
							Logger.getLogger(Main.class.getCanonicalName()).log(Level.WARNING, "Failed to read " + f, e);
						}
					//}});
			}
			//es.shutdown();
			//try {
			//	es.awaitTermination(600, TimeUnit.SECONDS);
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//}
		} else if (input.isDirectory()) { 
			Main.exitWithCode(ExitCode.ILLEGAL_ARGUMENT_VALUE, "If input is a directory, output must be an existing directory too.");
		} else {
			m.runDotify(input, output, setup, context, props);
		}
	}
	
	private void runDotify(File input, File output, String setup, String context, HashMap<String, String> props) throws InternalTaskException, IOException {
		if (!input.exists()) {
			Main.exitWithCode(ExitCode.MISSING_RESOURCE, "Cannot find input file: " + input);
		}
		Dotify.run(input, output, setup, FilterLocale.parse(context), props);
		int i = output.getName().lastIndexOf(".");
		String format = "";
		if (output.getName().length()>i) {
			format = output.getName().substring(i+1);
		}
		if (format.equalsIgnoreCase(SystemKeys.PEF_FORMAT)) {
			Logger logger = Logger.getLogger(Main.class.getCanonicalName());
			logger.info("Validating output...");
			PEFValidator validator = new PEFValidator();
			if (!validator.validate(output.toURI().toURL())) {
				logger.warning("Validation failed: " + output);
			} else {
				logger.info("Output is valid.");
				if (props.containsKey(PEFConverterFacade.KEY_TABLE)) {
					// create brl
					HashMap<String, String> p = new HashMap<String, String>();
					p.put(PEFConverterFacade.KEY_TABLE, props.get(PEFConverterFacade.KEY_TABLE));
					expandShortForm(p, PEFConverterFacade.KEY_TABLE, tableSF);
					File f = new File(output.getParentFile(), output.getName() + ".brl");
					logger.info("Writing brl to " + f.getAbsolutePath());
					FileOutputStream os = null;
					try {
						os = new FileOutputStream(f);
						PEFConverterFacade.parsePefFile(output, os, null, p);
					} catch (ParserConfigurationException e) {
						logger.log(Level.FINE, "Parse error when converting to brl", e);
					} catch (SAXException e) {
						logger.log(Level.FINE, "SAX error when converting to brl", e);
					} catch (UnsupportedWidthException e) {
						logger.log(Level.FINE, "Width error when converting to brl", e);
					} catch (NumberFormatException e) {
						logger.log(Level.FINE, "Number format error when converting to brl", e);
					} catch (EmbosserFactoryException e) {
						logger.log(Level.FINE, "Embosser error when converting to brl", e);
					} finally {
						if (os != null) {
							os.close();
						}
					}
				}
			}
		}
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
