package org.daisy.dotify.impl.system.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.input.InputManager;
import org.daisy.dotify.input.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.LayoutEngineTask;
import org.daisy.dotify.system.OBFLWhitespaceNormalizerTask;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.writer.PEFMediaWriter;
import org.daisy.dotify.writer.PagedMediaWriter;
import org.daisy.dotify.writer.TextMediaWriter;


/**
 * <p>Transforms XML into Swedish braille in PEF 2008-1 format.</p>
 * <p>Transforms documents into text format.</p>
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
 * @author Joel Håkansson
 */
public class DotifyTaskSystem implements TaskSystem {
	private final String outputFormat;
	private final FilterLocale context;
	private final String name;
	
	public DotifyTaskSystem(String name, String outputFormat, FilterLocale context) {
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
		{
			//InputDetector
			InputManager idts = InputManagerFactoryMaker.newInstance().newInputManager(context, p2.get(SystemKeys.INPUT_FORMAT).toString());
			setup.addAll(idts.compile(h));
			
			// Whitespace normalizer TransformerFactoryConstants.SAXON8
			/*
			 * setup.add(new XsltTask("OBFL whitespace normalizer",
			 * ObflResourceLocator.getInstance().getResourceByIdentifier(
			 * ObflResourceIdentifier.OBFL_WHITESPACE_NORMALIZER_XSLT),
			 * null,
			 * h));
			 */
			setup.add(new OBFLWhitespaceNormalizerTask("OBFL whitespace normalizer"));
		}

		if (SystemKeys.OBFL_FORMAT.equals(outputFormat)) {
			return setup;
		} else {
		
			// Layout FLOW as PEF

			// Customize which parameters are sent to the PEFMediaWriter, as it
			// outputs all parameters for future reference
			// System file paths should be concealed for security reasons
			p2.remove(SystemKeys.INPUT);
			p2.remove(SystemKeys.INPUT_URI);
			p2.remove("output");
			p2.remove(SystemKeys.TEMP_FILES_DIRECTORY);

			String translatorMode;
			PagedMediaWriter paged;

			if (SystemKeys.PEF_FORMAT.equals(outputFormat)) {
				// additional braille modes here...
				translatorMode = BrailleTranslatorFactory.MODE_UNCONTRACTED;
				paged = new PEFMediaWriter(p2);
			} else {
				translatorMode = BrailleTranslatorFactory.MODE_BYPASS;
				paged = new TextMediaWriter("UTF-8");
			}
			// BrailleTranslator bt =
			// BrailleTranslatorFactoryMaker.newInstance().newTranslator(context,
			// translatorMode);
			setup.add(new LayoutEngineTask("OBFL to " + outputFormat.toUpperCase() + " converter", context, translatorMode, paged));

			return setup;
		}
	}

}