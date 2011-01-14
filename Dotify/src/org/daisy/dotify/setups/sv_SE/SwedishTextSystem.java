package org.daisy.dotify.setups.sv_SE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import org.daisy.dotify.setups.ResourceLocator;
import org.daisy.dotify.setups.ResourceLocatorException;
import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.setups.common.InputDetectorTaskSystem;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.tasks.LayoutEngineTask;
import org.daisy.dotify.system.tasks.ValidatorTask;
import org.daisy.dotify.system.tasks.XsltTask;
import org.daisy.dotify.system.tasks.layout.impl.DefaultLayoutPerformer;
import org.daisy.dotify.system.tasks.layout.impl.PaginatorImpl;
import org.daisy.dotify.system.tasks.layout.text.RegexFilter;
import org.daisy.dotify.system.tasks.layout.text.brailleFilters.BrailleFilterFactory;
import org.daisy.dotify.system.tasks.layout.writers.TextMediaWriter;


/**
 * <p>Transforms a DTBook 2005-3 into text format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * @author Joel Håkansson, TPB
 *
 */
public class SwedishTextSystem implements TaskSystem {
	//private URL resourceBase;
	private final URL configURL;
	private final InputDetectorTaskSystem inputDetector;
	private final String name;
	private final ResourceLocator commonResourceLocator;
	
	public SwedishTextSystem(ResourceLocator resourceBase, URL configURL, String name) {
		//this.resourceBase = resourceBase;
		this.commonResourceLocator = new CommonResourceLocator();
		this.configURL = configURL;
		this.inputDetector = new InputDetectorTaskSystem(resourceBase, "sv_SE/config/", "common/config/");
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(Map<String, String> parameters) throws TaskSystemException {
		URL flowValidationURL;
		URL flowWsNormalizer;

		try {
			flowValidationURL = commonResourceLocator.getResource("validation/flow.xsd");
			flowWsNormalizer = commonResourceLocator.getResource("preprocessing/flow-whitespace-normalizer.xsl");
		} catch (ResourceLocatorException e) {
			throw new TaskSystemException("Failed to locate resource.", e);
		}

		Properties p = new Properties();
		p.put("l10nrearjacketcopy", "Baksidestext");
		p.put("l10imagedescription", "Bildbeskrivning");
		p.put("l10ncolophon", "Kolofon");
		p.put("l10ncaption", "Bildtext");

		try {
			p.loadFromXML(configURL.openStream());
		} catch (FileNotFoundException e) {
			throw new TaskSystemException("Configuration file not found: " + configURL, e);
		} catch (InvalidPropertiesFormatException e) {
			throw new TaskSystemException("Configuration file could not be parsed: " + configURL, e);
		} catch (IOException e) {
			throw new TaskSystemException("IOException while reading configuration file: " + configURL, e);
		}
		// GUI parameters should take precedence
		p.putAll(parameters);

		int flowWidth = Integer.parseInt(p.getProperty("cols", "28"));
		int pageHeight = Integer.parseInt(p.getProperty("rows", "29"));
		int innerMargin = Integer.parseInt(p.getProperty("inner-margin", "5"));
		int outerMargin = Integer.parseInt(p.getProperty("outer-margin", "2"));
		float rowgap = Float.parseFloat(p.getProperty("rowgap", "0"));

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();

		p.put("page-height", pageHeight);
		p.put("page-width", flowWidth+innerMargin+outerMargin);
		p.put("row-spacing", (rowgap/4)+1);

		HashMap h = new HashMap();
		h.putAll(p);
		
		setup.addAll(inputDetector.compile(h));

		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("FLOW whitespace normalizer", flowWsNormalizer, null, h));

		// Check that the result from the previous step is OK
		setup.add(new ValidatorTask("FLOW validator", flowValidationURL));

		// Layout FLOW as text
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		factory.setDefault(new RegexFilter("\\u200B", ""));
		TextMediaWriter paged = new TextMediaWriter(p, "UTF-8");
		PaginatorImpl paginator = new PaginatorImpl(factory.getDefault());
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer(factory);
		setup.add(new LayoutEngineTask("FLOW to Text converter", flow, paginator, paged));

		return setup;
	}

}