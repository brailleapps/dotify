package org.daisy.dotify.cli;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.daisy.braille.api.factory.Factory;
import org.daisy.braille.api.factory.FactoryCatalog;
import org.daisy.braille.api.factory.FactoryProperties;
import org.daisy.braille.api.table.BrailleConverter;
import org.daisy.braille.consumer.table.TableCatalog;
import org.daisy.cli.AbstractUI;
import org.daisy.cli.Argument;
import org.daisy.cli.CommandParserResult;
import org.daisy.cli.Definition;
import org.daisy.cli.ExitCode;
import org.daisy.cli.OptionalArgument;
import org.daisy.cli.ShortFormResolver;
import org.daisy.cli.SwitchArgument;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.api.translator.TranslatorSpecification;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;

public class TranslateCLI extends AbstractUI {
	private final static String DEFAULT_LOCALE = Locale.getDefault().toString().replaceAll("_", "-");
	private final static String META_KEY = "meta";
	private final static String LOCALE_KEY = "locale";
	private final static String TABLE_KEY = "table";
	private final static String HELP_KEY = "help";
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final ShortFormResolver tableSF;
	
	public TranslateCLI() {
		this.reqArgs = new ArrayList<Argument>();
		TableCatalog tableCatalog = TableCatalog.newInstance();
		Collection<String> idents = new ArrayList<String>();
		for (FactoryProperties p : tableCatalog.list()) { idents.add(p.getIdentifier()); }
		tableSF = new ShortFormResolver(idents);
		Collection<TranslatorSpecification> tr = BrailleTranslatorFactoryMaker.newInstance().listSpecifications();
		ArrayList<Definition> translations = new ArrayList<Definition>();
		for (TranslatorSpecification ts : tr) {
			if (!BrailleTranslatorFactory.MODE_BYPASS.equals(ts.getMode())) {
				translations.add(new Definition(ts.getLocale(), ""));
			}
		}
		this.optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(LOCALE_KEY, "Braille locale. Note that the default locale is based on system settings, not on available braille locales.", translations, DEFAULT_LOCALE));
		optionalArgs.add(new OptionalArgument(TABLE_KEY, "Table to use", getDefinitionList(tableCatalog, tableSF), "unicode_braille"));
		parser.addSwitch(new SwitchArgument('h', HELP_KEY, META_KEY, HELP_KEY, "Help text."));
	}
	
	public static void main(String[] args) throws IOException {
		TranslateCLI m = new TranslateCLI();
		CommandParserResult result = m.parser.parse(args);
		if (HELP_KEY.equals(result.getOptional().get(META_KEY))) {
			m.displayHelp(System.out);
			TranslateCLI.exitWithCode(ExitCode.OK);
		} else {
			m.runCLI(result);
		}
	}
	
	private void runCLI(CommandParserResult cmd) throws IOException {
		try {
			String locale = cmd.getOptional().get(LOCALE_KEY);
			if (locale==null || "".equals(locale)) {
				locale = DEFAULT_LOCALE;
			}
			BrailleTranslator t = BrailleTranslatorFactoryMaker.newInstance().newTranslator(locale, BrailleTranslatorFactory.MODE_UNCONTRACTED);
			TableCatalog tc = TableCatalog.newInstance();
			String table = cmd.getOptional().get(TABLE_KEY);
			BrailleConverter bc = null;
			if (table!=null && !"".equals(table)) {
				bc = tc.newTable(tableSF.resolve(table)).newBrailleConverter();
			}
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
			String text;
			while ((text=lnr.readLine())!=null) {
				if (bc!=null) {
					System.out.println(bc.toText(t.translate(text).getTranslatedRemainder()));
				} else {
					System.out.println(t.translate(text).getTranslatedRemainder());
				}
			}
		} catch (TranslatorConfigurationException e) {
			System.err.println("Cannot find a translator.");
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "Translates text on system in to braille on system out.";
	}

	@Override
	public String getName() {
		return "translate";
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	
	/**
	 * Creates a list of definitions based on the contents of the supplied FactoryCatalog.
	 * @param catalog the catalog to create definitions for
	 * @param resolver 
	 * @return returns a list of definitions
	 */
	List<Definition> getDefinitionList(FactoryCatalog<? extends Factory, ? extends FactoryProperties> catalog, ShortFormResolver resolver) {
		List<Definition> ret = new ArrayList<Definition>();
		for (String key : resolver.getShortForms()) {
			ret.add(new Definition(key, catalog.get(resolver.resolve(key)).getDescription()));
		}
		return ret;
	}
}
