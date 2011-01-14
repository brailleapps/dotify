package org.daisy.dotify.setups.sv_SE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.daisy.dotify.setups.ResourceLocator;
import org.daisy.dotify.setups.ResourceLocatorException;
import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.setups.common.InputDetectorTaskSystem;
import org.daisy.dotify.setups.sv_SE.tasks.SwedishVolumeCoverPage;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.tasks.LayoutEngineTask;
import org.daisy.dotify.system.tasks.ValidatorTask;
import org.daisy.dotify.system.tasks.VolumeCoverPageTask;
import org.daisy.dotify.system.tasks.XsltTask;
import org.daisy.dotify.system.tasks.layout.impl.DefaultLayoutPerformer;
import org.daisy.dotify.system.tasks.layout.impl.PaginatorImpl;
import org.daisy.dotify.system.tasks.layout.text.FilterLocale;
import org.daisy.dotify.system.tasks.layout.text.brailleFilters.BrailleFilterFactory;
import org.daisy.dotify.system.tasks.layout.utils.LayoutTools;
import org.daisy.dotify.system.tasks.layout.utils.TextBorder;
import org.daisy.dotify.system.tasks.layout.writers.PEFMediaWriter;
import org.xml.sax.SAXException;


/**
 * <p>Transforms a DTBook 2005-3 into Swedish braille in PEF 2008-1 format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * 
 * <p>This TaskSystem consists of the following steps:</p>
 * <ol>
	 * <li>Conformance checker.
	 * 		Checks that the input meets some basic requirements.</li>
	 * <li>DTBook to FLOW converter.
	 * 		Inserts braille characters preceding
	 * 		numbers and capital letters, inserts braille markers where inline structural
	 * 		markup such as <tt>em</tt> and <tt>strong</tt> occur and converts the DTBook
	 * 		structure into a flow definition, similar to XSL-FO.</li>
	 * <li>FLOW whitespace normalizer</li>
	 * <li>FLOW to PEF converter.
	 * 		Translates all characters into braille, and puts the text flow onto pages.</li>
	 * <li>Volume splitter.
	 * 		The output from the preceding step is a single volume that is split into volumes.</li>
	 * <li>Cover page adder</li>
	 * <li>Braille finalizer.
	 * 		Replaces any remaining non-braille characters, e.g. spaces and hyphens, with
	 * 		braille characters.</li>
	 * <li>Meta data finalizer</li>

 * </ol>
 * <p>The result should be validated against the PEF Relax NG schema using int_daisy_validator.</p>
 * @author Joel Håkansson, TPB
 */
public class SwedishBrailleSystem implements TaskSystem {
	//private URL resourceBase;
	private final ResourceLocator commonResourceLocator;
	//private String config;
	private final URL configURL;
	private final InputDetectorTaskSystem inputDetector;
	private final String name;
	
	public SwedishBrailleSystem(ResourceLocator resourceBase, URL configURL, String name) {
		//this.resourceBase = resourceBase;
		this.commonResourceLocator = new CommonResourceLocator();
		//this.config = config;
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
		URL volumeSplitter;
		URL brailleFinalizer;
		URL metaFinalizer;

		try {
			flowValidationURL = commonResourceLocator.getResource("validation/flow.xsd");
			flowWsNormalizer = commonResourceLocator.getResource("preprocessing/flow-whitespace-normalizer.xsl");
			volumeSplitter = commonResourceLocator.getResource("splitters/simple-splitter.xsl");
			brailleFinalizer = commonResourceLocator.getResource("renderers/braille-finalizer.xsl");
			metaFinalizer = commonResourceLocator.getResource("renderers/meta-finalizer.xsl");
		} catch (ResourceLocatorException e) {
			throw new TaskSystemException("Could not locate resource.", e);
		}
		//configURL = new URL(resourceBase, config);

		Properties p = new Properties();
		p.put("l10nrearjacketcopy", "Baksidestext");
		p.put("l10imagedescription", "Bildbeskrivning");
		p.put("l10ncolophon", "Tryckuppgifter");
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

		// Layout FLOW as PEF
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();

		// Customize which parameters are sent to the PEFMediaWriter, as it outputs all parameters for future reference
		Properties p2 = new Properties();
		p2.putAll(p);
		// Localization parameters are not that interesting in retrospect
		p2.remove("l10nrearjacketcopy");
		p2.remove("l10imagedescription");
		p2.remove("l10ncolophon");
		p2.remove("l10ncaption");
		// System file paths should be concealed for security reasons 
		p2.remove("input");
		p2.remove("input-uri");
		p2.remove("output");
		p2.remove("tempFilesDirectory");

		PEFMediaWriter paged = new PEFMediaWriter(p2);
		factory.setDefault(sv_SE);
		PaginatorImpl paginator = new PaginatorImpl(factory.getDefault());
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer(factory);
		setup.add(new LayoutEngineTask("FLOW to PEF converter", flow, paginator, paged));

		// Split result into volumes
		setup.add(new XsltTask("Volume splitter", volumeSplitter, null, h));

		// Add a title page first in each volume
    	TextBorder tb = new TextBorder.Builder(flowWidth+innerMargin).
    						topLeftCorner(LayoutTools.fill(' ', innerMargin) + "\u280F").
    						topBorder("\u2809").
    						topRightCorner("\u2839").
    						leftBorder(LayoutTools.fill(' ', innerMargin) + "\u2807  ").
    						rightBorder("  \u2838").
    						bottomLeftCorner(LayoutTools.fill(' ', innerMargin) + "\u2827").
    						bottomBorder("\u2824").
    						bottomRightCorner("\u283c").
    						alignment(TextBorder.Align.CENTER).
    						build();
    	SwedishVolumeCoverPage cover;
		try {
			cover = new SwedishVolumeCoverPage(new File(parameters.get("input")), tb, factory.getDefault(), pageHeight);
		} catch (XPathExpressionException e) {
			throw new TaskSystemException(e);
		} catch (ParserConfigurationException e) {
			throw new TaskSystemException(e);
		} catch (SAXException e) {
			throw new TaskSystemException(e);
		} catch (IOException e) {
			throw new TaskSystemException(e);
		}
		setup.add(new VolumeCoverPageTask("Cover page adder", cover));

		// Finalizes character data on rows
		HashMap<String, Object> finalizerOptions = new HashMap<String, Object>();
		finalizerOptions.put("finalizer-input", " \u00a0-\u00ad");
		finalizerOptions.put("finalizer-output", "\u2800\u2800\u2824\u2824");
		setup.add(new XsltTask("Braille finalizer", brailleFinalizer, null, finalizerOptions));

		// Finalize meta data from input file
		setup.add(new XsltTask("Meta data finalizer", metaFinalizer, null, h));

		return setup;
	}

}