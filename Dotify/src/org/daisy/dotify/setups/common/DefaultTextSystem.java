package org.daisy.dotify.setups.common;

import java.util.ArrayList;
import java.util.HashMap;

import org.daisy.dotify.obfl.ObflResourceLocator;
import org.daisy.dotify.obfl.ObflResourceLocator.ObflResourceIdentifier;
import org.daisy.dotify.system.InputManager;
import org.daisy.dotify.system.InputManagerFactoryMaker;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.LayoutEngineTask;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.system.XsltTask;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.daisy.dotify.writer.TextMediaWriter;


/**
 * <p>Transforms a DTBook 2005-3 into text format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * @author Joel Håkansson, TPB
 *
 */
public class DefaultTextSystem implements TaskSystem {
	private final String name;
	private final FilterLocale context;
	
	public DefaultTextSystem(String name, FilterLocale context) {
		this.name = name;
		this.context = context;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(RunParameters p) throws TaskSystemException {

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		//InputDetector
		InputManager idts = InputManagerFactoryMaker.newInstance().newInputManager(context);
		setup.addAll(idts.compile(p));
		{
			// Whitespace normalizer TransformerFactoryConstants.SAXON8
			HashMap<String, Object> h = new HashMap<String, Object>();
			for (Object key : p.getKeys()) {
				h.put(key.toString(), p.getProperty(key));
			}
			setup.add(new XsltTask("OBFL whitespace normalizer",
									ObflResourceLocator.getInstance().getResourceByIdentifier(ObflResourceIdentifier.OBFL_WHITESPACE_NORMALIZER_XSLT), 
									null,
									h));
		}
		// Layout FLOW as text
		BrailleTranslator bt;
		try {
			bt = BrailleTranslatorFactoryMaker.newInstance().newBrailleTranslator(context, BrailleTranslatorFactory.MODE_BYPASS);
		} catch (UnsupportedSpecificationException e) {
			throw new TaskSystemException(e);
		}

		TextMediaWriter paged = new TextMediaWriter("UTF-8");
		setup.add(new LayoutEngineTask("OBFL to Text converter", bt, paged));

		return setup;
	}

}