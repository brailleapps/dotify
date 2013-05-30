package org.daisy.dotify.impl.system.sv_SE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.impl.system.common.CommonResourceLocator;
import org.daisy.dotify.impl.system.common.CommonResourceLocator.CommonResourceIdentifier;
import org.daisy.dotify.input.InputManager;
import org.daisy.dotify.input.InputManagerFactoryMaker;
import org.daisy.dotify.obfl.ObflResourceLocator;
import org.daisy.dotify.obfl.ObflResourceLocator.ObflResourceIdentifier;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.LayoutEngineTask;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.XsltTask;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.StringTools;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.daisy.dotify.writer.PEFMediaWriter;
import org.xml.sax.SAXException;


/**
 * <p>Transforms XML into Swedish braille in PEF 2008-1 format.</p>
 * 
 * <p>This TaskSystem consists of the following steps:</p>
 * <ol>
	 * <li>Input Manager. Validates and converts input to OBFL.</li>
	 * <li>Whitespace normalizer. Normalizes OBFL whitespace.</li>
	 * <li>OBFL to PEF converter.
	 * 		Translates all characters into braille, and puts the text flow onto pages.</li>
	 * <li>Cover page adder</li>
	 * <li>Meta data finalizer</li>

 * </ol>
 * <p>The result should be validated against the PEF Relax NG schema using int_daisy_validator.</p>
 * @author Joel Håkansson, TPB
 */
@SuppressWarnings("deprecation")
public class SwedishBrailleSystem implements TaskSystem {
	private final String outputFormat;
	private final FilterLocale context;
	private final String name;
	
	public SwedishBrailleSystem(String name, String outputFormat, FilterLocale context) {
		this.context = context;
		this.outputFormat = outputFormat;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(Map<String, Object> pa) throws TaskSystemException {
		//configURL = new URL(resourceBase, config);
		
		RunParameters p;
		{
			Properties p1 = new Properties();
			for (String key : pa.keySet()) {
				p1.put(key, pa.get(key));
			}
			p = new RunParameters(p1);
		}
		Properties p2 = new Properties();
		HashMap<String, Object> h = new HashMap<String, Object>();
		for (Object key : p.getKeys()) {
			p2.put(key, p.getProperty(key));
			h.put(key.toString(), p.getProperty(key));
		}
		
		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		//InputDetector
		InputManager idts = InputManagerFactoryMaker.newInstance().newInputManager(context, p2.get(SystemKeys.INPUT_FORMAT).toString());
		setup.addAll(idts.compile(h));
		
		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("OBFL whitespace normalizer",
								ObflResourceLocator.getInstance().getResourceByIdentifier(ObflResourceIdentifier.OBFL_WHITESPACE_NORMALIZER_XSLT), 
								null,
								h));

		if (SystemKeys.OBFL_FORMAT.equals(outputFormat)) {
			return setup;
		}
		
		// Layout FLOW as PEF
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		//BrailleFilterFactory factory = BrailleFilterFactory.newInstance();

		// Customize which parameters are sent to the PEFMediaWriter, as it outputs all parameters for future reference
		// System file paths should be concealed for security reasons 
		p2.remove(SystemKeys.INPUT);
		p2.remove(SystemKeys.INPUT_URI);
		p2.remove("output");
		p2.remove(SystemKeys.TEMP_FILES_DIRECTORY);

		//StringFilter swedishFilter = new SwedishBrailleFilter(sv_SE);
		
		BrailleTranslator bt;
		try {
			bt = BrailleTranslatorFactoryMaker.newInstance().newTranslator(sv_SE, BrailleTranslatorFactory.MODE_UNCONTRACTED);
		} catch (UnsupportedSpecificationException e1) {
			throw new TaskSystemException(e1);
		}
		PEFMediaWriter paged = new PEFMediaWriter(p2);
		setup.add(new LayoutEngineTask("OBFL to PEF converter", bt, paged));

		// TODO: the rest is DTBook specific, the requirements should be
		// incorporated in the design instead

		String space = bt.translate(" ").getTranslatedRemainder();

		// Add a title page first in each volume
    	TextBorder tb = new TextBorder.Builder(p.getFlowWidth()+p.getInnerMargin(), space).
    						style(BrailleTextBorderStyle.SOLID_WIDE_INNER).
    						outerLeftMargin(StringTools.fill(space, p.getInnerMargin())).
    						innerLeftMargin(space+space).
    						innerRightMargin(space+space).
    						//topLeftCorner(StringTools.fill(space, p.getInnerMargin()) + "\u280F").
    						//topBorder("\u2809").
    						//topRightCorner("\u2839").
    						//leftBorder(StringTools.fill(space, p.getInnerMargin()) + "\u2807"+space+space).
    						//rightBorder(space+space+"\u2838").
    						//bottomLeftCorner(StringTools.fill(space, p.getInnerMargin()) + "\u2827").
    						//bottomBorder("\u2824").
    						//bottomRightCorner("\u283c").
    						alignment(TextBorder.Align.CENTER).
    						build();

		try {
			SwedishVolumeCoverPage cover = new SwedishVolumeCoverPage(new File(p.getProperty(SystemKeys.INPUT)), tb, bt);
			setup.add(new VolumeCoverPageTask("Cover page adder", cover));

			// Finalize meta data from input file
			URL metaFinalizer = CommonResourceLocator.getInstance().getResourceByIdentifier(CommonResourceIdentifier.META_FINALIZER_XSLT);
			setup.add(new XsltTask("Meta data finalizer", metaFinalizer, null, h));

		} catch (XPathExpressionException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Unable to add cover. Perhaps input isn't DTBook");
			// throw new TaskSystemException(e);
		} catch (ParserConfigurationException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Unable to add cover. Perhaps input isn't DTBook");
			// throw new TaskSystemException(e);
		} catch (SAXException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Unable to add cover. Perhaps input isn't DTBook");
			// throw new TaskSystemException(e);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Unable to add cover. Perhaps input isn't DTBook");
			// throw new TaskSystemException(e);
		}

		return setup;
	}

}