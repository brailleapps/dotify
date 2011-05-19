package org.daisy.dotify.formatter;

import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;



/**
 * BlockStruct is a pull interface for the first step of the layout process
 * @author Joel Håkansson
 */
public interface BlockStruct extends Iterable<BlockSequence> {
	
	public LayoutMaster getLayoutMaster(String name);

}