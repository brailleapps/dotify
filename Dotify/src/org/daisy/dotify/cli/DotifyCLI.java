package org.daisy.dotify.cli;

import org.daisy.braille.ui.BasicUI;

public class DotifyCLI extends BasicUI {

	public DotifyCLI(String[] args) {
		super(args);
		putCommand("convert", "Converts a document into braille with Dotify", Main.class);
	}
	
	@Override
	public String getName() {
		return "dotify";
	}
	
	@Override
	public String getDescription() {
		return "Provides translation and formatting of documents into braille as well as tools for managing PEF-files.";
	}
	
	public static void main(String[] args) throws Exception {
		DotifyCLI ui = new DotifyCLI(args);
		ui.run();
	}

}
