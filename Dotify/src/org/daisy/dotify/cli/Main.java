package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.braille.api.embosser.EmbosserFactoryException;
import org.daisy.braille.api.factory.Factory;
import org.daisy.braille.api.factory.FactoryCatalog;
import org.daisy.braille.api.factory.FactoryProperties;
import org.daisy.braille.consumer.embosser.EmbosserCatalog;
import org.daisy.braille.consumer.table.TableCatalog;
import org.daisy.braille.pef.PEFConverterFacade;
import org.daisy.braille.pef.PEFValidator;
import org.daisy.braille.pef.UnsupportedWidthException;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.CommandParserResult;
import org.daisy.cli.Definition;
import org.daisy.cli.ExitCode;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.ShortFormResolver;
import org.daisy.cli.SwitchArgument;
import org.daisy.dotify.Dotify;
import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.SystemProperties;
import org.daisy.dotify.api.tasks.InternalTaskException;
import org.daisy.dotify.api.tasks.TaskSystemFactoryException;
import org.daisy.dotify.api.translator.TranslatorSpecification;
import org.daisy.dotify.common.text.FilterLocale;
import org.daisy.dotify.config.ConfigurationsCatalog;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.tasks.runner.DefaultTempFileWriter;
import org.xml.sax.SAXException;

/**
 * Provides a command line entry point to Dotify.
 * @author Joel Håkansson
 */
public class Main extends AbstractUI {
	//private final static String DEFAULT_TEMPLATE = "A4-w32";
	private final static String DEFAULT_LOCALE = Locale.getDefault().toString().replaceAll("_", "-");
	private final static String META_KEY = "meta";
	private final static String VERSION_KEY = "version";
	private final static String CONFIG_KEY = "configs";

	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;

	private final ShortFormResolver tableSF;

	public Main() {

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
		/*
		{
			ArrayList<Definition> vals = new ArrayList<Definition>();
			InputManagerFactoryMaker m = InputManagerFactoryMaker.newInstance();
			for (String o : m.listSupportedLocales()) {
				vals.add(new Definition(o, "A context locale"));
			}
			reqArgs.add(new Argument("locale", "The target locale for the result", vals));
		}*/
		
		this.optionalArgs = new ArrayList<OptionalArgument>();
		
		optionalArgs.add(new OptionalArgument("locale", "The target locale for the result", DEFAULT_LOCALE));
		
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
		optionalArgs.add(new OptionalArgument(SystemKeys.TEMP_FILES_DIRECTORY, "Path to temp files directory", DefaultTempFileWriter.TEMP_DIR));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE, "Sets date in meta data (if available)", Dotify.getDefaultDate(SystemProperties.DEFAULT_DATE_FORMAT)));
		optionalArgs.add(new OptionalArgument(SystemKeys.DATE_FORMAT, "Date format in meta data (if available and date is not specified)", SystemProperties.DEFAULT_DATE_FORMAT));
		TableCatalog tableCatalog = TableCatalog.newInstance();
		Collection<String> idents = new ArrayList<String>();
		for (FactoryProperties p : tableCatalog.list()) { idents.add(p.getIdentifier()); }
		tableSF = new ShortFormResolver(idents);
		optionalArgs.add(new OptionalArgument(PEFConverterFacade.KEY_TABLE, "If specified, an ASCII-braille file (.brl) is generated in addition to the PEF-file using the specified braille code table", getDefinitionList(tableCatalog, tableSF), ""));
		parser.addSwitch(new SwitchArgument('v', VERSION_KEY, META_KEY, VERSION_KEY, "Displays the version of Dotify."));
		parser.addSwitch(new SwitchArgument('c', CONFIG_KEY, META_KEY, CONFIG_KEY, "Lists known configurations."));
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
		CommandParserResult result = m.parser.parse(args);
		if (args.length<3) {
			if (VERSION_KEY.equals(result.getOptional().get(META_KEY))) {
				System.out.println("About " + SystemProperties.SYSTEM_NAME);
				System.out.println("Version: "+ SystemProperties.SYSTEM_RELEASE);
				System.out.println("Build: "+ SystemProperties.SYSTEM_BUILD);
				Main.exitWithCode(ExitCode.OK);
			} else if (CONFIG_KEY.equals(result.getOptional().get(META_KEY))) {
				ArrayList<TranslatorSpecification> s = new ArrayList<TranslatorSpecification>();
				s.addAll(BrailleTranslatorFactoryMaker.newInstance().listSpecifications());
				Collections.sort(s);
				System.out.println("Known configurations (locale, braille mode):");
				for (TranslatorSpecification ts : s) {
					System.out.println("  " + ts.getLocale() + ", " + ts.getMode());
				}
				Main.exitWithCode(ExitCode.OK);
			} else {
				System.out.println("Expected at least three arguments");
				
				System.out.println();
				m.displayHelp(System.out);
				Main.exitWithCode(ExitCode.MISSING_ARGUMENT);
			}
		}

		List<String> p = result.getRequired();
		// remove required arguments
		File input = new File(p.get(0));
		//File input = new File(args[0]);
		if (!input.exists()) {
			System.out.println("Cannot find input file: " + input);
			Main.exitWithCode(ExitCode.MISSING_RESOURCE);
		}
		
		final File output = new File(p.get(1)).getAbsoluteFile();

		final String setup = p.get(2);
		final String context;
		{
			String s = result.getOptional().get("locale");
			if (s==null || s.equals("")) {
				s = DEFAULT_LOCALE;
			}
			context = s;
		}

		//File output = new File(args[1]);
		final HashMap<String, String> props = new HashMap<String, String>();
		//props.put("debug", "true");
		//props.put(SystemKeys.TEMP_FILES_DIRECTORY, TEMP_DIR);

		props.putAll(result.getOptional());
		
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
						new PEFConverterFacade(EmbosserCatalog.newInstance()).parsePefFile(output, os, null, p);
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
		return "convert";
	}
	
	@Override
	public String getDescription() {
		return "Converts documents into braille.";
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}
	
	
	/**
	 * Creates a list of definitions based on the contents of the supplied FactoryCatalog.
	 * @param catalog the catalog to create definitions for
	 * @param resolver 
	 * @return returns a list of definitions
	 */
	List<Definition> getDefinitionList(FactoryCatalog<? extends Factory> catalog, ShortFormResolver resolver) {
		List<Definition> ret = new ArrayList<Definition>();
		for (String key : resolver.getShortForms()) {
			ret.add(new Definition(key, catalog.get(resolver.resolve(key)).getDescription()));
		}
		return ret;
	}

}
