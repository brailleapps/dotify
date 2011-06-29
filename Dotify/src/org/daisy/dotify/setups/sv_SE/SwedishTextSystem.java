package org.daisy.dotify.setups.sv_SE;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.daisy.dotify.formatter.writers.TextMediaWriter;
import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.LayoutEngineTask;
import org.daisy.dotify.system.ResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.ValidatorTask;
import org.daisy.dotify.system.XsltTask;
import org.daisy.dotify.text.FilterFactory;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.RegexFilter;
import org.daisy.dotify.text.StringFilter;


/**
 * <p>Transforms a DTBook 2005-3 into text format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * @author Joel Håkansson, TPB
 *
 */
public class SwedishTextSystem implements TaskSystem {
	private final String name;
	private final ResourceLocator commonResourceLocator;
	
	public SwedishTextSystem(String name) {
		this.commonResourceLocator = new CommonResourceLocator();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(RunParameters p) throws TaskSystemException {
		URL flowValidationURL;
		URL flowWsNormalizer;

		try {
			flowValidationURL = commonResourceLocator.getResource("validation/flow.xsd");
			flowWsNormalizer = commonResourceLocator.getResource("preprocessing/flow-whitespace-normalizer.xsl");
		} catch (ResourceLocatorException e) {
			throw new TaskSystemException("Failed to locate resource.", e);
		}

		HashMap h = new HashMap();
		h.putAll(p.getProperties());

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();

		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("FLOW whitespace normalizer", flowWsNormalizer, null, h));

		// Check that the result from the previous step is OK
		setup.add(new ValidatorTask("FLOW validator", flowValidationURL));

		// Layout FLOW as text
		MyFilterFactory factory = new MyFilterFactory();
		TextMediaWriter paged = new TextMediaWriter(p.getProperties(), "UTF-8");
		setup.add(new LayoutEngineTask("FLOW to Text converter", factory, FilterLocale.parse("sv-SE"), paged));

		return setup;
	}
	
	private static class MyFilterFactory implements FilterFactory {
		public StringFilter newStringFilter(FilterLocale target) {
			// remove zero width space
			return new RegexFilter("\\u200B", "");
		}
	}

}