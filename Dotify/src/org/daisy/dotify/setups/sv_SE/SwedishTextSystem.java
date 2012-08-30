package org.daisy.dotify.setups.sv_SE;

import java.util.ArrayList;

import org.daisy.dotify.formatter.writers.TextMediaWriter;
import org.daisy.dotify.system.InternalTask;
import org.daisy.dotify.system.LayoutEngineTask;
import org.daisy.dotify.system.RunParameters;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.translator.UnsupportedSpecificationException;


/**
 * <p>Transforms a DTBook 2005-3 into text format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * @author Joel Håkansson, TPB
 *
 */
public class SwedishTextSystem implements TaskSystem {
	private final String name;
	
	public SwedishTextSystem(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(RunParameters p) throws TaskSystemException {

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();

		// Layout FLOW as text
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		BrailleTranslator bt;
		try {
			bt = BrailleTranslatorFactoryMaker.newInstance().newBrailleTranslator(sv_SE, BrailleTranslatorFactory.MODE_BYPASS);
		} catch (UnsupportedSpecificationException e1) {
			throw new TaskSystemException(e1);
		}
		TextMediaWriter paged = new TextMediaWriter(p.getProperties(), "UTF-8");
		setup.add(new LayoutEngineTask("OBFL to Text converter", bt, paged));

		return setup;
	}

}