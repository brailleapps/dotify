package org.daisy.dotify.setups.sv_SE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.formatter.utils.LayoutTools;
import org.daisy.dotify.formatter.utils.TextBorder;
import org.daisy.dotify.formatter.writers.PEFMediaWriter;
import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.setups.sv_SE.tasks.SwedishVolumeCoverPage;
import org.daisy.dotify.setups.sv_SE.tasks.VolumeCoverPageTask;
import org.daisy.dotify.system.InputManager;
import org.daisy.dotify.system.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.LayoutEngineTask;
import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.SystemResourceLocator;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.XsltTask;
import org.daisy.dotify.system.SystemResourceLocator.SystemResourceIdentifier;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
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
@SuppressWarnings("deprecation")
public class SwedishBrailleSystem implements TaskSystem {
	private final CommonResourceLocator commonResourceLocator;
	private final String outputFormat;
	private final FilterLocale context;
	private final String name;
	
	public SwedishBrailleSystem(String name, String outputFormat, FilterLocale context) {
		this.commonResourceLocator = new CommonResourceLocator();
		this.context = context;
		this.outputFormat = outputFormat;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(RunParameters p) throws TaskSystemException {
		if (SystemKeys.OBFL_FORMAT.equals(outputFormat)) {
			return new ArrayList<InternalTask>();
		}
		
		//URL brailleFinalizer;
		URL metaFinalizer;

		try {
			//brailleFinalizer = commonResourceLocator.getResource("xslt/braille-finalizer.xsl");
			metaFinalizer = commonResourceLocator.getResource("xslt/meta-finalizer.xsl");
		} catch (ResourceLocatorException e) {
			throw new TaskSystemException("Could not locate resource.", e);
		}
		//configURL = new URL(resourceBase, config);
		Properties p2 = new Properties();
		HashMap<String, Object> h = new HashMap<String, Object>();
		for (Object key : p.getKeys()) {
			p2.put(key, p.getProperty(key));
			h.put(key.toString(), p.getProperty(key));
		}
		
		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		//InputDetector
		InputManager idts = InputManagerFactoryMaker.newInstance().newInputManager(context);
		setup.addAll(idts.compile(p));
		
		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("OBFL whitespace normalizer",
								SystemResourceLocator.getInstance().getResourceByIdentifier(SystemResourceIdentifier.OBFL_WHITESPACE_NORMALIZER_XSLT), 
								null,
								h));

		// Layout FLOW as PEF
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		//BrailleFilterFactory factory = BrailleFilterFactory.newInstance();

		// Customize which parameters are sent to the PEFMediaWriter, as it outputs all parameters for future reference
		// Localization parameters are not that interesting in retrospect
		p2.remove("l10nrearjacketcopy");
		p2.remove("l10nimagedescription");
		p2.remove("l10ncolophon");
		p2.remove("l10ncaption");
		// System file paths should be concealed for security reasons 
		p2.remove(SystemKeys.INPUT);
		p2.remove(SystemKeys.INPUT_URI);
		p2.remove("output");
		p2.remove(SystemKeys.TEMP_FILES_DIRECTORY);

		//StringFilter swedishFilter = new SwedishBrailleFilter(sv_SE);
		
		BrailleTranslator bt;
		try {
			bt = BrailleTranslatorFactoryMaker.newInstance().newBrailleTranslator(sv_SE, BrailleTranslatorFactory.MODE_UNCONTRACTED);
		} catch (UnsupportedSpecificationException e1) {
			throw new TaskSystemException(e1);
		}
		PEFMediaWriter paged = new PEFMediaWriter(p2);
		setup.add(new LayoutEngineTask("OBFL to PEF converter", bt, paged));

		// Split result into volumes
		//setup.add(new XsltTask("Volume splitter", volumeSplitter, null, h));

		// Add a title page first in each volume
    	TextBorder tb = new TextBorder.Builder(p.getFlowWidth()+p.getInnerMargin()).
    						topLeftCorner(LayoutTools.fill(' ', p.getInnerMargin()) + "\u280F").
    						topBorder("\u2809").
    						topRightCorner("\u2839").
    						leftBorder(LayoutTools.fill(' ', p.getInnerMargin()) + "\u2807  ").
    						rightBorder("  \u2838").
    						bottomLeftCorner(LayoutTools.fill(' ', p.getInnerMargin()) + "\u2827").
    						bottomBorder("\u2824").
    						bottomRightCorner("\u283c").
    						alignment(TextBorder.Align.CENTER).
    						build();
    	SwedishVolumeCoverPage cover;
		try {
			cover = new SwedishVolumeCoverPage(new File(p.getProperty(SystemKeys.INPUT)), tb, bt, p.getPageHeight());
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
		//HashMap<String, Object> finalizerOptions = new HashMap<String, Object>();
		//finalizerOptions.put("finalizer-input", " \u00a0-\u00ad");
		//finalizerOptions.put("finalizer-output", "\u2800\u2800\u2824\u2824");
		//setup.add(new XsltTask("Braille finalizer", brailleFinalizer, null, finalizerOptions));

		// Finalize meta data from input file
		setup.add(new XsltTask("Meta data finalizer", metaFinalizer, null, h));

		return setup;
	}

}